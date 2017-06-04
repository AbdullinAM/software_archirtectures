/**
 * Created by kivi on 21.05.17.
 */

package com.spbpu.exceptions;

public class UserAlreadyExistsException extends Exception {
    public UserAlreadyExistsException(String user) {
        super("User " + user + " already exists");
    }
}

