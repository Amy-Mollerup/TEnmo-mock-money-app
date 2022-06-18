package com.techelevator.tenmo.dao;

import com.techelevator.tenmo.exceptions.TransferAmountZeroOrLessException;
import com.techelevator.tenmo.exceptions.TransferIdDoesNotExistException;
import com.techelevator.tenmo.model.Transfer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.security.core.parameters.P;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
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
    // TODO check exception handling
    public List<Transfer> findByAccountId(Long accountId) {
        List<Transfer> transfers = new ArrayList<>();
        String sql = "SELECT * FROM transfer WHERE account_from = ? OR account_to = ?;";
        SqlRowSet results = jdbcTemplate.queryForRowSet(sql, accountId, accountId);
        while(results.next()) {
            Transfer transfer = mapRowToTransfer(results);
            transfers.add(transfer);
        }
        return transfers;
    }

    @Override // locating transfer by id
    // TODO Check exception handling
    public Transfer findByTransferId(Long id) {
        Transfer transfer = null;
        String sql = "SELECT * FROM transfer WHERE transfer_id = ?";
        SqlRowSet results = jdbcTemplate.queryForRowSet(sql, id);
        if (results.next()) {
             transfer = mapRowToTransfer(results);
        }
        return transfer;
    }


    @Override // sending actual transfer to user
    // TODO Check exception handling
    public boolean createTransfer(Transfer transfer) {
        String sql = "INSERT INTO transfer (transfer_type_id, transfer_status_id, account_from, account_to, amount) " +
                "VALUES (?, ?, ?, ?, ?)";
        boolean success = false;
        try {
            jdbcTemplate.update(sql, transfer.getTransferTypeId(), transfer.getTransferStatusId(),
                    transfer.getAccountFrom(), transfer.getAccountTo(), transfer.getAmount());
            if(transfer.getTransferTypeId() == 2) {
                executeTransfer(transfer);
            }
            success = true;
        } catch (TransferAmountZeroOrLessException e) {
            System.out.println(e.getMessage());
        } catch (DataAccessException e) {
            System.out.println("Error accessing data");
        }
        return success;
    }

    @Override
    // TODO Check exception handling
    public boolean updateTransferStatus(Transfer transfer) {
        String sql = "UPDATE transfer SET transfer_status_id = ?" +
                "WHERE transfer_id = ?;";
        boolean success = false;
        try {
            jdbcTemplate.update(sql, transfer.getTransferStatusId(), transfer.getTransferId());
            if (transfer.getTransferStatusId() == 2) {
                executeTransfer(transfer);
            }
            success = true;
        } catch (DataAccessException e) {
            System.out.println("Error accessing data");
        }
        return success;
    }

    public void executeTransfer(Transfer transfer) {
        accountDao.withdraw(transfer.getAccountFrom(), transfer.getAmount());
        accountDao.deposit(transfer.getAccountTo(), transfer.getAmount());
    }


    private Transfer mapRowToTransfer(SqlRowSet results) {
        Transfer transfer = new Transfer();
        transfer.setTransferId(results.getLong("transfer_id"));
        transfer.setTransferTypeId(results.getInt("transfer_type_id"));
        transfer.setTransferStatusId(results.getInt("transfer_status_id"));
        transfer.setAccountFrom(results.getLong("account_from"));
        transfer.setAccountTo(results.getLong("account_to"));
        transfer.setAmount(results.getBigDecimal("amount"));
        return transfer;
    }

//    @Override
//    public List<Transfer> findPendingByAccountId(Long accountId) {
//        List<Transfer> pendingTransfers = new ArrayList<>();
//        String sql = "SELECT * FROM transfer WHERE account_from = ? AND transfer_status_id = 1;";
//        SqlRowSet results = jdbcTemplate.queryForRowSet(sql, accountId);
//        while(results.next()) {
//            Transfer transfer = mapRowToTransfer(results);
//            pendingTransfers.add(transfer);
//        }
//        return pendingTransfers;
//
//    }

//    We thought about doing this, not sure if it's worth the effort, may revisit later.
//    @Override
//    public Transfer executeTransfer(long accountFromId, long accountToId, BigDecimal amount) {
//
//        String sql = "BEGIN TRANSACTION; " +
//                "UPDATE account SET balance = ? " +
//                "WHERE account_id = ?;  " +
//                "UPDATE account SET balance = ? " +
//                "WHERE account_id = ?; " +
//                "COMMIT;";
//
//    }

//    @Override
//    public List<Transfer> findAll() {
//        List<Transfer> transfers = new ArrayList<>();
//        String sql = "SELECT * FROM transfer;";
//        SqlRowSet results = jdbcTemplate.queryForRowSet(sql);
//        while(results.next()) {
//            Transfer transfer = mapRowToTransfer(results);
//            transfers.add(transfer);
//        }
//        return transfers;
//    }


    //    @Override //retrieves transfer status description
//    public String getTransferStatus(Long id) throws TransferIdDoesNotExistException {
//        String transferStatus = null;
//        String sql = "SELECT transfer_type_desc FROM transfer_type " +
//                    "JOIN transfer ON transfer_type.transfer_type_id = transfer.transfer_type_id " +
//                    "WHERE transfer_id = ?;";
//        try {
//            transferStatus = String.valueOf(jdbcTemplate.queryForRowSet(sql, id));
//        } catch (TransferIdDoesNotExistException e) {
//            System.out.println(e.getMessage());
//        } catch (DataAccessException e ) {
//            System.out.println("Error accessing data");
//        }
//        return transferStatus;
//
//    }


//    @Override //setting up our transfer requests from one user to another
//    public boolean createRequestTransfer(Long accountFrom, Long accountTo, BigDecimal amount) {
//        String sql = "INSERT INTO transfer (transfer_type_id, transfer_status_id, account_from, account_to, amount) " +
//                "VALUES (1, 1, ?, ?, ?)";
//        boolean success = false;
//        try {
//            jdbcTemplate.update(sql, accountFrom, accountTo, amount);
//            success = true;
//        } catch (TransferAmountZeroOrLessException e) {
//            System.out.println(e.getMessage());
//        } catch(DataAccessException e) {
//            System.out.println("Error accessing data");
//        }
//        return success;
//    }

}
