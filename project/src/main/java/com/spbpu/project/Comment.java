/**
 * Created by Azat on 30.03.2017.
 */

package com.spbpu.project;

import com.spbpu.user.User;

import java.util.Date;

public class Comment {

    private int id;
    private Date date;
    private User commenter;
    private String comment;

    public Comment(Date date_, User user_, String comment_) {
        date = date_;
        commenter = user_;
        comment = comment_;
    }

    public Comment(int id_, Date date_, User user_, String comment_) {
        this(date_, user_, comment_);
        id = id_;
    }

    public void setId(int id_) { id = id_; }
    public int getId() { return id; }

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

    @Override
    public int hashCode() {
        return comment.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) return false;
        Comment other = (Comment) obj;
        return date.equals(other.getDate()) &&
                commenter.equals(other.getCommenter()) &&
                comment.equals(other.getComment());
    }
}
