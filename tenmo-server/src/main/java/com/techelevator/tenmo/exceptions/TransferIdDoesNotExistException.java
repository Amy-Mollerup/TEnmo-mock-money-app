package com.techelevator.tenmo.exceptions;

import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.BAD_REQUEST, reason = "Transfer ID does not exist" )
public class TransferIdDoesNotExistException extends DataAccessException {

    public TransferIdDoesNotExistException() {
        super("Transfer ID does not exist");
    }
}
