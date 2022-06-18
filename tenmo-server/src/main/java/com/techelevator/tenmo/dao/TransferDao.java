package com.techelevator.tenmo.dao;

import com.techelevator.tenmo.model.Transfer;
import com.techelevator.tenmo.model.TransferDto;
import com.techelevator.tenmo.model.TransferDTO;

import java.util.List;

public interface TransferDao {

    List<Transfer> findByAccountId(Long accountId);

    TransferDTO findByTransferId(Long id);

    boolean createTransfer(TransferDTO transferDTO);

    boolean updateTransferStatus(TransferDTO transferDTO);

    Transfer convertFromDTO(Long transferId);

}
