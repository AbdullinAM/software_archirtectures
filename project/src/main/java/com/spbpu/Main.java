package com.spbpu;

import com.spbpu.storage.StorageRepository;
import com.spbpu.storage.user.DeveloperMapper;
import com.spbpu.storage.user.ManagerMapper;
import com.spbpu.storage.user.UserMapper;
import com.spbpu.user.Manager;
import com.spbpu.user.User;

import java.io.IOException;
import java.sql.SQLException;

public class Main {

    public static void main(String[] args) throws IOException, SQLException {
        UserMapper mapper = new UserMapper();
        ManagerMapper mmapper = new ManagerMapper();
        User user = mapper.findByLogin("user1");
        Manager manager = mmapper.findByLogin("user1");
        System.out.println(user.getId());
        System.out.println(user.toString());
        System.out.println(manager.getId());
        System.out.println(manager.toString());
        mapper.closeConnection();
    }
}
