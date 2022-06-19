package com.techelevator.tenmo.exceptions;

import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.BAD_REQUEST, reason = "Insufficient funds" )
public class InsufficentFundsException extends DataAccessException {
    public InsufficentFundsException(String message) {
        super("Insufficient funds to complete transaction. Transaction amount cannot exceed available funds or be for less than or 0. " +
                "Please check your balance and try again.");
    }
}
