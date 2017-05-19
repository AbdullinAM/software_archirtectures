package com.spbpu.storage.user;

import com.spbpu.project.Project;
import com.spbpu.storage.DataGateway;
import com.spbpu.storage.project.ProjectMapper;
import com.spbpu.user.TeamLeader;
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
public class TeamLeaderMapper implements UserMapperInterface<TeamLeader> {

    private static Set<TeamLeader> teamLeaders = new HashSet<>();
    private Connection connection;
    private UserMapper userMapper;
    private ProjectMapper projectMapper;

    public TeamLeaderMapper() throws IOException, SQLException {
        connection = DataGateway.getInstance().getDataSource().getConnection();
        userMapper = new UserMapper();
        projectMapper = new ProjectMapper();
    }

    @Override
    public TeamLeader findByID(int id) throws SQLException {
        for (TeamLeader it : teamLeaders)
            if (it.getId() == id) return it;

        User user = userMapper.findByID(id);
        if (user == null) return null;
        TeamLeader teamLeader = new TeamLeader(user);
        teamLeaders.add(teamLeader);

        String extractProject = "SELECT (TEAMLEADERS.project) FROM TEAMLEADERS WHERE TEAMLEADERS.teamleader = ?;";
        PreparedStatement stmt = connection.prepareStatement(extractProject);
        stmt.setInt(1, teamLeader.getId());

        ResultSet rs = stmt.executeQuery();
        while (rs.next()) {
            int project = rs.getInt("project");
            teamLeader.addProject(projectMapper.findByID(project));
        }

        return teamLeader;
    }

    @Override
    public List<TeamLeader> findAll() throws SQLException {
        List<TeamLeader> all = new ArrayList<>();
        teamLeaders.clear();

        String extractAll = "SELECT TEAMLEADERS.teamleader FROM TEAMLEADERS;";
        Statement statement = connection.createStatement();
        ResultSet rs = statement.executeQuery(extractAll);
        while (rs.next()) {
            int user = rs.getInt("teamleader");
            all.add(findByID(user));
        }

        return all;
    }

    @Override
    public void update(TeamLeader item) throws SQLException {
        if (!teamLeaders.contains(item)) {
            userMapper.update(item);
            teamLeaders.add(item);
        }
        for (Project project : item.getProjects()) {
            String insertSQL = "INSERT UPDATE INTO TEAMLEADERS(project, teamleader) VALUES (?, ?);";
            PreparedStatement statement = connection.prepareStatement(insertSQL);
            statement.setInt(1, project.getId());
            statement.setInt(2, item.getId());
            statement.execute();
        }

    }

    @Override
    public void closeConnection() throws SQLException {
        userMapper.closeConnection();
        projectMapper.closeConnection();
        connection.close();
    }

    @Override
    public TeamLeader findByLogin(String login) throws SQLException {
        for (TeamLeader it : teamLeaders)
            if (it.getName().equals(login)) return it;

        User user = userMapper.findByLogin(login);
        if (user == null) return null;
        TeamLeader teamLeader = new TeamLeader(user);
        teamLeaders.add(teamLeader);

        String extractProject = "SELECT (TEAMLEADERS.project) FROM TEAMLEADERS WHERE TEAMLEADERS.teamleader = ?;";
        PreparedStatement stmt = connection.prepareStatement(extractProject);
        stmt.setInt(1, teamLeader.getId());

        ResultSet rs = stmt.executeQuery();
        while (rs.next()) {
            int project = rs.getInt("project");
            teamLeader.addProject(projectMapper.findByID(project));
        }

        return teamLeader;
    }
}
