package com.techelevator.tenmo.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.BAD_REQUEST, reason = "Transfer ID does not exist" )
public class TransferIdDoesNotExistException extends RuntimeException{

    public TransferIdDoesNotExistException(String message) {
        super("Transfer ID does not exist");
    }
}
