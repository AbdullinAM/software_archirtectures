package com.spbpu.storage.project;

import com.spbpu.project.Message;
import com.spbpu.project.Ticket;
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

    private static Set<Message> messages = new HashSet<>();
    private Connection connection;

    public MessageMapper() throws IOException, SQLException {
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
        List<Message> all = new ArrayList<>();

        String selectSQL = "SELECT MESSAGE.id FROM MESSAGE;";
        Statement selectStatement = connection.createStatement();
        ResultSet rs = selectStatement.executeQuery(selectSQL);

        while (rs.next()) {
            all.add(findByID(rs.getInt("id")));
        }

        return all;
    }

    @Override
    public void update(Message item) throws SQLException {
        if (messages.contains(item)) {
            // message object is immutable, don't need to update
        } else {
            String insertSQL = "INSERT INTO MESSAGE(MESSAGE.user, MESSAGE.message) VALUES (?, ?);";
            PreparedStatement insertStatement = connection.prepareStatement(insertSQL, Statement.RETURN_GENERATED_KEYS);
            insertStatement.setInt(1, item.getOwner().getId());
            insertStatement.setString(2, item.getMessage());
            insertStatement.execute();
            ResultSet rs = insertStatement.getGeneratedKeys();
            if (rs.next()) {
                long id = rs.getLong(1);
                item.setId((int) id);
            }
            messages.add(item);
        }
    }

    @Override
    public void closeConnection() throws SQLException {
        connection.close();
    }

    @Override
    public void clear() {
        messages.clear();
    }

    @Override
    public void update() throws SQLException {
        for (Message it : messages)
            update(it);
    }
}
