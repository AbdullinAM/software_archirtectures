package com.spbpu;

import com.spbpu.storage.StorageRepository;
import com.spbpu.user.Manager;
import com.spbpu.user.User;

public class Main {

    public static void main(String[] args) {
        StorageRepository repository = new StorageRepository();
        repository.addUser("manager", "Manager", "man@mail.com", "pass");
        repository.addUser("developer", "Developer", "dev@mail.com", "pass");
        repository.addUser("wrongDev", "Developer", "dev@mail.com", "pass");
        repository.addUser("teamleader", "teamleader", "tl@mail.com", "pass");
        repository.addUser("tester", "Tester", "test@mail.com", "pass");

        /// Creating project and assigning users
        Manager manager = new Manager(repository.getUser("manager"));
        User user = repository.getUser("manager");
        System.out.println(user.getLogin() + " " + manager.getLogin());
        manager.signIn("pass");
    }
}
