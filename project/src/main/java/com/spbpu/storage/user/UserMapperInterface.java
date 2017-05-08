package com.spbpu.storage.user;

import com.spbpu.storage.Mapper;

import java.sql.SQLException;
import java.util.List;

/**
 * Created by kivi on 08.05.17.
 */
public interface UserMapperInterface<T> extends Mapper<T> {
    List<String> getMessagesForUser(int id) throws SQLException;
    T findByLogin(String login) throws SQLException;
}