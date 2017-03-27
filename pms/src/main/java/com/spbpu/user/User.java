/**
 * Created by Azat on 26.03.2017.
 */

package com.spbpu.user;

import com.spbpu.storage.StorageRepository;

public class User {

    private String name;
    private String login;
    private String email;
    private boolean authenticated;

    public User(String name_, String login_, String email_) {
        name = name_;
        login = login_;
        email = email_;
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

    public boolean signIn(String password) {
        assert (!authenticated);
        authenticated = (new StorageRepository()).authenticateUser(login, password);
        return authenticated;
    }

    public boolean isSignedIn() {
        return authenticated;
    }

    public boolean signOut() {
        assert (authenticated);
        authenticated = false;
        return true;
    }

    public String toString() {
        return  login + ":" + name + "<" + email + ">";
    }

}
