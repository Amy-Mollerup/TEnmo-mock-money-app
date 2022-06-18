package com.techelevator.tenmo.controller;

import com.techelevator.tenmo.dao.AccountDao;
import com.techelevator.tenmo.dao.TransferDao;
import com.techelevator.tenmo.exceptions.TransferIdDoesNotExistException;
import com.techelevator.tenmo.model.Transfer;
import com.techelevator.util.BasicLogger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClientResponseException;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/transfer")
@PreAuthorize("isAuthenticated()")
public class TransferController {

    @Autowired
    private TransferDao transferDao;
    private AccountDao accountDao;

    public TransferController(TransferDao transferDao, AccountDao accountDao) {
        this.accountDao = accountDao;
        this.transferDao = transferDao;
    }

    @GetMapping("/user/{id}")
    //works in postman
    public List<Transfer> getTransferByAccountId(@PathVariable Long id) {
        return transferDao.findByAccountId(id);
    }

    @GetMapping("pending/{accountId}")
    public List<Transfer> getPendingTransfersByAccountId(@PathVariable Long accountId) {
        return transferDao.findPendingByAccountId(accountId);
    }

    @GetMapping("/{id}")//get transfer by id
    public Transfer getTransferById(@PathVariable Long id) {
        return transferDao.findByTransferId(id);

//        Transfer transferByID = null;
//        try {
//            transferByID = transferDao.findByTransferId(id);
//        } catch (RestClientResponseException e) {
//            BasicLogger.log(e.getRawStatusCode() + " : " + e.getStatusText());
//        } catch (Exception e) {
//            BasicLogger.log(e.getMessage());
//        }
//        return transferByID;
    }

    @PostMapping()
    public boolean createTransfer(@RequestBody Transfer transfer) {
        Long accountFrom = transfer.getAccountFrom();
        Long accountTo = transfer.getAccountTo();
        BigDecimal amount = transfer.getAmount();

        boolean success = false;
        try {
            transferDao.createTransfer(transfer);
            accountDao.withdraw(accountFrom, amount);
            accountDao.deposit(accountTo, amount);
            success = true;
        } catch (RestClientResponseException e) {
            BasicLogger.log(e.getRawStatusCode() + " : " + e.getStatusText());
        }
        catch (ResourceAccessException e) {
            BasicLogger.log(e.getMessage());
        }
        return success;
    }

    @PutMapping("/update/{transferId}")
    public boolean updateTransferStatus(@RequestBody Transfer transfer, @PathVariable Long transferId) throws Exception {
        boolean success = false;

        try {
            if (transfer.getTransferStatusId() == 2) {
                accountDao.withdraw(transfer.getAccountFrom(), transfer.getAmount());
                accountDao.deposit(transfer.getAccountTo(), transfer.getAmount());
            }
            transferDao.updateTransferStatus(transferId, transfer.getTransferStatusId());
            success = true;
        }
        catch (RestClientResponseException e) {
            BasicLogger.log(e.getRawStatusCode() + " : " + e.getStatusText());
        }
        catch (ResourceAccessException e) {
            BasicLogger.log(e.getMessage());
        }
        return success;
    }






}
