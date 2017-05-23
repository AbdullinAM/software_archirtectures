package com.spbpu.storage.user;

import com.spbpu.exceptions.EndBeforeStartException;
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

    public TeamLeaderMapper(ProjectMapper pm) throws IOException, SQLException {
        connection = DataGateway.getInstance().getDataSource().getConnection();
        userMapper = new UserMapper();
        projectMapper = pm;
    }

    public TeamLeader findTeamLeaderOfProject(Project project) throws SQLException, EndBeforeStartException {
        for (TeamLeader teamLeader : teamLeaders)
            if (teamLeader.getProjects().contains(project)) return teamLeader;

        String extractTL = "SELECT (TEAMLEADERS.teamleader) FROM TEAMLEADERS WHERE TEAMLEADERS.project = ?;";
        PreparedStatement stmt = connection.prepareStatement(extractTL);
        stmt.setInt(1, project.getId());
        ResultSet rs = stmt.executeQuery();

        if (!rs.next()) return null;

        return findByID(rs.getInt("teamleader"));
    }

    @Override
    public TeamLeader findByID(int id) throws SQLException, EndBeforeStartException {
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
    public List<TeamLeader> findAll() throws SQLException, EndBeforeStartException {
        List<TeamLeader> all = new ArrayList<>();

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
            String insertSQL = "INSERT INTO TEAMLEADERS(project, teamleader) VALUES (?, ?) ON DUPLICATE KEY UPDATE teamleader = ?;";
            PreparedStatement statement = connection.prepareStatement(insertSQL);
            statement.setInt(1, project.getId());
            statement.setInt(2, item.getId());
            statement.setInt(3, item.getId());
            statement.execute();
        }

    }

    @Override
    public void closeConnection() throws SQLException {
        userMapper.closeConnection();
        connection.close();
    }

    @Override
    public TeamLeader findByLogin(String login) throws SQLException, EndBeforeStartException {
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

    @Override
    public void clear() {
        userMapper.clear();
        teamLeaders.clear();
    }


    @Override
    public void update() throws SQLException {
        userMapper.update();
        for (TeamLeader it : teamLeaders)
            update(it);
    }
}
