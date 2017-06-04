/**
 * Created by kivi on 04.06.17.
 */

package com.spbpu.exceptions;

public class UserAlreadyHasRoleException extends Exception {
    public UserAlreadyHasRoleException(String user, String project) {
        super("User " + user + " already has role in project " + project);
    }
}
