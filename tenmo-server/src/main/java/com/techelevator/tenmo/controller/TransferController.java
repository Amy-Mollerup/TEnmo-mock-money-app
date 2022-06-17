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

    @GetMapping("/{id}")//get transfer by id
    //sort of works?? Only returns literally the transfer id
    public Transfer getTransferById(@PathVariable Long id) throws TransferIdDoesNotExistException {
        Transfer transferByID = null;
        try {
            transferByID = transferDao.findByTransferId(id);
        } catch (RestClientResponseException e) {
            BasicLogger.log(e.getRawStatusCode() + " : " + e.getStatusText());
        } catch (Exception e) {
            BasicLogger.log(e.getMessage());
        }
        return transferByID;
    }

    @PostMapping()
    //works in postman - does not return transfer id
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
/*

    @PostMapping("/request")
    //works in postman, I think
    public boolean createRequestTransfer(@RequestBody String transfer) {
        String[] paramList = transfer.split(",");
        Long accountFrom = Long.valueOf(paramList[0]);
        Long accountTo = Long.valueOf(paramList[1]);
        BigDecimal amount = BigDecimal.valueOf(Double.parseDouble(paramList[2]));

        boolean success = false;
        try {
            transferDao.createRequestTransfer(accountFrom, accountTo, amount);
            success = true;
        } catch (RestClientResponseException e) {
            BasicLogger.log(e.getRawStatusCode() + " : " + e.getStatusText());
        }
        catch (ResourceAccessException e) {
            BasicLogger.log(e.getMessage());
        }
        return success;
    }*/

    @PutMapping("/update/{transferId}")
    //client side -- add logic for transferStatusId input
    public boolean updateTransferStatus(@RequestBody int transferStatusId, @PathVariable Long transferId) throws Exception {
        boolean success = false;
        Transfer transfer = transferDao.findByTransferId(transferId);

        try {
            if (transferStatusId == 2) {
                accountDao.withdraw(transfer.getAccountFrom(), transfer.getAmount());
                //if withdraw fails, we need to update the status to rejected
                accountDao.deposit(transfer.getAccountTo(), transfer.getAmount());
            }
            //if withdraw + deposit are both successful, update status accordingly
            transferDao.updateTransferStatus(transferId, transferStatusId);
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
