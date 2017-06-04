/**
 * Created by Azat on 26.03.2017.
 */
package com.spbpu.storage;

import com.spbpu.exceptions.*;
import com.spbpu.project.Project;
import com.spbpu.service.VerifyEmailService;
import com.spbpu.storage.project.ProjectMapper;
import com.spbpu.storage.user.*;
import com.spbpu.user.*;

import java.io.IOException;
import java.sql.SQLException;

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

    public void addUser(String login, String name, String email, String password)
            throws UserAlreadyExistsException, EmailNotVerifiedException, DBConnectionException {
        try {
            if (userMapper.findByLogin(login) != null)
                throw new UserAlreadyExistsException(login);

            VerifyEmailService verificator = new VerifyEmailService(login, name, email, password);
            if (!verificator.verify()) throw new EmailNotVerifiedException(email);

            User newUser = new User(0, login, name, email, null);
            userMapper.addUser(newUser, password);
        } catch (SQLException e) {
            throw new DBConnectionException();
        }
    }

    public User getUser(String login) throws UserNotFoundException, DBConnectionException {
        try {
            User user = userMapper.findByLogin(login);
            if (user == null) throw new UserNotFoundException(login);
            return user;
        } catch (SQLException e) {
            throw new DBConnectionException();
        }
    }

    public Manager getManager(User user) throws EndBeforeStartException, DBConnectionException {
        try {
            return managerMapper.findByLogin(user.getLogin());
        } catch (SQLException e) {
            throw new DBConnectionException();
        }
    }

    public TeamLeader getTeamLeader(User user) throws EndBeforeStartException, DBConnectionException {
        try {
            return teamLeaderMapper.findByLogin(user.getLogin());
        } catch (SQLException e) {
            throw new DBConnectionException();
        }
    }

    public Developer getDeveloper(User user) throws EndBeforeStartException, DBConnectionException {
        try {
            return developerMapper.findByLogin(user.getLogin());
        } catch (SQLException e) {
            throw new DBConnectionException();
        }
    }

    public Tester getTester(User user) throws EndBeforeStartException, DBConnectionException, UserAlreadyHasRoleException {
        try {
            return testerMapper.findByLogin(user.getLogin());
        } catch (SQLException e) {
            throw new DBConnectionException();
        }
    }

    public void authenticateUser(String login, String password) throws DBConnectionException, IncorrectPasswordException {
        // replase UserNotFound exception with IncorrectPassword exception,
        // so nobody can find out if login was correct or not
        try {
            getUser(login).signIn(password);
        } catch (UserNotFoundException e) {
            throw new IncorrectPasswordException();
        }
    }

    public void authenticateUser(User user, String password) throws DBConnectionException, IncorrectPasswordException {
        try {
            if (!userMapper.authenticateUser(user, password))
                throw new IncorrectPasswordException();
        } catch (SQLException e) {
            throw new DBConnectionException();
        }
    }

    public Project addProject(String name, Manager manager)
            throws DBConnectionException, ProjectAlreadyExistsException, EndBeforeStartException {
        Project project = new Project(name, manager);
        try {
            if (projectMapper.findByName(name) != null) throw new ProjectAlreadyExistsException(name);
            projectMapper.update(project);
            return project;
        } catch (SQLException e) {
            throw new DBConnectionException();
        }
    }

    public Project getProject(String name) throws DBConnectionException, EndBeforeStartException {
        try {
            return projectMapper.findByName(name);
        } catch (SQLException e) {
            throw new DBConnectionException();
        }
    }

    public Project getProject(int id) throws DBConnectionException, EndBeforeStartException {
        try {
            return projectMapper.findByID(id);
        } catch (SQLException e) {
            throw new DBConnectionException();
        }
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

    synchronized public void drop() throws DBConnectionException {
        try {
            DataGateway.getInstance().dropAll();
        } catch (SQLException e) {
            throw new DBConnectionException();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
