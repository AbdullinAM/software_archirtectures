package com.spbpu.storage.user;

import com.spbpu.storage.DataGateway;
import com.spbpu.user.User;

import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by kivi on 08.05.17.
 */
public class UserMapper implements UserMapperInterface<User> {

    private Set<User> users;
    private Connection connection;

    public UserMapper() throws SQLException, IOException {
        users = new HashSet<>();

        DataGateway gateway = DataGateway.getInstance();
        connection = gateway.getDataSource().getConnection();
    }

    @Override
    public List<String> getMessagesForUser(int id) throws SQLException {
        List<String> messages = new ArrayList<>();

        String messagesSelectStatement = "SELECT * FROM MESSAGE WHERE user = ?;";
        PreparedStatement extractMessagesStatement = connection.prepareStatement(messagesSelectStatement);
        extractMessagesStatement.setInt(1, id);

        ResultSet rs = extractMessagesStatement.executeQuery();
        while (rs.next()) {
            String message = rs.getString("message");
            messages.add(message);
        }

        return messages;
    }

    @Override
    public User findByLogin(String login) throws SQLException {
        for (User it : users) {
            if (it.getLogin().equals(login))
                return it;
        }

        // User not found, extract from database
        String userSelectStatement = "SELECT * FROM USERS WHERE login = ?;";
        PreparedStatement extractUserStatement = connection.prepareStatement(userSelectStatement);
        extractUserStatement.setString(1, login);
        ResultSet rs = extractUserStatement.executeQuery();

        if (!rs.next()) return null;
        String name = rs.getString("name");
        String email = rs.getString("email");
        List<String> messages = getMessagesForUser(rs.getInt("id"));

        User newUser = new User(login, name, email, messages);
        users.add(newUser);

        return newUser;
    }

    @Override
    public List<User> findAll() throws SQLException {
        users.clear();
        List<User> all = new ArrayList<>();

        String userSelectStatement = "SELECT * FROM USERS;";
        Statement extractUserStatement = connection.createStatement();
        ResultSet rs = extractUserStatement.executeQuery(userSelectStatement);

        while (rs.next()) {
            String login = rs.getString("login");
            String name = rs.getString("name");
            String email = rs.getString("email");
            List<String> messages = getMessagesForUser(rs.getInt("id"));
            User newUser = new User(login, name, email, messages);
            users.add(newUser);
            all.add(newUser);
        }

        return all;
    }

    @Override
    public void update(User item) throws SQLException {

    }

    @Override
    public void closeConnection() throws SQLException {
        connection.close();

    }
}
