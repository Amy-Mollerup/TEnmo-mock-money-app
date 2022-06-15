package com.techelevator.tenmo.controller;

import com.techelevator.tenmo.dao.AccountDao;
import com.techelevator.tenmo.dao.TransferDao;
import com.techelevator.tenmo.exceptions.TransferIdDoesNotExistException;
import com.techelevator.tenmo.model.Transfer;
import org.apache.commons.logging.Log;
import com.techelevator.util.BasicLogger;
import org.jboss.logging.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
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
    public boolean updateTransferStatus(@RequestParam int transferStatusId, @PathVariable Long transferId) throws TransferIdDoesNotExistException {
        boolean success = false;
        try {
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
