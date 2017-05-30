package com.spbpu.exceptions;

/**
 * Created by kivi on 30.05.17.
 */
public class EmailNotVerifiedException extends Exception {
    public EmailNotVerifiedException(String email) {
        super("email address not verified: " + email);
    }
}
