package com.spbpu.storage.user;

import com.spbpu.project.Message;
import com.spbpu.storage.DataGateway;
import com.spbpu.storage.project.MessageMapper;
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

    private static Set<User> users = new HashSet<>();
    private Connection connection;
    private MessageMapper msgMapper;

    public UserMapper() throws SQLException, IOException {
        msgMapper = new MessageMapper();

        DataGateway gateway = DataGateway.getInstance();
        connection = gateway.getDataSource().getConnection();
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
        int id = rs.getInt("id");
        String name = rs.getString("name");
        String email = rs.getString("email");
        List<Message> messages = msgMapper.findAllForUser(id);

        User newUser = new User(id, login, name, email, messages);
        for (Message it : messages)
            it.setOwner(newUser);

        users.add(newUser);

        return newUser;
    }

    @Override
    public User findByID(int id) throws SQLException {
        for (User it : users) {
            if (it.getId() == id)
                return it;
        }

        // User not found, extract from database
        String selectSQL = "SELECT * FROM USERS WHERE id = ?;";
        PreparedStatement extractUserStatement = connection.prepareStatement(selectSQL);
        extractUserStatement.setInt(1, id);
        ResultSet rs = extractUserStatement.executeQuery();

        if (!rs.next()) return null;

        int uid = rs.getInt("id");
        String login = rs.getString("login");
        String name = rs.getString("name");
        String email = rs.getString("email");
        List<Message> messages = msgMapper.findAllForUser(id);

        User newUser = new User(id, login, name, email, messages);
        for (Message it : messages)
            it.setOwner(newUser);

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
            int id = rs.getInt("id");
            String login = rs.getString("login");
            String name = rs.getString("name");
            String email = rs.getString("email");
            List<Message> messages = msgMapper.findAllForUser(id);
            User newUser = new User(id, login, name, email, messages);
            users.add(newUser);
            all.add(newUser);
        }

        return all;
    }

    @Override
    public void update(User item) throws SQLException {
        if (users.contains(item)) {
            // user itself is immutable, he can only have new messages
            for (Message it : item.getMessages())
                msgMapper.update(it);

        } else {
            String insertSQL = "INSERT INTO USERS(USERS.name, USERS.login, USERS.email, USERS.password) VALUES (?, ?, ?, SHA1(?));";
            PreparedStatement insertStatement = connection.prepareStatement(insertSQL);
            insertStatement.setString(1, item.getName());
            insertStatement.setString(2, item.getLogin());
            insertStatement.setString(3, item.getMailAddress());
            insertStatement.setString(4, "user");
            insertStatement.execute();
        }
    }

    @Override
    public void closeConnection() throws SQLException {
        msgMapper.closeConnection();
        connection.close();
    }
}
