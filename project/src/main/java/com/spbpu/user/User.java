/**
 * Created by Azat on 26.03.2017.
 */

package com.spbpu.user;

import com.spbpu.exceptions.DBConnectionException;
import com.spbpu.exceptions.IncorrectPasswordException;
import com.spbpu.exceptions.NotAuthenticatedException;
import com.spbpu.project.Message;
import com.spbpu.storage.StorageRepository;

import java.util.ArrayList;
import java.util.List;

public class User implements UserInterface {

    private int id;
    private String login;
    private String name;
    private String email;
    private boolean authenticated;
    private List<Message> messages;

    public User(int id_, String login_, String name_, String email_, List<Message> messages_) {
        id = id_;
        login = login_;
        name = name_;
        email = email_;
        authenticated = false;
        messages = messages_;
    }

    public User(User user) {
        id = user.id;
        login = user.login;
        name = user.name;
        email = user.email;
        authenticated = user.authenticated;
        messages = new ArrayList<>();
    }

    public String getLogin() {
        return login;
    }

    public String getName() {
        return name;
    }

    public String getMailAddress() {
        return email;
    }

    public void signIn(String password) throws DBConnectionException, IncorrectPasswordException {
        (new StorageRepository()).authenticateUser(this, password);
        authenticated = true;
    }

    public boolean isAuthenticated() {
        return authenticated;
    }

    public void signOut() {
        authenticated = false;
    }

    public String toString() {
        return  login + ":" + name + "<" + email + ">";
    }

    @Override
    public void setId(int id_) {
        id = id_;
    }

    @Override
    public int getId() { return id; }

    @Override
    public User getUser() {
        return this;
    }

    @Override
    public void checkAuthenticated() throws NotAuthenticatedException {
        if (isAuthenticated()) return;
        throw new NotAuthenticatedException(toString() + " is not authenticated");
    }

    @Override
    public void addMessage(String message) {
        Message m = new Message(this, message);
        messages.add(m);
    }

    @Override
    public List<Message> getMessages() {
        return messages;
    }

    @Override
    public boolean equals(Object obj) {
        if ( (obj == null) ) return false;
        User other = (User)obj;
        return login.equals(other.login);
    }

    @Override
    public int hashCode() {
        return login.hashCode();
    }
}
