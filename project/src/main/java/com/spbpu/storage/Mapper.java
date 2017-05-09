package com.spbpu.storage;

import java.sql.SQLException;
import java.util.List;

/**
 * Created by kivi on 08.05.17.
 */
public interface Mapper<T> {
    T findByID(int id) throws SQLException;
    List<T> findAll() throws SQLException;
    void update(T item) throws SQLException;
    void closeConnection() throws SQLException;
}

