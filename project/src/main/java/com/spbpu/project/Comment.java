/**
 * Created by Azat on 30.03.2017.
 */

package com.spbpu.project;

import com.spbpu.user.User;

import java.util.Date;

public class Comment {

    private Date date;
    private User commenter;
    private String comment;

    Comment(Date date_, User user_, String comment_) {
        date = date_;
        commenter = user_;
        comment = comment_;
    }

    public Date getDate() {
        return date;
    }

    public User getCommenter() {
        return commenter;
    }

    public String getComment() {
        return comment;
    }

    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("[");
        builder.append(date);
        builder.append("] ");
        builder.append(commenter);
        builder.append(":");
        builder.append(comment);
        return builder.toString();
    }
}
