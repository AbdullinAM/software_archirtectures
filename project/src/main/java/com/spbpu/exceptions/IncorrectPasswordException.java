package com.spbpu.exceptions;

/**
 * Created by kivi on 04.06.17.
 */
public class IncorrectPasswordException extends Exception {
    public IncorrectPasswordException() {
        super("Incorrect login or password");
    }
}
