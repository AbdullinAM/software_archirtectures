/**
 * Created by kivi on 04.06.17.
 */

package com.spbpu.exceptions;

public class UserNotFoundException extends Exception {
    public UserNotFoundException(String user) {
        super("User " + user + " not found");
    }
}
