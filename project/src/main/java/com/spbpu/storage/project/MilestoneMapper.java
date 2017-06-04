/**
 * Created by kivi on 19.05.17.
 */

package com.spbpu.storage.project;

import com.spbpu.exceptions.EndBeforeStartException;
import com.spbpu.project.Milestone;
import com.spbpu.project.Project;
import com.spbpu.project.Ticket;
import com.spbpu.storage.DataGateway;
import com.spbpu.storage.Mapper;

import java.io.IOException;
import java.sql.*;
import java.time.chrono.MinguoEra;
import java.util.*;
import java.util.Date;
import java.util.stream.Collectors;

public class MilestoneMapper implements Mapper<Milestone> {

    private static Set<Milestone> milestones = new HashSet<>();
    private Connection connection;
    private ProjectMapper projectMapper;
    private TicketMapper ticketMapper;

    public MilestoneMapper(ProjectMapper pm) throws IOException, SQLException {
        connection = DataGateway.getInstance().getDataSource().getConnection();
        projectMapper = pm;
        ticketMapper = new TicketMapper(this);
    }

    public List<Milestone> findProjectMilestones(Project project) throws SQLException, EndBeforeStartException {
        List<Milestone> projectMilestones = new ArrayList<>();

        String findSQL = "SELECT MILESTONE.id FROM MILESTONE WHERE MILESTONE.project = ?;";
        PreparedStatement stmt = connection.prepareStatement(findSQL);
        stmt.setInt(1, project.getId());
        ResultSet rs = stmt.executeQuery();

        while (rs.next()) {
            projectMilestones.add(findByID(rs.getInt("id")));
        }

        return projectMilestones;
    }

    @Override
    public Milestone findByID(int id) throws SQLException, EndBeforeStartException {
        for (Milestone it : milestones)
            if (it.getId() == id) return it;

        String extractSQL = "SELECT * FROM MILESTONE WHERE id = ?;";
        PreparedStatement extract = connection.prepareStatement(extractSQL);
        extract.setInt(1, id);
        ResultSet rs = extract.executeQuery();

        if (!rs.next()) return null;

        Project project = projectMapper.findByID(rs.getInt("project"));
        Milestone.Status status = Milestone.Status.valueOf(rs.getString("status"));
        Date startDate = rs.getDate("startDate");
        Date endDate = rs.getDate("endDate");
        Date activeDate = rs.getDate("activeDate");
        Date closingDate = rs.getDate("closingDate");

        Milestone milestone = new Milestone(id, project, status, startDate, endDate);
        if (activeDate != null) milestone.setActiveDate(activeDate);
        if (closingDate != null) milestone.setClosingDate(closingDate);
        milestones.add(milestone);

        milestone.setTickets(ticketMapper.findTicketsOfMilestone(milestone).stream().collect(Collectors.toSet()));

        return milestone;
    }

    @Override
    public List<Milestone> findAll() throws SQLException, EndBeforeStartException {
        List<Milestone> all = new ArrayList<>();
        Statement stmt = connection.createStatement();
        ResultSet rs = stmt.executeQuery("SELECT MILESTONE.id FROM MILESTONE;");
        while (rs.next()) {
            all.add(findByID(rs.getInt("id")));
        }
        return all;
    }

    @Override
    public void update(Milestone item) throws SQLException {
        if (!milestones.contains(item)) {
            System.out.println("New!!");
            String insertSQL = "INSERT INTO MILESTONE(project, status, startDate, endDate) VALUES (?, ?, ?, ?);";
            PreparedStatement stmt = connection.prepareStatement(insertSQL, Statement.RETURN_GENERATED_KEYS);
            stmt.setInt(1, item.getProject().getId());
            stmt.setString(2, item.getStatus().name());
            stmt.setDate(3, new java.sql.Date(item.getStartDate().getTime()));
            stmt.setDate(4, new java.sql.Date(item.getEndDate().getTime()));
            stmt.execute();
            ResultSet rs = stmt.getGeneratedKeys();
            if (rs.next()) {
                long id = rs.getLong(1);
                item.setId((int) id);
            }
            milestones.add(item);
        } else {
            String update = "UPDATE MILESTONE SET status = ? WHERE id = ?;";
            PreparedStatement updateStatus = connection.prepareStatement(update);
            updateStatus.setString(1, item.getStatus().name());
            updateStatus.setInt(2, item.getId());
            updateStatus.execute();
        }

        if (item.getActiveDate() != null) {
            PreparedStatement updateActive = connection.prepareStatement("UPDATE MILESTONE SET activeDate = ? WHERE id = ?;");
            updateActive.setDate(1, new java.sql.Date(item.getActiveDate().getTime()));
            updateActive.setInt(2, item.getId());
            updateActive.execute();
        }
        if (item.getClosingDate() != null) {
            PreparedStatement updateClosing = connection.prepareStatement("UPDATE MILESTONE SET closingDate = ? WHERE id = ?;");
            updateClosing.setDate(1, new java.sql.Date(item.getClosingDate().getTime()));
            updateClosing.setInt(2, item.getId());
            updateClosing.execute();
        }

        for (Ticket ticket : item.getTickets())
            ticketMapper.update(ticket);
    }

    @Override
    public void closeConnection() throws SQLException {
        ticketMapper.closeConnection();
        connection.close();
    }

    @Override
    public void clear() {
        ticketMapper.clear();
        milestones.clear();
    }

    @Override
    public void update() throws SQLException {
        ticketMapper.update();
        for (Milestone it : milestones)
            update(it);
    }
}
