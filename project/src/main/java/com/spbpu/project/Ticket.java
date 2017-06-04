/**
 * Created by Azat on 26.03.2017.
 */
package com.spbpu.project;


import com.spbpu.exceptions.NoRightsException;
import com.spbpu.user.TicketDeveloper;
import com.spbpu.user.TicketManager;

import java.util.ArrayList;
import java.util.Date;

public class Ticket {

    public enum Status {
        NEW,
        ACCEPTED,
        IN_PROGRESS,
        FINISHED,
        CLOSED
    }

    private int id;
    private Milestone milestone;
    private TicketManager creator;
    private Status status;
    private ArrayList<TicketDeveloper> assignees;
    private Date creationTime;
    private String task;
    private ArrayList<Comment> comments;

    public Ticket(Milestone milestone_, TicketManager creator_, String task_) {
        this(-1, milestone_, Status.NEW, creator_, new Date(), task_);
    }

    public Ticket(int id_, Milestone milestone_, Ticket.Status status_, TicketManager creator_, Date creationTime_, String task_) {
        id = id_;
        milestone = milestone_;
        creator = creator_;
        status = status_;
        assignees = new ArrayList<>();
        creationTime = creationTime_;
        task = task_;
        comments = new ArrayList<>();
    }

    public void setId(int id_) { id = id_; }
    public int getId() { return id; }

    public Milestone getMilestone() {
        return milestone;
    }

    public TicketManager getCreator() {
        return creator;
    }

    public ArrayList<TicketDeveloper> getAssignees() {
        return assignees;
    }

    public Date getCreationTime() {
        return creationTime;
    }

    public String getTask() {
        return task;
    }

    public ArrayList<Comment> getComments() {
        return comments;
    }

    public void addAssignee(TicketDeveloper developer) {
        if (!assignees.contains(developer)) {
            assignees.add(developer);
            developer.assign(this);
            developer.notifyNew(this);
        }
    }

    public void addComment(Comment comment) { comments.add(comment); }

    public void addComment(TicketManager manager, String comment) {
        comments.add(new Comment(new Date(), manager.getUser(), comment));
    }

    public void addComment(TicketDeveloper developer, String comment) throws NoRightsException {
        if (!assignees.contains(developer)) throw new NoRightsException(developer.toString() + " cannot change " + toString());
        comments.add(new Comment(new Date(), developer.getUser(), comment));
    }

    public Ticket.Status getStatus() { return status; }

    public boolean isNew()          { return status.equals(Status.NEW); }
    public boolean isAccepted()     { return status.equals(Status.ACCEPTED); }
    public boolean isInProgress()   { return status.equals(Status.IN_PROGRESS); }
    public boolean isFinished()     { return status.equals(Status.FINISHED); }
    public boolean isClosed()       { return status.equals(Status.CLOSED); }

    public void setNew(TicketManager manager, String comment) {
        status = Status.NEW;
        addComment(manager, comment);
    }

    public void setAccepted(TicketDeveloper developer) throws NoRightsException {
        if (!assignees.contains(developer)) throw new NoRightsException(developer.toString() + " cannot change " + toString());
        status = Status.ACCEPTED;
        addComment(developer, "ACCEPTED");
    }

    public void setInProgress(TicketDeveloper developer) throws NoRightsException {
        if (!assignees.contains(developer)) throw new NoRightsException(developer.toString() + " cannot change " + toString());
        status = Status.IN_PROGRESS;
        addComment(developer, "IN PROGRESS");
    }

    public void setFinished(TicketDeveloper developer) throws NoRightsException {
        if (!assignees.contains(developer)) throw new NoRightsException(developer.toString() + " cannot change " + toString());
        status = Status.FINISHED;
        addComment(developer, "FINISHED");
    }

    public void setClosed(TicketManager manager) {
        status = Status.CLOSED;
        addComment(manager, "CLOSED");
    }
}
