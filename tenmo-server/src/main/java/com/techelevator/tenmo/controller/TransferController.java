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

import java.util.ArrayList;
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

    @PostMapping("/send")
    //works in postman - does not return transfer id
    public boolean createSendTransfer(@RequestBody Transfer transfer) {
        boolean success = false;
        try {
            transferDao.createSendTransfer(transfer.getAccountFrom(), transfer.getAccountTo(), transfer.getAmount());
            success = true;
        } catch (RestClientResponseException e) {
            BasicLogger.log(e.getRawStatusCode() + " : " + e.getStatusText());
        }
        catch (ResourceAccessException e) {
            BasicLogger.log(e.getMessage());
        }
        return success;
    }


    @PostMapping("/request")
    //works in postman, I think
    public boolean createRequestTransfer(@RequestBody Transfer transfer) {
        boolean success = false;
        try {
            transferDao.createRequestTransfer(transfer.getAccountFrom(), transfer.getAccountTo(), transfer.getAmount());
            success = true;
        } catch (RestClientResponseException e) {
            BasicLogger.log(e.getRawStatusCode() + " : " + e.getStatusText());
        }
        catch (ResourceAccessException e) {
            BasicLogger.log(e.getMessage());
        }
        return success;
    }

    @PutMapping("/update/{id}")
    //client side -- add logic for transferStatusId input
    public boolean updateTransferStatus(@RequestParam int transferStatusId, @PathVariable Long transferId) throws Exception {
        boolean success = false;
        Transfer transfer = transferDao.findByTransferId(transferId);
        try {
            accountDao.withdraw(transfer.getAccountFrom(), transfer.getAmount());
            //if withdraw fails, we need to update the status to rejected
            accountDao.deposit(transfer.getAccountTo(), transfer.getAmount());
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
