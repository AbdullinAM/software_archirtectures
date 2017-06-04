package com.spbpu.storage;

import com.spbpu.config.ConfigReader;
import com.spbpu.exceptions.IncorrectPasswordException;
import com.spbpu.exceptions.UserAlreadyExistsException;
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
        ConfigReader config = ConfigReader.getInstance();
        try {
            repository.addUser("testuser", "testuser", config.getEmailAccount(), "pass");
            assertTrue("Added user", true);
        } catch (Exception e) {
            assertTrue("Can't add user", false);
        }
        assertNotNull("Added user not found", repository.getUser("testuser"));

        try {
            repository.addUser("testuser", "testuser", config.getEmailAccount(), "pass");
            assertTrue("Added user", false);
        } catch (UserAlreadyExistsException e) {
            assertTrue("Exception thrown", true);
        }
        try {
            repository.addUser("testuser2", "testuser2", config.getEmailAccount(), "pass");
            assertTrue("Added user", true);
        } catch (Exception e) {
            assertTrue("Can't add user", false);
        }

        assertNotNull("Added user not found", repository.getUser("testuser"));
        assertNotNull("Added user not found", repository.getUser("testuser2"));
    }

    @Test
    public void authenticateUserTest() throws Exception {
        ConfigReader config = ConfigReader.getInstance();
        try {
            repository.addUser("devel", "devel", config.getEmailAccount(), "pass");
            assertTrue("Exception not thrown", true);
        } catch (UserAlreadyExistsException e) {
            assertTrue("Exception thrown", false);
        }

        User user = repository.getUser("devel");
        assertNotNull("Added user not found", user);

        try {
            repository.authenticateUser(user, "pass2");
            assertFalse("Authenticated with wrong password", true);
        } catch (IncorrectPasswordException e) {
            assertTrue("Exception thrown", true);
        }
        try {
            repository.authenticateUser(user, "pass");
            assertFalse("Authenticated with correct password", false);
        } catch (IncorrectPasswordException e) {
            assertTrue("Not authenticated with correct password", false);
        }
    }

}