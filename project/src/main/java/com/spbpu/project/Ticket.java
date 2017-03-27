/**
 * Created by Azat on 26.03.2017.
 */
package com.spbpu.project;


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

    private Milestone milestone;
    private TicketManager creator;
    private Status status;
    private ArrayList<TicketDeveloper> assignees;
    private Date creationTime;
    private String task;
    private ArrayList<String> comments;

    public Ticket(Milestone milestone_, TicketManager creator_, String task_) {
        this(milestone_, creator_, new Date(), task_);
    }

    public Ticket(Milestone milestone_, TicketManager creator_, Date creationTime_, String task_) {
        milestone = milestone_;
        creator = creator_;
        status = Status.NEW;
        assignees = new ArrayList<>();
        creationTime = creationTime_;
        task = task_;
        comments = new ArrayList<>();
    }

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

    public ArrayList<String> getComments() {
        return comments;
    }

    public void addAssignee(TicketDeveloper developer) {
        if (!assignees.contains(developer))
            assignees.add(developer);
    }

    public void addComment(TicketManager manager, String comment) {
        comments.add("[" + (new Date()).toString() + "] Manager " + manager.toString() + ": " + comment);
    }

    public void addComment(TicketDeveloper developer, String comment) {
        comments.add("[" + (new Date()).toString() + "] Developer " + developer.toString() + ": " + comment);
    }

    public boolean isNew()          { return status.equals(Status.NEW); }
    public boolean isAccepted()     { return status.equals(Status.ACCEPTED); }
    public boolean isInProgress()   { return status.equals(Status.IN_PROGRESS); }
    public boolean isFinished()     { return status.equals(Status.FINISHED); }
    public boolean isClosed()       { return status.equals(Status.CLOSED); }

    public void setNew(TicketManager manager, String comment) {
        status = Status.NEW;
        addComment(manager, comment);
    }

    public boolean setAccepted(TicketDeveloper developer) {
        if (!assignees.contains(developer)) return false;
        status = Status.ACCEPTED;
        addComment(developer, "ACCEPTED");
        return true;
    }

    public boolean setInProgress(TicketDeveloper developer) {
        if (!assignees.contains(developer)) return false;
        status = Status.IN_PROGRESS;
        addComment(developer, "IN PROGRESS");
        return true;
    }

    public boolean setFinished(TicketDeveloper developer) {
        if (!assignees.contains(developer)) return false;
        status = Status.FINISHED;
        addComment(developer, "FINISHED");
        return true;
    }

    public void setClosed(TicketManager manager) {
        status = Status.CLOSED;
        addComment(manager, "CLOSED");
    }
}
