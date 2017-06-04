package com.spbpu.exceptions;

/**
 * Created by kivi on 04.06.17.
 */
public class MilestoneTicketsNotCLosedException extends Exception {
    public MilestoneTicketsNotCLosedException() {
        super("Cannot close milestone, because not all it's tickets are closed");
    }
}
