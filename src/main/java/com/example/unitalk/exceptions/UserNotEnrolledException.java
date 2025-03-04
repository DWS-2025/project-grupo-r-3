package com.example.unitalk.exceptions;

public class UserNotEnrolledException extends RuntimeException {
    public UserNotEnrolledException(String message) {
        super(message);
    }
}
