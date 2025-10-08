package com.example.datnbe.exception;

public class LoginInfoNotCorrect extends RuntimeException {
    public LoginInfoNotCorrect(String message) {
        super(message);
    }
}
