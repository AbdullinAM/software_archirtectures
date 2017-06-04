/**
 * Created by kivi on 11.05.17.
 */

package com.spbpu.storage.user;

import com.spbpu.exceptions.EndBeforeStartException;
import com.spbpu.exceptions.NoRightsException;
import com.spbpu.exceptions.UserAlreadyHasRoleException;
import com.spbpu.project.Project;
import com.spbpu.storage.DataGateway;
import com.spbpu.storage.project.ProjectMapper;
import com.spbpu.user.Manager;
import com.spbpu.user.User;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ManagerMapper implements UserMapperInterface<Manager> {

    private static Set<Manager> managers = new HashSet<>();
    private Connection connection;
    private UserMapper userMapper;
    private ProjectMapper projectMapper;

    public ManagerMapper() throws IOException, SQLException {
        connection = DataGateway.getInstance().getDataSource().getConnection();
        userMapper = new UserMapper();
        projectMapper = new ProjectMapper(this);
    }

    @Override
    public Manager findByLogin(String login) throws SQLException, EndBeforeStartException {
        for (Manager it : managers)
            if (it.getLogin().equals(login)) return it;

        User user = userMapper.findByLogin(login);
        if (user == null) return null;
        Manager manager = new Manager(user);
        managers.add(manager);

        /// Add manager projects
        for (Project it : projectMapper.findAllManagerProjects(manager)) {
            try {
                manager.addProject(it);
            } catch (NoRightsException e) {
                e.printStackTrace();
            }
        }
        return manager;
    }

    @Override
    public Manager findByID(int id) throws SQLException, EndBeforeStartException {
        for (Manager it : managers)
            if (it.getId() == id) return it;

        User user = userMapper.findByID(id);
        if (user == null) return null;
        Manager manager = new Manager(user);
        managers.add(manager);

        /// Add manager projects
        for (Project it : projectMapper.findAllManagerProjects(manager)) {
            try {
                manager.addProject(it);
            } catch (NoRightsException e) {
                e.printStackTrace();
            }
        }

        return manager;
    }

    @Override
    public List<Manager> findAll() throws SQLException {
        managers.clear();
        List<Manager> all = new ArrayList<>();

        for (User it : userMapper.findAll()) {
            Manager manager = new Manager(it);
            all.add(manager);
            managers.add(manager);
        }
        return all;
    }

    @Override
    public void update(Manager item) throws SQLException {
        if (!managers.contains(item)) {
            userMapper.update(item);
            managers.add(item);
        }

        for (Project it : item.getProjects()) {
            projectMapper.update(it);
        }
    }

    @Override
    public void closeConnection() throws SQLException {
        userMapper.closeConnection();
        projectMapper.closeConnection();
        connection.close();
    }

    @Override
    public void clear() {
        projectMapper.clear();
        userMapper.clear();
        managers.clear();
    }

    @Override
    public void update() throws SQLException {
        projectMapper.update();
        userMapper.update();
        for (Manager it : managers)
            update(it);
    }
}
