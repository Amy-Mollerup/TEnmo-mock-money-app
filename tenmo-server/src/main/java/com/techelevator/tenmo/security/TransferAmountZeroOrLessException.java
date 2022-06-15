package com.techelevator.tenmo.security;

public class TransferAmountZeroOrLessException extends RuntimeException{

    public TransferAmountZeroOrLessException(String message) {
        super(message);
    }
}
