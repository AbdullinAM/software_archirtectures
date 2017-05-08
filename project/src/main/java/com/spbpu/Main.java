package com.spbpu;

import com.spbpu.storage.StorageRepository;
import com.spbpu.storage.user.UserMapper;
import com.spbpu.user.Manager;
import com.spbpu.user.User;

import java.io.IOException;
import java.sql.SQLException;

public class Main {

    public static void main(String[] args) throws IOException, SQLException {
        UserMapper mapper = new UserMapper();
        mapper.findByLogin("user1");
    }
}
