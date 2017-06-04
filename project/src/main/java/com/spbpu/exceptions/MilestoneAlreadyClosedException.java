package com.spbpu.exceptions;

/**
 * Created by kivi on 04.06.17.
 */
public class MilestoneAlreadyClosedException extends Exception {
    public MilestoneAlreadyClosedException() {
        super("Milestone already closed");
    }
}
