/**
 * Created by kivi on 19.05.17.
 */

package com.spbpu.storage.project;

import com.spbpu.exceptions.EndBeforeStartException;
import com.spbpu.project.Milestone;
import com.spbpu.project.Project;
import com.spbpu.storage.DataGateway;
import com.spbpu.storage.Mapper;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class MilestoneMapper implements Mapper<Milestone> {

    private static Set<Milestone> milestones = new HashSet<>();
    private Connection connection;
    private ProjectMapper projectMapper;
    private TicketMapper ticketMapper;

    public MilestoneMapper() throws IOException, SQLException {
        connection = DataGateway.getInstance().getDataSource().getConnection();
        projectMapper = new ProjectMapper();
        ticketMapper = new TicketMapper();
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

        Milestone milestone = new Milestone(id, project, startDate, endDate);
        if (activeDate != null) milestone.setActiveDate(activeDate);
        if (closingDate != null) milestone.setClosingDate(closingDate);
        milestones.add(milestone);

        return milestone;
    }

    @Override
    public List<Milestone> findAll() throws SQLException {
        return null;
    }

    @Override
    public void update(Milestone item) throws SQLException {

    }

    @Override
    public void closeConnection() throws SQLException {
        projectMapper.closeConnection();
        ticketMapper.closeConnection();
        connection.close();
    }
}
