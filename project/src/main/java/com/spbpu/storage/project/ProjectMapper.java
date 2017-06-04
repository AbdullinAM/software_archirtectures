/**
 * Created by kivi on 11.05.17.
 */

package com.spbpu.storage.project;

import com.spbpu.exceptions.EndBeforeStartException;
import com.spbpu.exceptions.UserAlreadyHasRoleException;
import com.spbpu.project.BugReport;
import com.spbpu.project.Milestone;
import com.spbpu.project.Project;
import com.spbpu.project.Ticket;
import com.spbpu.storage.DataGateway;
import com.spbpu.storage.Mapper;
import com.spbpu.storage.user.DeveloperMapper;
import com.spbpu.storage.user.ManagerMapper;
import com.spbpu.storage.user.TeamLeaderMapper;
import com.spbpu.storage.user.TesterMapper;
import com.spbpu.user.Developer;
import com.spbpu.user.Manager;
import com.spbpu.user.TeamLeader;
import com.spbpu.user.Tester;

import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ProjectMapper implements Mapper<Project> {

    private static Set<Project> projects = new HashSet<>();
    private Connection connection;
    private ManagerMapper managerMapper;
    private TeamLeaderMapper teamLeaderMapper;
    private DeveloperMapper developerMapper;
    private MilestoneMapper milestoneMapper;
    private TesterMapper testerMapper;
    private BugReportMapper reportMapper;

    public ProjectMapper(ManagerMapper mm) throws IOException, SQLException {
        connection = DataGateway.getInstance().getDataSource().getConnection();
        managerMapper = mm;
        teamLeaderMapper = new TeamLeaderMapper(this);
        developerMapper = new DeveloperMapper(this);
        milestoneMapper = new MilestoneMapper(this);
        testerMapper = new TesterMapper(this);
        reportMapper = new BugReportMapper(this);
    }

    public List<Project> findAllManagerProjects(Manager manager) throws SQLException, EndBeforeStartException {
        List<Project> managerProjects = new ArrayList<>();

        String findSQL = "SELECT * FROM PROJECT WHERE PROJECT.manager = ?;";
        PreparedStatement extract = connection.prepareStatement(findSQL);
        extract.setInt(1, manager.getId());
        ResultSet rs = extract.executeQuery();
        while (rs.next()) {
            int projid = rs.getInt("id");
            for (Project it : projects) {
                if (it.getId() == projid) {
                    managerProjects.add(it);
                    continue;
                }
            }

            String name = rs.getString("name");
            Project project = new Project(projid, name, manager);
            projects.add(project);

            try {
                TeamLeader tl = teamLeaderMapper.findTeamLeaderOfProject(project);
                if (tl != null) project.setTeamLeader(tl);
                managerProjects.add(project);

                for (Developer dev : developerMapper.findDevelopersOfProject(project)) {
                    project.addDeveloper(dev);
                }
                for (Tester tester : testerMapper.findDevelopersOfProject(project)) {
                    project.addTester(tester);
                }
                for (Milestone milestone : milestoneMapper.findProjectMilestones(project)) {
                    project.addMilestone(milestone);
                }
                for (BugReport report : reportMapper.findReportsOfProject(project)) {
                    project.addReport(report);
                }
            } catch (UserAlreadyHasRoleException e) {}
        }

        return managerProjects;
    }

    public Project findByName(String name) throws SQLException, EndBeforeStartException {
        for (Project it : projects)
            if (it.getName().equals(name)) return it;

        String findSQL = "SELECT * FROM PROJECT WHERE PROJECT.name = ?;";
        PreparedStatement extract = connection.prepareStatement(findSQL);
        extract.setString(1, name);
        ResultSet rs = extract.executeQuery();

        if (!rs.next()) return null;

        Manager manager = managerMapper.findByID(rs.getInt("manager"));
        Project project = new Project(rs.getInt("id"), name, manager);
        projects.add(project);

        try {
            TeamLeader tl = teamLeaderMapper.findTeamLeaderOfProject(project);
            if (tl != null) project.setTeamLeader(tl);

            for (Developer dev : developerMapper.findDevelopersOfProject(project)) {
                project.addDeveloper(dev);
            }
            for (Tester tester : testerMapper.findDevelopersOfProject(project)) {
                project.addTester(tester);
            }
            for (Milestone milestone : milestoneMapper.findProjectMilestones(project)) {
                project.addMilestone(milestone);
            }
            for (BugReport report : reportMapper.findReportsOfProject(project)) {
                project.addReport(report);
            }
        } catch (UserAlreadyHasRoleException e) {}

        return project;
    }

    @Override
    public Project findByID(int id) throws SQLException, EndBeforeStartException {
        for (Project it : projects)
            if (it.getId() == id) return it;

        String findSQL = "SELECT * FROM PROJECT WHERE PROJECT.id = ?;";
        PreparedStatement extract = connection.prepareStatement(findSQL);
        extract.setInt(1, id);
        ResultSet rs = extract.executeQuery();

        if (!rs.next()) return null;

        Manager manager = managerMapper.findByID(rs.getInt("manager"));
        String name = rs.getString("name");
        Project project = new Project(id, name, manager);
        projects.add(project);

        try{
            TeamLeader tl = teamLeaderMapper.findTeamLeaderOfProject(project);
            if (tl != null) project.setTeamLeader(tl);

            for (Developer dev : developerMapper.findDevelopersOfProject(project)) {
                project.addDeveloper(dev);
            }
            for (Tester tester : testerMapper.findDevelopersOfProject(project)) {
                project.addTester(tester);
            }
            for (Milestone milestone : milestoneMapper.findProjectMilestones(project)) {
                project.addMilestone(milestone);
            }
            for (BugReport report : reportMapper.findReportsOfProject(project)) {
                project.addReport(report);
            }
        } catch (UserAlreadyHasRoleException e) {}

        return project;
    }

    @Override
    public List<Project> findAll() throws SQLException, EndBeforeStartException {
        List<Project> all = new ArrayList<>();

        Statement stmt = connection.createStatement();
        ResultSet rs = stmt.executeQuery("SELECT PROJECT.id FROM PROJECT;");
        while (rs.next())
            all.add(findByID(rs.getInt("id")));

        return all;
    }

    @Override
    public void update(Project item) throws SQLException {
        if (!projects.contains(item)) {
            String insertSQL = "INSERT INTO PROJECT(name, manager) VALUES (?, ?);";
            PreparedStatement stmt = connection.prepareStatement(insertSQL, Statement.RETURN_GENERATED_KEYS);
            stmt.setString(1, item.getName());
            stmt.setInt(2, item.getManager().getId());
            stmt.execute();
            ResultSet rs = stmt.getGeneratedKeys();
            if (rs.next()) {
                long id = rs.getLong(1);
                item.setId((int) id);
            }
            projects.add(item);
        }
        if (item.getTeamLeader() != null)
            teamLeaderMapper.update(item.getTeamLeader());

        for (Developer dev : item.getDevelopers())
            developerMapper.update(dev);

        for (Tester tester : item.getTesters())
            testerMapper.update(tester);

        for (Milestone milestone : item.getMilestones())
            milestoneMapper.update(milestone);

        for (BugReport report : item.getReports())
            reportMapper.update(report);
    }

    @Override
    public void closeConnection() throws SQLException {
        teamLeaderMapper.closeConnection();
        developerMapper.closeConnection();
        milestoneMapper.closeConnection();
        testerMapper.closeConnection();
        reportMapper.closeConnection();
        connection.close();
    }

    @Override
    public void clear() {
        teamLeaderMapper.clear();
        developerMapper.clear();
        milestoneMapper.clear();
        testerMapper.clear();
        reportMapper.clear();
        projects.clear();
    }

    @Override
    public void update() throws SQLException {
        teamLeaderMapper.update();
        developerMapper.update();
        milestoneMapper.update();
        testerMapper.update();
        reportMapper.update();
        for (Project it : projects)
            update(it);
    }
}
