package com.techelevator.tenmo.security;

public class BalanceNotZeroException extends Exception{

    public BalanceNotZeroException(String message) {
    super(message);
    }
}
