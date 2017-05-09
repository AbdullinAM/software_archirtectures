/**
 * Created by kivi on 09.05.17.
 */

package com.spbpu.storage.project;

import com.spbpu.project.Comment;
import com.spbpu.project.Message;
import com.spbpu.storage.DataGateway;
import com.spbpu.storage.Mapper;
import com.spbpu.storage.user.UserMapper;
import com.spbpu.user.User;

import java.io.IOException;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.*;
import java.util.Date;

public class CommentMapper implements Mapper<Comment> {

    private Set<Comment> comments;
    private Connection connection;
    private UserMapper userMapper;

    public CommentMapper() throws IOException, SQLException {
        comments = new HashSet<>();
        userMapper = new UserMapper();
        connection = DataGateway.getInstance().getDataSource().getConnection();
    }

    @Override
    public Comment findByID(int id) throws SQLException {
        for (Comment it : comments)
            if (it.getId() == id) return it;

        String selectSQL = "SELECT * FROM COMMENTS WHERE id = ?;";
        PreparedStatement selectStatement = connection.prepareStatement(selectSQL);
        selectStatement.setInt(1, id);
        ResultSet rs = selectStatement.executeQuery();

        if (!rs.next()) return null;

        int cid = rs.getInt("id");
        Date date = rs.getDate("time");
        User commenter = userMapper.findByID(rs.getInt("commenter"));
        String message = rs.getString("description");

        Comment newComment = new Comment(cid, date, commenter, message);
        comments.add(newComment);
        return newComment;
    }

    @Override
    public List<Comment> findAll() throws SQLException {
        comments.clear();
        List<Comment> all = new ArrayList<>();

        String selectSQL = "SELECT * FROM COMMENTS;";
        Statement selectStatement = connection.createStatement();
        ResultSet rs = selectStatement.executeQuery(selectSQL);

        while (rs.next()) {
            int id = rs.getInt("id");
            Date date = rs.getDate("time");
            User commenter = userMapper.findByID(rs.getInt("commenter"));
            String message = rs.getString("description");

            Comment newComment = new Comment(id, date, commenter, message);
            comments.add(newComment);
            all.add(newComment);
        }

        return all;
    }

    @Override
    public void update(Comment item) throws SQLException {
        if (comments.contains(item)) {
            // message object is immutable, don't need to update
        } else {
            userMapper.update(item.getCommenter());

            String insertSQL = "INSERT INTO COMMENTS(COMMENTS.time, COMMENTS.commenter, COMMENTS.description) VALUES (?, ?, ?);";
            PreparedStatement insertStatement = connection.prepareStatement(insertSQL);
            insertStatement.setDate(1, new java.sql.Date(item.getDate().getTime()));
            insertStatement.setInt(2, item.getCommenter().getId());
            insertStatement.setString(3, item.getComment());
            insertStatement.execute();
        }
    }

    @Override
    public void closeConnection() throws SQLException {
        connection.close();
    }
}
