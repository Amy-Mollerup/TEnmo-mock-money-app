package com.techelevator.tenmo.dao;

import com.techelevator.tenmo.model.Transfer;

import java.math.BigDecimal;
import java.util.List;

public interface TransferDao {

    List<Transfer> findAll();

    List<Transfer> findByAccountId(Long accountId);

    Transfer findByTransferId(Long id) throws Exception;

    boolean createSendTransfer(Long accountFrom, Long accountTo, BigDecimal amount);

    boolean createRequestTransfer(Long accountFrom, Long accountTo, BigDecimal amount);

    boolean updateTransferStatus(Long transferId, int transferStatusId);

    String getTransferStatus(Long transferId);
}
