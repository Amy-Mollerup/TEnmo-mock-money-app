package com.techelevator.tenmo.dao;

import com.techelevator.tenmo.exceptions.TransferAmountZeroOrLessException;
import com.techelevator.tenmo.model.Transfer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;

import javax.xml.crypto.Data;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;


@Component
public class JdbcTransferDao implements TransferDao {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    public JdbcTransferDao(JdbcTemplate jdbcTemplate) { this.jdbcTemplate = jdbcTemplate; }

    @Override
    public List<Transfer> findAll() {
        List<Transfer> transfers = new ArrayList<>();
        String sql = "SELECT * FROM transfer;";
        SqlRowSet results = jdbcTemplate.queryForRowSet(sql);
        while(results.next()) {
            Transfer transfer = mapRowToTransfer(results);
            transfers.add(transfer);
        }
        return transfers;
    }

    @Override
    //we have a method in account that finds by userId
    public List<Transfer> findByAccountId(Long accountId) {
        List<Transfer> transfers = new ArrayList<>();
        String sql = "SELECT * FROM transfer WHERE account_from ILIKE ? OR account_to ILIKE ?;";
        SqlRowSet results = jdbcTemplate.queryForRowSet(sql, accountId, accountId);
        while(results.next()) {
            Transfer transfer = mapRowToTransfer(results);
            transfers.add(transfer);
        }
        return transfers;
    }

    @Override
    public Transfer findByTransferId(Long id) throws Exception {
        String sql = "SELECT * FROM transfer WHERE transfer_id = ?";
        SqlRowSet results = jdbcTemplate.queryForRowSet(sql, id);
        if (results.next()) {
            return mapRowToTransfer(results);
        }
        throw new Exception("Transfer ID not found");
        // TODO create custom Exception
    }

    @Override //sending actual transfer to user
    public boolean createSendTransfer(Long accountFrom, Long accountTo, BigDecimal amount) {
        // TODO update balances to reflect the amount transferred
        String sql = "INSERT INTO transfer (transfer_type_id, transfer_status_id, account_from, account_to, amount) " +
                "VALUES (1, 2, ?, ?)";
        try {
            jdbcTemplate.queryForRowSet(sql, accountFrom, accountTo);
        } catch (TransferAmountZeroOrLessException e) {
            System.out.println(e.getMessage());
            return false;
        } catch (DataAccessException e) {
            System.out.println("Error accessing data");
            return false;
        }
        return true;
    }

    @Override //setting up our transfer requests from one user to another
    public boolean createRequestTransfer(Long accountFrom, Long accountTo, BigDecimal amount) {
        // TODO update balances to reflect the amount transferred
        String sql = "INSERT INTO transfer (transfer_type_id, transfer_status_id, account_from, account_to, amount) " +
                "VALUES (2, 1, ?, ?)";
        try {
            jdbcTemplate.queryForRowSet(sql, accountFrom, accountTo);
        } catch (TransferAmountZeroOrLessException e) {
            System.out.println(e.getMessage());
            return false;
        } catch(DataAccessException e) {
            System.out.println("Error accessing data");
            return false;
        }
        return true;
    }

    @Override
    public boolean updateTransferStatus(Long transferId, int transferStatusId) {
        String sql = "UPDATE transfer SET transfer_status_id = ?" +
                "WHERE transfer_id = ?;";
        try {
            jdbcTemplate.queryForRowSet(sql, transferStatusId, transferId);
        } catch (DataAccessException e) {
            System.out.println("Error accessing data");
            return false;
        }
        return true;
    }

    private Transfer mapRowToTransfer(SqlRowSet results) {
        Transfer transfer = new Transfer();
        transfer.setId(results.getLong("transfer_id"));
        transfer.setTransferTypeId(results.getInt("transfer_type_id"));
        transfer.setAccountFrom(results.getLong("account_from"));
        transfer.setAccountTo(results.getLong("account_to"));
        return transfer;
    }

}
