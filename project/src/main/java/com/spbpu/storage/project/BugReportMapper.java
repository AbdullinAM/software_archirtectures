/**
 * Created by kivi on 19.05.17.
 */

package com.spbpu.storage.project;

import com.spbpu.project.BugReport;
import com.spbpu.project.Comment;
import com.spbpu.project.Project;
import com.spbpu.storage.DataGateway;
import com.spbpu.storage.Mapper;
import com.spbpu.storage.user.DeveloperMapper;
import com.spbpu.storage.user.TeamLeaderMapper;
import com.spbpu.storage.user.TesterMapper;
import com.spbpu.storage.user.UserMapper;
import com.spbpu.user.ReportCreator;
import com.spbpu.user.ReportDeveloper;
import com.spbpu.user.TeamLeader;
import com.spbpu.user.User;

import java.io.IOException;
import java.sql.*;
import java.util.*;
import java.util.Date;

public class BugReportMapper implements Mapper<BugReport> {

    private static Set<BugReport> bugReports = new HashSet<>();
    private Connection connection;
    private ProjectMapper projectMapper;
    private CommentMapper commentMapper;

    public BugReportMapper() throws IOException, SQLException {
        connection = DataGateway.getInstance().getDataSource().getConnection();
        projectMapper = new ProjectMapper();
        commentMapper = new CommentMapper();
    }

    @Override
    public BugReport findByID(int id) throws SQLException {
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
        BugReport report = new BugReport(id, project, creator, description, creationTime);
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
            if (rd.getId() == creatorId) {
                developer = rd;
                break;
            }
        }
        if (developer != null) report.setDeveloper(developer);

        return report;
    }

    @Override
    public List<BugReport> findAll() throws SQLException {
        List<BugReport> all = new ArrayList<>();
        bugReports.clear();

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
            String insertSQL = "INSERT INTO BUGREPORT(project, creator, status, creationTime, description) VALUES (?, ?, ?, ?, ?)";
            PreparedStatement statement = connection.prepareStatement(insertSQL);
            statement.setInt(1, item.getProject().getId());
            statement.setInt(2, item.getCreator().getId());
            statement.setString(3, item.getStatus().name());
            statement.setDate(4, new java.sql.Date(item.getCreationTime().getTime()));
            statement.setString(5, item.getDescription());
            item.setId(statement.executeUpdate());
            bugReports.add(item);
        }

        for (Comment it : item.getComments())
            commentMapper.update(it);

    }

    @Override
    public void closeConnection() throws SQLException {
        projectMapper.closeConnection();
        commentMapper.closeConnection();
        connection.close();
    }
}
