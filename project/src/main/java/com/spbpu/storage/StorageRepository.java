/**
 * Created by Azat on 26.03.2017.
 */
package com.spbpu.storage;

import com.spbpu.exceptions.AlreadyExistsException;
import com.spbpu.exceptions.EndBeforeStartException;
import com.spbpu.project.Milestone;
import com.spbpu.project.Project;
import com.spbpu.service.VerifyEmailService;
import com.spbpu.storage.project.ProjectMapper;
import com.spbpu.storage.user.*;
import com.spbpu.user.*;

import javax.jws.soap.SOAPBinding;
import java.io.IOException;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

public class StorageRepository {

    private static UserMapper userMapper;
    private static ManagerMapper managerMapper;
    private static TeamLeaderMapper teamLeaderMapper;
    private static DeveloperMapper developerMapper;
    private static TesterMapper testerMapper;
    private static ProjectMapper projectMapper;


    public StorageRepository() {
        try {
            if (userMapper == null) userMapper = new UserMapper();
            if (managerMapper == null) managerMapper = new ManagerMapper();
            if (projectMapper == null) projectMapper = new ProjectMapper(managerMapper);
            if (teamLeaderMapper == null) teamLeaderMapper = new TeamLeaderMapper(projectMapper);
            if (developerMapper == null) developerMapper = new DeveloperMapper(projectMapper);
            if (testerMapper == null) testerMapper = new TesterMapper(projectMapper);
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public boolean addUser(String login, String name, String email, String password) throws AlreadyExistsException {
        try {
            if (userMapper.findByLogin(login) != null) throw new AlreadyExistsException("User with login " + login + " already exists");
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }

        VerifyEmailService verificator = new VerifyEmailService(login, name, email, password);
        if (!verificator.verify()) return false;

        User newUser = new User(0, login, name, email, null);
        try {
            userMapper.addUser(newUser, password);
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    public User getUser(String login) {
        try {
            return userMapper.findByLogin(login);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public Manager getManager(User user) {
        try {
            return managerMapper.findByLogin(user.getLogin());
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (EndBeforeStartException e) {
            e.printStackTrace();
        }
        return null;
    }

    public TeamLeader getTeamLeader(User user) {
        try {
            return teamLeaderMapper.findByLogin(user.getLogin());
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (EndBeforeStartException e) {
            e.printStackTrace();
        }
        return null;
    }

    public Developer getDeveloper(User user) {
        try {
            return developerMapper.findByLogin(user.getLogin());
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (EndBeforeStartException e) {
            e.printStackTrace();
        }
        return null;
    }

    public Tester getTester(User user) {
        try {
            return testerMapper.findByLogin(user.getLogin());
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (EndBeforeStartException e) {
            e.printStackTrace();
        }
        return null;
    }

    public boolean authenticateUser(String login, String password) {
        User user = getUser(login);
        return user.signIn(password);
    }

    public boolean authenticateUser(User user, String password) {
        try {
            return userMapper.authenticateUser(user, password);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public Project addProject(String name, Manager manager) {
        Project project = new Project(name, manager);
        try {
            projectMapper.update(project);
            return project;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public Project getProject(String name) {
        try {
            return projectMapper.findByName(name);
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (EndBeforeStartException e) {
            e.printStackTrace();
        }
        return null;
    }

    public Project getProject(int id) {
        try {
            return projectMapper.findByID(id);
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (EndBeforeStartException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void clear() {
        userMapper.clear();
        managerMapper.clear();
        projectMapper.clear();
        teamLeaderMapper.clear();
        developerMapper.clear();
        testerMapper.clear();
    }

    public void update() throws SQLException {
        userMapper.update();
        managerMapper.update();
        projectMapper.update();
        teamLeaderMapper.update();
        developerMapper.update();
        testerMapper.update();
    }

    synchronized public void drop() {
        try {
            DataGateway.getInstance().dropAll();
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
