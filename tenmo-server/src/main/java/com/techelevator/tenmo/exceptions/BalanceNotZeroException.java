package com.techelevator.tenmo.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.NOT_ACCEPTABLE, reason = "Balance must be 0 to proceed")
public class BalanceNotZeroException extends RuntimeException{

    public BalanceNotZeroException(String message) {

        super("Balance must be 0 to proceed");
    }
}
