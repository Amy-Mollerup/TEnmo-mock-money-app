package com.techelevator.tenmo.security;

public class TransferAmountZeroOrLessException extends Exception{

    public TransferAmountZeroOrLessException(String message) {
        super(message);
    }
}
