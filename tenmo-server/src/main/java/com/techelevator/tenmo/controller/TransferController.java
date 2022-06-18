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
    public List<Transfer> getTransferByAccountId(@PathVariable Long id) {
        return transferDao.findByAccountId(id);
    }

//    @GetMapping("pending/{accountId}")
//    public List<Transfer> getPendingTransfersByAccountId(@PathVariable Long accountId) {
//        return transferDao.findPendingByAccountId(accountId);
//    }

    @GetMapping("/{id}")//get transfer by id
    public Transfer getTransferById(@PathVariable Long id) {
        return transferDao.findByTransferId(id);
    }

    @PostMapping()
    public boolean createTransfer(@RequestBody Transfer transfer) {
        return transferDao.createTransfer(transfer);
    }

    @PutMapping("/update/{transferId}")
    public boolean updateTransferStatus(@RequestBody Transfer transfer, @PathVariable Long transferId) {
        // TODO - does this need a Path Variable anymore?
            return transferDao.updateTransferStatus(transfer);
    }






}
