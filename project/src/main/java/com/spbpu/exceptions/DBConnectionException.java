/**
 * Created by kivi on 04.06.17.
 */

package com.spbpu.exceptions;

public class DBConnectionException extends Exception {
    public DBConnectionException() {
        super("Could not connect to database");
    }
}
