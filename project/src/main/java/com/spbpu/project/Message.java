/**
 * Created by kivi on 09.05.17.
 */

package com.spbpu.project;

import com.spbpu.user.User;

public class Message {
    private int id;
    private User owner;
    private String message;

    public Message(User owner_, String message_) {
        message = message_;
        owner = owner_;
    }

    public Message(int id_, String message_) {
        message = message_;
        id = id_;
    }

    public void setOwner(User owner_) {
        owner = owner_;
    }

    public void setId(int id_) { id = id_; }
    public int getId() { return id; }
    public User getOwner() { return owner; }
    public String getMessage() { return message; }

    @Override
    public int hashCode() {
        return id;
    }
    @Override
    public String toString() {
        return "To: " + owner.getName() + "\n" + message;
    }
}
