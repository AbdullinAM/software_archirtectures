/**
 * Created by Azat on 30.03.2017.
 */

package com.spbpu.user;

import com.spbpu.exceptions.NotAuthenticatedException;
import com.spbpu.project.Message;

import java.util.List;

public interface UserInterface {
    void setId(int id_);
    int getId();
    User getUser();
    void checkAuthenticated() throws NotAuthenticatedException;
    void addMessage(String message);
    List<Message> getMessages();
}
