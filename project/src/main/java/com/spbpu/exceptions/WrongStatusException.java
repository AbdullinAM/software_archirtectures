package com.spbpu.exceptions;

/**
 * Created by kivi on 04.06.17.
 */
public class WrongStatusException extends Exception {
    public WrongStatusException(String prev, String next) {
        super("Cannot change status from " + prev + " to " + next);
    }
}
