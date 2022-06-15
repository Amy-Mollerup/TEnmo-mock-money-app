package com.techelevator.tenmo.security;



public class UserNotAuthorizedException extends Exception {
    //still doing research on how to utilize exception handling for authorization checks
    //servlet and filters?
    //user.isAuthorized?

     public UserNotAuthorizedException(String message) {
         super(message);
     }


}
