package com.spbpu.storage.user;

import com.spbpu.exceptions.EndBeforeStartException;
import com.spbpu.exceptions.UserAlreadyHasRoleException;
import com.spbpu.project.Project;
import com.spbpu.storage.DataGateway;
import com.spbpu.storage.project.ProjectMapper;
import com.spbpu.user.Tester;
import com.spbpu.user.User;

import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by kivi on 19.05.17.
 */
public class TesterMapper implements UserMapperInterface<Tester> {

    private static Set<Tester> testers = new HashSet<>();
    private Connection connection;
    private UserMapper userMapper;
    private ProjectMapper projectMapper;

    public TesterMapper(ProjectMapper pm) throws IOException, SQLException {
        connection = DataGateway.getInstance().getDataSource().getConnection();
        userMapper = new UserMapper();
        projectMapper = pm;
    }


    public List<Tester> findDevelopersOfProject(Project project) throws SQLException, EndBeforeStartException {
        List<Tester> tessters = new ArrayList<>();

        String extractTesters = "SELECT (TESTERS.tester) FROM TESTERS WHERE TESTERS.project = ?;";
        PreparedStatement stmt = connection.prepareStatement(extractTesters);
        stmt.setInt(1, project.getId());

        ResultSet rs = stmt.executeQuery();
        while (rs.next()) {
            tessters.add(findByID(rs.getInt("tester")));
        }

        return tessters;
    }

    @Override
    public Tester findByLogin(String login) throws SQLException, EndBeforeStartException, UserAlreadyHasRoleException {
        for (Tester it : testers)
            if (it.getName().equals(login)) return it;

        User user = userMapper.findByLogin(login);
        if (user == null) return null;
        Tester tester = new Tester(user);
        testers.add(tester);

        String extractProject = "SELECT (TESTERS.project) FROM TESTERS WHERE TESTERS.tester = ?;";
        PreparedStatement stmt = connection.prepareStatement(extractProject);
        stmt.setInt(1, tester.getId());

        ResultSet rs = stmt.executeQuery();
        while (rs.next()) {
            int project = rs.getInt("project");
            tester.addProject(projectMapper.findByID(project));
        }

        return tester;
    }

    @Override
    public Tester findByID(int id) throws SQLException, EndBeforeStartException {
        for (Tester it : testers)
            if (it.getId() == id) return it;

        User user = userMapper.findByID(id);
        if (user == null) return null;
        Tester tester = new Tester(user);
        testers.add(tester);

        String extractProject = "SELECT (TESTERS.project) FROM TESTERS WHERE TESTERS.tester = ?;";
        PreparedStatement stmt = connection.prepareStatement(extractProject);
        stmt.setInt(1, tester.getId());

        ResultSet rs = stmt.executeQuery();
        while (rs.next()) {
            int project = rs.getInt("project");
            tester.addProject(projectMapper.findByID(project));
        }

        return tester;
    }

    @Override
    public List<Tester> findAll() throws SQLException, EndBeforeStartException {
        List<Tester> all = new ArrayList<>();

        String extractAll = "SELECT DISTINCT TESTERS.tester FROM TESTERS;";
        Statement statement = connection.createStatement();
        ResultSet rs = statement.executeQuery(extractAll);
        while (rs.next()) {
            int user = rs.getInt("tester");
            all.add(findByID(user));
        }

        return all;
    }

    @Override
    public void update(Tester item) throws SQLException {
        if (!testers.contains(item)) {
            userMapper.update(item);
            testers.add(item);
        }
        for (Project project : item.getProjects()) {
            String insertSQL = "INSERT IGNORE INTO TESTERS(project, tester) VALUES (?, ?);";
            PreparedStatement statement = connection.prepareStatement(insertSQL);
            statement.setInt(1, project.getId());
            statement.setInt(2, item.getId());
            statement.execute();
        }
    }

    @Override
    public void closeConnection() throws SQLException {
        userMapper.closeConnection();
        connection.close();
    }

    @Override
    public void clear() {
        userMapper.clear();
        testers.clear();
    }

    @Override
    public void update() throws SQLException {
        userMapper.update();
        for (Tester it : testers)
            update(it);
    }

}