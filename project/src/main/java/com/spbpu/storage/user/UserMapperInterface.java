package com.spbpu.storage.user;

import com.spbpu.exceptions.EndBeforeStartException;
import com.spbpu.exceptions.UserAlreadyHasRoleException;
import com.spbpu.storage.Mapper;

import java.sql.SQLException;
import java.util.List;

/**
 * Created by kivi on 08.05.17.
 */
public interface UserMapperInterface<T> extends Mapper<T> {
    T findByLogin(String login) throws SQLException, EndBeforeStartException, UserAlreadyHasRoleException;
}
