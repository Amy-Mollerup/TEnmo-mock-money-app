package com.techelevator.tenmo.security;


import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.FORBIDDEN, reason = "User Not Authorized")
public class UserNotAuthorizedException extends RuntimeException {
    //still doing research on how to utilize exception handling for authorization checks
    //servlet and filters?
    //user.isAuthorized?

     public UserNotAuthorizedException(String message) {
         super("User Not Authorized");
     }


}
