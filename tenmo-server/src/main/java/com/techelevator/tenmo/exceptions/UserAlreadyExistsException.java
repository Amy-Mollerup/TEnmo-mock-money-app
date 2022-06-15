package com.techelevator.tenmo.exceptions;


import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.concurrent.ExecutionException;

@ResponseStatus(value = HttpStatus.BAD_REQUEST, reason = "User already exists in the system.")
public class UserAlreadyExistsException extends ExecutionException { //wanted to make this a checked exception vs runtime

    public UserAlreadyExistsException(String message) {
        super ("User already exists in the system.");
    }
}
