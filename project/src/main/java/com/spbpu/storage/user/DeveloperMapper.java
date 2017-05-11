/**
 * Created by kivi on 11.05.17.
 */

package com.spbpu.storage.user;

import com.spbpu.storage.DataGateway;
import com.spbpu.storage.project.ProjectMapper;
import com.spbpu.user.Developer;
import com.spbpu.user.Manager;
import com.spbpu.user.User;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class DeveloperMapper implements UserMapperInterface<Developer> {

    private static Set<Developer> developers = new HashSet<>();
    private Connection connection;
    private UserMapper userMapper;
    private ProjectMapper projectMapper;

    DeveloperMapper() throws IOException, SQLException {
        connection = DataGateway.getInstance().getDataSource().getConnection();
        userMapper = new UserMapper();
        projectMapper = new ProjectMapper();
    }

    @Override
    public Developer findByLogin(String login) throws SQLException {
        return null;
    }

    @Override
    public Developer findByID(int id) throws SQLException {
        return null;
    }

    @Override
    public List<Developer> findAll() throws SQLException {
        return null;
    }

    @Override
    public void update(Developer item) throws SQLException {

    }

    @Override
    public void closeConnection() throws SQLException {
        userMapper.closeConnection();
        projectMapper.closeConnection();
        closeConnection();
    }

}
