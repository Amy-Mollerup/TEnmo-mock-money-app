package com.techelevator.tenmo.dao;

import com.techelevator.tenmo.exceptions.InsufficentFundsException;
import com.techelevator.tenmo.exceptions.TransferIdDoesNotExistException;
import com.techelevator.tenmo.model.Transfer;
import com.techelevator.tenmo.model.TransferDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;


@Component
public class JdbcTransferDao implements TransferDao {

    @Autowired
    private JdbcTemplate jdbcTemplate;
    private AccountDao accountDao;

    public JdbcTransferDao(JdbcTemplate jdbcTemplate, AccountDao accountDao) {
        this.jdbcTemplate = jdbcTemplate;
        this.accountDao = accountDao;
    }

    @Override
    public List<Transfer> findByAccountId(Long accountId) {
        List<Transfer> transfers = new ArrayList<>();
        String sql = "SELECT transfer_id, transfer_type_desc, transfer_status_desc, user_from.username AS user_from, user_to.username AS user_to, amount " +
                "FROM transfer " +
                "NATURAL JOIN transfer_type " +
                "NATURAL JOIN transfer_status " +
                "JOIN account a ON account_from = a.account_id " +
                "JOIN account b ON account_to = b.account_id " +
                "JOIN tenmo_user AS user_from ON a.user_id = user_from.user_id " +
                "JOIN tenmo_user AS user_to ON b.user_id = user_to.user_id " +
                "WHERE account_from = ? OR account_to = ?;";
        SqlRowSet results = jdbcTemplate.queryForRowSet(sql, accountId, accountId);
        while(results.next()) {
            Transfer transfer = mapRowToTransfer(results);
            transfers.add(transfer);
        }
        return transfers;
    }

    @Override // locating transfer by id
    public TransferDTO findByTransferId(Long id) {
        TransferDTO transferDTO = null;
        String sql = "SELECT * FROM transfer WHERE transfer_id = ?";
        SqlRowSet results = jdbcTemplate.queryForRowSet(sql, id);
        if (results.next()) {
             transferDTO = mapRowToTransferDTO(results);
        } else {
            throw new TransferIdDoesNotExistException();
        }
        return transferDTO;
    }


    @Override // sending actual transfer to user
    public boolean createTransfer(TransferDTO transferDTO) {
        String sql = "INSERT INTO transfer (transfer_type_id, transfer_status_id, account_from, account_to, amount) " +
                "VALUES (?, ?, ?, ?, ?)";
        boolean success = false;
        try {
            jdbcTemplate.update(sql, transferDTO.getTransferTypeId(), transferDTO.getTransferStatusId(),
                    transferDTO.getAccountFrom(), transferDTO.getAccountTo(), transferDTO.getAmount());
            if (transferDTO.getTransferTypeId() == 2) {
                executeTransfer(transferDTO);
            }
            success = true;
        } catch (DataAccessException e) { //generic family of runtime exceptions
            throw new InsufficentFundsException(e.getMessage());
        }
        return success;
    }

    @Override
    public boolean updateTransferStatus(TransferDTO transferDTO) {
        int lines = 0;
        String sql = "UPDATE transfer SET transfer_status_id = ?" +
                "WHERE transfer_id = ?;";
        boolean success = false;
        lines = jdbcTemplate.update(sql, transferDTO.getTransferStatusId(), transferDTO.getTransferId());
        if(lines == 0) {
            throw new TransferIdDoesNotExistException();
        }
        if (transferDTO.getTransferStatusId() == 2) {
            executeTransfer(transferDTO);
            success = true;
        } else if (transferDTO.getTransferStatusId() == 3) {
            success = true;
        }
        return success;
    }

    @Override
    public Transfer convertFromDTO(Long transferId) {
        Transfer transfer = null;
        String sql = "SELECT transfer_id, transfer_type_desc, transfer_status_desc, user_from.username AS user_from, user_to.username AS user_to, amount " +
                "FROM transfer " +
                "NATURAL JOIN transfer_type " +
                "NATURAL JOIN transfer_status " +
                "JOIN account a ON account_from = a.account_id " +
                "JOIN account b ON account_to = b.account_id " +
                "JOIN tenmo_user AS user_from ON a.user_id = user_from.user_id " +
                "JOIN tenmo_user AS user_to ON b.user_id = user_to.user_id " +
                "WHERE transfer_id = ?;";
        SqlRowSet results = jdbcTemplate.queryForRowSet(sql, transferId);
        if (results.next()) {
            transfer = mapRowToTransfer(results);
        }

        return transfer;

    }

    private Transfer mapRowToTransfer(SqlRowSet results) {
        Transfer transfer = new Transfer();
        transfer.setTransferId(results.getLong("transfer_id"));
        transfer.setTransferType(results.getString("transfer_type_desc"));
        transfer.setTransferStatus(results.getString("transfer_status_desc"));
        transfer.setAccountFrom(results.getString("user_from"));
        transfer.setAccountTo(results.getString("user_to"));
        transfer.setAmount(results.getBigDecimal("amount"));
        return transfer;
    }

    public void executeTransfer(TransferDTO transferDTO) {
            accountDao.withdraw(transferDTO.getAccountFrom(), transferDTO.getAmount());
            accountDao.deposit(transferDTO.getAccountTo(), transferDTO.getAmount());
    }


    private TransferDTO mapRowToTransferDTO(SqlRowSet results) {
        TransferDTO transferDTO = new TransferDTO();
        transferDTO.setTransferId(results.getLong("transfer_id"));
        transferDTO.setTransferTypeId(results.getInt("transfer_type_id"));
        transferDTO.setTransferStatusId(results.getInt("transfer_status_id"));
        transferDTO.setAccountFrom(results.getLong("account_from"));
        transferDTO.setAccountTo(results.getLong("account_to"));
        transferDTO.setAmount(results.getBigDecimal("amount"));
        return transferDTO;
    }

}
