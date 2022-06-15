package com.techelevator.tenmo.exceptions;


import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.NOT_ACCEPTABLE, reason = "Transfer amount must be greater than 0")
public class TransferAmountZeroOrLessException extends RuntimeException{

    public TransferAmountZeroOrLessException() {
        super("Transfer amount must be greater than 0");
    }
}
