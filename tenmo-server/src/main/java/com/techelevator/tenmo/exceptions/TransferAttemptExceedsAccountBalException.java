package com.techelevator.tenmo.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

    @ResponseStatus(value = HttpStatus.NOT_ACCEPTABLE, reason = "Amount to transfer exceeds available funds")
    public class TransferAttemptExceedsAccountBalException extends RuntimeException { //might need to be checked exception

        public TransferAttemptExceedsAccountBalException() {
            super("Amount to transfer exceeds available funds");
        }
    }

