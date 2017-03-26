/**
 * Created by Azat on 26.03.2017.
 */
package storage;

import project.Project;
import user.Manager;
import user.User;

import java.util.HashMap;

public class StorageRepository {

    private static HashMap<String, User> users = new HashMap<>();
    private static HashMap<User, String> passwds = new HashMap<>();
    private static HashMap<String, Project> projects = new HashMap<>();

    public StorageRepository() {}

    public boolean addUser(String login, String name, String email, String password) {
        if (users.containsKey(login)) return false;

        User newUser = new User(login, name, email);
        synchronized (this) {
            users.put(login, newUser);
            passwds.put(newUser, password);
        }
        return true;
    }

    public boolean authenticateUser(String login, String password) {
        User user = users.get(login);
        return user != null && authenticateUser(user, password);
    }

    public boolean authenticateUser(User user, String password) {
        return passwds.get(user).equals(password);
    }

    public Project addProject(String name, Manager manager) {
        if (projects.containsKey(name)) return null;
        Project project = new Project(name, manager);
        synchronized (projects) {
            projects.put(name, project);
        }
        return project;
    }
}
