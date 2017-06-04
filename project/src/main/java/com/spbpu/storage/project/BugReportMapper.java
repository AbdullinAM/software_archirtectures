/**
 * Created by kivi on 19.05.17.
 */

package com.spbpu.storage.project;

import com.spbpu.exceptions.AlreadyAcceptedException;
import com.spbpu.exceptions.EndBeforeStartException;
import com.spbpu.exceptions.NotAuthenticatedException;
import com.spbpu.project.BugReport;
import com.spbpu.project.Comment;
import com.spbpu.project.Project;
import com.spbpu.project.Ticket;
import com.spbpu.storage.DataGateway;
import com.spbpu.storage.Mapper;
import com.spbpu.user.ReportCreator;
import com.spbpu.user.ReportDeveloper;

import java.io.IOException;
import java.sql.*;
import java.util.*;
import java.util.Date;

public class BugReportMapper implements Mapper<BugReport> {

    private static Set<BugReport> bugReports = new HashSet<>();
    private Connection connection;
    private ProjectMapper projectMapper;
    private CommentMapper commentMapper;

    public BugReportMapper(ProjectMapper pm) throws IOException, SQLException {
        connection = DataGateway.getInstance().getDataSource().getConnection();
        projectMapper = pm;
        commentMapper = new CommentMapper();
    }

    public List<BugReport> findReportsOfProject(Project project) throws SQLException, EndBeforeStartException {
        List<BugReport> reports = new ArrayList<>();

        String extractReports = "SELECT BUGREPORT.id FROM BUGREPORT WHERE BUGREPORT.project = ?";
        PreparedStatement stmt = connection.prepareStatement(extractReports);
        stmt.setInt(1, project.getId());
        ResultSet rs = stmt.executeQuery();

        while (rs.next()) {
            reports.add(findByID(rs.getInt("id")));
        }

        return reports;
    }

    @Override
    public BugReport findByID(int id) throws SQLException, EndBeforeStartException {
        for (BugReport it : bugReports)
            if (it.getId() == id) return it;

        String extrectReport = "SELECT * FROM BUGREPORT WHERE BUGREPORT.id = ?;";
        PreparedStatement statement = connection.prepareStatement(extrectReport);
        statement.setInt(1, id);

        ResultSet rs = statement.executeQuery();
        if (!rs.next()) return null;

        // extract project
        Project project = projectMapper.findByID(rs.getInt("project"));

        // find report creator
        int creatorId = rs.getInt("creator");
        ReportCreator creator = null;
        for (ReportCreator rc : project.getReportCreators()) {
            if (rc.getId() == creatorId) {
                creator = rc;
                break;
            }
        }

        // get status
        BugReport.Status status = BugReport.Status.valueOf(rs.getString("status"));
        // get creation time
        Date creationTime = rs.getDate("creationTime");
        // get description
        String description = rs.getString("description");

        // create report
        BugReport report = new BugReport(id, project, creator, status, description, creationTime);
        bugReports.add(report);

        // extract comments
        String extractCommentsSQL = "SELECT BUGREPORT_COMMENTS.commentid FROM BUGREPORT_COMMENTS WHERE BUGREPORT_COMMENTS.bugreport = ?";
        PreparedStatement extractComments = connection.prepareStatement(extractCommentsSQL);
        extractComments.setInt(1, id);
        ResultSet rsComments = extractComments.executeQuery();
        while (rsComments.next()) {
            report.addComment(commentMapper.findByID(rsComments.getInt("commentid")));
        }

        // find developer (if exists)
        int developerId = rs.getInt("developer");
        ReportDeveloper developer = null;
        for (ReportDeveloper rd : project.getReportDevelopers()) {
            if (rd.getId() == developerId) {
                developer = rd;
                break;
            }
        }
        if (developer != null) {
            report.setDeveloper(developer);
            developer.assign(report);
        }

        return report;
    }

    @Override
    public List<BugReport> findAll() throws SQLException, EndBeforeStartException {
        List<BugReport> all = new ArrayList<>();

        Statement statement = connection.createStatement();
        ResultSet rs = statement.executeQuery("SELECT * FROM BUGREPORT;");
        while (rs.next()) {
            BugReport report = findByID(rs.getInt("id"));
            all.add(report);
        }
        return all;
    }

    @Override
    public void update(BugReport item) throws SQLException {
        if (!bugReports.contains(item)) {
            String insertSQL = "INSERT INTO BUGREPORT(project, creator, status, creationTime, description) VALUES (?, ?, ?, ?, ?);";
            PreparedStatement statement = connection.prepareStatement(insertSQL, Statement.RETURN_GENERATED_KEYS);
            statement.setInt(1, item.getProject().getId());
            statement.setInt(2, item.getCreator().getId());
            statement.setString(3, item.getStatus().name());
            statement.setDate(4, new java.sql.Date(item.getCreationTime().getTime()));
            statement.setString(5, item.getDescription());
            statement.execute();
            ResultSet rs = statement.getGeneratedKeys();
            if (rs.next()) {
                long id = rs.getLong(1);
                item.setId((int) id);
            }
            bugReports.add(item);
        } else {
            String update = "UPDATE BUGREPORT SET status = ? WHERE id = ?;";
            PreparedStatement updateStatus = connection.prepareStatement(update);
            updateStatus.setString(1, item.getStatus().name());
            updateStatus.setInt(2, item.getId());
            updateStatus.execute();
        }
        if (item.getAssignee() != null) {
            String update = "UPDATE BUGREPORT SET developer = ? WHERE id = ?;";
            PreparedStatement updateStatus = connection.prepareStatement(update);
            updateStatus.setInt(1, item.getAssignee().getId());
            updateStatus.setInt(2, item.getId());
            updateStatus.execute();
        }

        for (Comment it : item.getComments()) {
            String insertSQL = "INSERT IGNORE INTO BUGREPORT_COMMENTS(commentid, bugreport) VALUES (?, ?);";
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
        bugReports.clear();
    }

    @Override
    public void update() throws SQLException {
        for (BugReport it : bugReports)
            update(it);
    }
}
