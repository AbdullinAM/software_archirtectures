/**
 * Created by kivi on 19.05.17.
 */

package com.spbpu.storage.project;

import com.spbpu.exceptions.EndBeforeStartException;
import com.spbpu.project.Comment;
import com.spbpu.project.Milestone;
import com.spbpu.project.Ticket;
import com.spbpu.storage.DataGateway;
import com.spbpu.storage.Mapper;
import com.spbpu.user.TicketDeveloper;
import com.spbpu.user.TicketManager;
import com.spbpu.user.User;

import java.io.IOException;
import java.sql.*;
import java.util.*;
import java.util.Date;

public class TicketMapper implements Mapper<Ticket> {

    private static Set<Ticket> tickets = new HashSet<>();
    private Connection connection;
    private MilestoneMapper milestoneMapper;
    private CommentMapper commentMapper;

    public TicketMapper(MilestoneMapper mm) throws IOException, SQLException {
        connection = DataGateway.getInstance().getDataSource().getConnection();
        milestoneMapper = mm;
        commentMapper = new CommentMapper();
    }

    public List<Ticket> findTicketsOfMilestone(Milestone milestone) throws SQLException, EndBeforeStartException {
        List<Ticket> result = new ArrayList<>();
        String extractSQL = "SELECT TICKET.id FROM TICKET WHERE TICKET.milestone = ?;";
        PreparedStatement extract = connection.prepareStatement(extractSQL);
        extract.setInt(1, milestone.getId());
        ResultSet rs = extract.executeQuery();
        while (rs.next()) {
            result.add(findByID(rs.getInt("id")));
        }
        return result;
    }

    @Override
    public Ticket findByID(int id) throws SQLException, EndBeforeStartException {
        for (Ticket it : tickets)
            if (it.getId() == id) return it;

        String extractSQL = "SELECT * FROM TICKET WHERE id = ?;";
        PreparedStatement extract = connection.prepareStatement(extractSQL);
        extract.setInt(1, id);

        ResultSet rs = extract.executeQuery();
        if (!rs.next()) return null;

        // get milestone
        Milestone milestone = milestoneMapper.findByID(rs.getInt("milestone"));

        // get manager
        TicketManager manager = null;
        int managerId = rs.getInt("creator");
        for (TicketManager it : milestone.getProject().getTicketManagers()) {
            if (it.getId() == managerId) {
                manager = it;
                break;
            }
        }

        // get status
        Ticket.Status status = Ticket.Status.valueOf(rs.getString("status"));
        // get creation time
        Date creationTime = rs.getDate("creationTime");
        // get description
        String task = rs.getString("task");

        Ticket ticket = new Ticket(id, milestone, status, manager, creationTime, task);
        tickets.add(ticket);

        // extract comments
        String extractCommentsSQL = "SELECT TICKET_COMMENTS.commentid FROM TICKET_COMMENTS WHERE TICKET_COMMENTS.ticket = ?;";
        PreparedStatement extractComments = connection.prepareStatement(extractCommentsSQL);
        extractComments.setInt(1, id);
        ResultSet rsComments = extractComments.executeQuery();
        while (rsComments.next()) {
            ticket.addComment(commentMapper.findByID(rsComments.getInt("commentid")));
        }

        // get assignees
        String extracAssigneesSQL = "SELECT TICKET_ASSIGNEES.assignee FROM TICKET_ASSIGNEES WHERE TICKET_ASSIGNEES.ticket = ?;";
        PreparedStatement extractAssignees = connection.prepareStatement(extracAssigneesSQL);
        extractAssignees.setInt(1, id);

        ResultSet rsAssignees = extractAssignees.executeQuery();
        List<TicketDeveloper> developers = milestone.getProject().getTicketDevelopers();
        while (rsAssignees.next()) {
            int devid = rsAssignees.getInt("assignee");
            for (TicketDeveloper it : developers) {
                if (it.getId() == devid) {
                    ticket.addAssignee(it);
                    it.assign(ticket);
                }
            }
        }

        return ticket;
    }

    @Override
    public List<Ticket> findAll() throws SQLException, EndBeforeStartException {
        List<Ticket> all = new ArrayList<>();

        Statement statement = connection.createStatement();
        ResultSet rs = statement.executeQuery("SELECT TICKET.id FROM TICKET;");
        while (rs.next()) {
            Ticket ticket = findByID(rs.getInt("id"));
            all.add(ticket);
        }

        return all;
    }

    @Override
    public void update(Ticket item) throws SQLException {
        if (!tickets.contains(item)) {
            String insertSQL = "INSERT INTO TICKET(milestone, creator, status, creationTime, task) VALUES (?, ?, ?, ?, ?);";
            PreparedStatement insert = connection.prepareStatement(insertSQL, Statement.RETURN_GENERATED_KEYS);
            insert.setInt(1, item.getMilestone().getId());
            insert.setInt(2, item.getCreator().getId());
            insert.setString(3, item.getStatus().name());
            insert.setDate(4, new java.sql.Date(item.getCreationTime().getTime()));
            insert.setString(5, item.getTask());
            insert.execute();
            ResultSet rs = insert.getGeneratedKeys();
            if (rs.next()) {
                long id = rs.getLong(1);
                item.setId((int) id);
            }
            tickets.add(item);
        } else {
            String update = "UPDATE TICKET SET status = ? WHERE id = ?;";
            PreparedStatement updateStatus = connection.prepareStatement(update);
            updateStatus.setString(1, item.getStatus().name());
            updateStatus.setInt(2, item.getId());
            updateStatus.execute();
        }

        for (TicketDeveloper dev : item.getAssignees()) {
            String update = "INSERT IGNORE INTO TICKET_ASSIGNEES(ticket, assignee) VALUES (?, ?);";
            PreparedStatement stmt = connection.prepareStatement(update);
            stmt.setInt(1, item.getId());
            stmt.setInt(2, dev.getId());
            stmt.execute();
        }

        for (Comment it : item.getComments()) {
            String insertSQL = "INSERT IGNORE INTO TICKET_COMMENTS(commentid, ticket) VALUES (?, ?);";
            PreparedStatement stmt = connection.prepareStatement(insertSQL);
            stmt.setInt(1, it.getId());
            stmt.setInt(2, item.getId());
            stmt.execute();
            commentMapper.update(it);
        }

    }

    @Override
    public void closeConnection() throws SQLException {
        commentMapper.closeConnection();
        connection.close();
    }

    @Override
    public void clear() {
        commentMapper.clear();
        tickets.clear();
    }

    @Override
    public void update() throws SQLException {
        commentMapper.update();
        for (Ticket it : tickets)
            update(it);
    }
}
