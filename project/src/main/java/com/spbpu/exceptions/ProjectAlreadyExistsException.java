/**
 * Created by kivi on 04.06.17.
 */

package com.spbpu.exceptions;

public class ProjectAlreadyExistsException extends Exception {
    public ProjectAlreadyExistsException(String project) {
        super("Project " + project + " already exists");
    }
}
