package com.techelevator.tenmo.dao;

import com.techelevator.tenmo.model.Transfer;

import java.math.BigDecimal;
import java.util.List;

public interface TransferDao {

//    UNUSED
//    List<Transfer> findAll();

    List<Transfer> findByAccountId(Long accountId);

    Transfer findByTransferId(Long id);

    boolean createTransfer(Transfer transfer);

//    UNUSED
//    boolean createRequestTransfer(Long accountFrom, Long accountTo, BigDecimal amount);

    boolean updateTransferStatus(Long transferId, int transferStatusId);

//    UNUSED
//    String getTransferStatus(Long transferId);

    List<Transfer> findPendingByAccountId(Long accountId); //account from id
}
