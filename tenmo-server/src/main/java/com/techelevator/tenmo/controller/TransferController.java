package com.techelevator.tenmo.controller;

import com.techelevator.tenmo.dao.AccountDao;
import com.techelevator.tenmo.dao.TransferDao;
import com.techelevator.tenmo.model.Transfer;
import org.apache.commons.logging.Log;
import com.techelevator.util.BasicLogger;
import org.jboss.logging.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClientResponseException;

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

    @GetMapping //get transfer by id
    public Transfer getTransferById(@PathVariable Long id) throws Exception {
       Transfer transferByID = transferDao.findByTransferId(id);
       return transferByID;
    }
    @GetMapping //get transfer by type (needs to be coming from the logged in user to view all the types of transfers they've made so far)


    @PostMapping
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

    @PostMapping
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





}
