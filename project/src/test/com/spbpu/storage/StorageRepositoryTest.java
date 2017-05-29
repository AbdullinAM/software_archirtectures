package com.spbpu.storage;

import com.spbpu.config.ConfigReader;
import com.spbpu.exceptions.AlreadyExistsException;
import com.spbpu.user.User;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Created by Azat on 31.03.2017.
 */
public class StorageRepositoryTest {

    StorageRepository repository;

    @Before
    public void setUp() throws Exception {
        repository = new StorageRepository();
    }

    @After
    public void tearDown() throws Exception {
        repository.drop();
        repository.clear();
        repository = null;
    }

    @Test
    public void addUserTest() throws Exception {
        assertTrue("Can't add user", repository.addUser("testuser", "testuser", "testuser@example.com", "pass"));
        assertNotNull("Added user not found", repository.getUser("testuser"));

        try {
            assertFalse("Added user second time", repository.addUser("testuser", "testuser", "testuser@example.com", "pass"));
        } catch (AlreadyExistsException e) {
            assertTrue("Exception thrown", true);
        }
        assertTrue("Can't add user", repository.addUser("testuser2", "testuser2", "testuser2@example.com", "pass"));

        assertNotNull("Added user not found", repository.getUser("testuser"));
        assertNotNull("Added user not found", repository.getUser("testuser2"));
    }

    @Test
    public void authenticateUserTest() throws Exception {
        assertTrue("Can't add user", repository.addUser("devel", "devel", "devel@example.com", "pass"));

        User user = repository.getUser("devel");
        assertNotNull("Added user not found", user);

        assertFalse("Authenticated with wrong password", repository.authenticateUser(user, "pass2"));
        assertTrue("Not authenticated with correct password", repository.authenticateUser(user, "pass"));
    }

}