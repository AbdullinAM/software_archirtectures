package com.spbpu.storage;

import com.spbpu.exceptions.EndBeforeStartException;
import com.spbpu.exceptions.UserAlreadyHasRoleException;

import java.sql.SQLException;
import java.util.List;

/**
 * Created by kivi on 08.05.17.
 */
public interface Mapper<T> {
    T findByID(int id) throws SQLException, EndBeforeStartException, UserAlreadyHasRoleException;
    List<T> findAll() throws SQLException, EndBeforeStartException;
    void update(T item) throws SQLException;
    void update() throws SQLException;
    void closeConnection() throws SQLException;
    void clear();
}

