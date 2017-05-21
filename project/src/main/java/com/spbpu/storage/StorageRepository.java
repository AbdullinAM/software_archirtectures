/**
 * Created by Azat on 26.03.2017.
 */
package com.spbpu.storage;

import com.spbpu.project.Project;
import com.spbpu.user.Manager;
import com.spbpu.user.User;

import java.util.HashMap;
import java.util.Map;

public class StorageRepository {

    private static Map<String, User> users = new HashMap<>();
    private static Map<String, String> passwds = new HashMap<>();
    private static Map<String, Project> projects = new HashMap<>();

    public StorageRepository() {}

    public boolean addUser(String login, String name, String email, String password) {
        if (users.containsKey(login)) return false;

        User newUser = new User(0, login, name, email, null);
        synchronized (this) {
            users.put(login, newUser);
            passwds.put(login, password);
        }
        return true;
    }

    public User getUser(String login) {
        return users.get(login);
    }

    public boolean authenticateUser(String login, String password) {
        User user = users.get(login);
        return user != null && authenticateUser(user, password);
    }

    public boolean authenticateUser(User user, String password) {
        return true;
    }

    public Project addProject(String name, Manager manager) {
        if (projects.containsKey(name)) return null;
        Project project = new Project(name, manager);
        synchronized (projects) {
            projects.put(name, project);
        }
        return project;
    }

    synchronized public void clear() {
        users.clear();
        passwds.clear();
        projects.clear();
    }
}
