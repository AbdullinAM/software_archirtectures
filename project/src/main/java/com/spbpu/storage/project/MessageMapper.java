package com.spbpu.storage.project;

import com.spbpu.project.Message;
import com.spbpu.storage.DataGateway;
import com.spbpu.storage.Mapper;
import com.spbpu.user.User;

import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by kivi on 09.05.17.
 */
public class MessageMapper implements Mapper<Message> {

    private Set<Message> messages;
    private Connection connection;

    public MessageMapper() throws IOException, SQLException {
        messages = new HashSet<>();
        connection = DataGateway.getInstance().getDataSource().getConnection();
    }

    public List<Message> findAllForUser(int user) {
        List<Message> userMessages = new ArrayList<>();
        for (Message it : messages) {
            if (it.getOwner().getId() == user)
                userMessages.add(it);
        }

        return userMessages;
    }

    @Override
    public Message findByID(int id) throws SQLException {
        for (Message it : messages)
            if (it.getId() == id) return it;

        String selectSQL = "SELECT * FROM MESSAGE WHERE id = ?;";
        PreparedStatement selectStatement = connection.prepareStatement(selectSQL);
        selectStatement.setInt(1, id);
        ResultSet rs = selectStatement.executeQuery();

        if (!rs.next()) return null;

        int mid = rs.getInt("id");
        String message = rs.getString("message");

        Message newMessage = new Message(mid, message);
        messages.add(newMessage);
        return newMessage;
    }

    @Override
    public List<Message> findAll() throws SQLException {
        messages.clear();
        List<Message> all = new ArrayList<>();

        String selectSQL = "SELECT * FROM MESSAGE;";
        Statement selectStatement = connection.createStatement();
        ResultSet rs = selectStatement.executeQuery(selectSQL);

        while (rs.next()) {
            int id = rs.getInt("id");
            String message = rs.getString("message");

            Message newMessage = new Message(id, message);
            messages.add(newMessage);
            all.add(newMessage);
        }

        return all;
    }

    @Override
    public void update(Message item) throws SQLException {
        if (messages.contains(item)) {
            // message object is immutable, don't need to update
        } else {
            String insertSQL = "INSERT INTO MESSAGE(MESSAGE.user, MESSAGE.message) VALUES (?, ?);";
            PreparedStatement insertStatement = connection.prepareStatement(insertSQL);
            insertStatement.setInt(1, item.getOwner().getId());
            insertStatement.setString(2, item.getMessage());
            insertStatement.execute();
        }
    }

    @Override
    public void closeConnection() throws SQLException {
        connection.close();
    }
}
