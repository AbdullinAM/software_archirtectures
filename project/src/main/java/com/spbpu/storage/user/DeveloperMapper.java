/**
 * Created by kivi on 11.05.17.
 */

package com.spbpu.storage.user;

import com.spbpu.exceptions.EndBeforeStartException;
import com.spbpu.project.Project;
import com.spbpu.storage.DataGateway;
import com.spbpu.storage.project.ProjectMapper;
import com.spbpu.user.Developer;
import com.spbpu.user.Manager;
import com.spbpu.user.User;

import javax.swing.plaf.nimbus.State;
import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class DeveloperMapper implements UserMapperInterface<Developer> {

    private static Set<Developer> developers = new HashSet<>();
    private Connection connection;
    private UserMapper userMapper;
    private ProjectMapper projectMapper;

    public DeveloperMapper(ProjectMapper pm) throws IOException, SQLException {
        connection = DataGateway.getInstance().getDataSource().getConnection();
        userMapper = new UserMapper();
        projectMapper = pm;
    }

    public List<Developer> findDevelopersOfProject(Project project) throws SQLException, EndBeforeStartException {
        List<Developer> devs = new ArrayList<>();

        String extractDevelopers = "SELECT (DEVELOPERS.developer) FROM DEVELOPERS WHERE DEVELOPERS.project = ?;";
        PreparedStatement stmt = connection.prepareStatement(extractDevelopers);
        stmt.setInt(1, project.getId());

        ResultSet rs = stmt.executeQuery();
        while (rs.next()) {
            devs.add(findByID(rs.getInt("developer")));
        }

        return devs;
    }

    @Override
    public Developer findByLogin(String login) throws SQLException, EndBeforeStartException {
        for (Developer it : developers)
            if (it.getLogin().equals(login)) return it;

        User user = userMapper.findByLogin(login);
        if (user == null) return null;
        Developer developer = new Developer(user);
        developers.add(developer);

        String extractProject = "SELECT (DEVELOPERS.project) FROM DEVELOPERS WHERE DEVELOPERS.developer = ?;";
        PreparedStatement stmt = connection.prepareStatement(extractProject);
        stmt.setInt(1, developer.getId());

        ResultSet rs = stmt.executeQuery();
        while (rs.next()) {
            int project = rs.getInt("project");
            developer.addProject(projectMapper.findByID(project));
        }

        return developer;
    }

    @Override
    public Developer findByID(int id) throws SQLException, EndBeforeStartException {
        for (Developer it : developers)
            if (it.getId() == id) return it;

        User user = userMapper.findByID(id);
        if (user == null) return null;
        Developer developer = new Developer(user);
        developers.add(developer);

        String extractProject = "SELECT (DEVELOPERS.project) FROM DEVELOPERS WHERE DEVELOPERS.developer = ?;";
        PreparedStatement stmt = connection.prepareStatement(extractProject);
        stmt.setInt(1, developer.getId());

        ResultSet rs = stmt.executeQuery();
        while (rs.next()) {
            int project = rs.getInt("project");
            developer.addProject(projectMapper.findByID(project));
        }

        return developer;
    }

    @Override
    public List<Developer> findAll() throws SQLException, EndBeforeStartException {
        List<Developer> all = new ArrayList<>();

        String extractAll = "SELECT DISTINCT DEVELOPERS.developer FROM DEVELOPERS;";
        Statement statement = connection.createStatement();
        ResultSet rs = statement.executeQuery(extractAll);
        while (rs.next()) {
            int user = rs.getInt("developer");
            all.add(findByID(user));
        }

        return all;
    }

    @Override
    public void update(Developer item) throws SQLException {
        if (!developers.contains(item)) {
            userMapper.update(item);
            developers.add(item);
        }
        for (Project project : item.getProjects()) {
            String insertSQL = "INSERT IGNORE INTO DEVELOPERS(project, developer) VALUES (?, ?);";
            PreparedStatement statement = connection.prepareStatement(insertSQL);
            statement.setInt(1, project.getId());
            statement.setInt(2, item.getId());
            statement.execute();
        }
    }

    @Override
    public void closeConnection() throws SQLException {
        developers.clear();
        userMapper.closeConnection();
        connection.close();
    }

    @Override
    public void clear() {
        userMapper.clear();
        developers.clear();
    }

    @Override
    public void update() throws SQLException {
        userMapper.update();
        for (Developer it : developers)
            update(it);
    }

}
