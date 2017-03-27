/**
 * Created by Azat on 26.03.2017.
 */

package com.spbpu.project;

import com.spbpu.user.ReportDeveloper;
import com.spbpu.user.ReportManager;

import java.util.ArrayList;
import java.util.Date;

public class BugReport {

    public enum Status {
        OPENED,
        ACCEPTED,
        FIXED,
        CLOSED
    }

    private Project project;
    private ReportManager creator;
    private Status status;
    private Date creationTime;
    private String description;
    private ArrayList<String> comments;

    public BugReport(Project project_, ReportManager creator_, String description_) {
        this(project_, creator_, description_, new Date());
    }

    public BugReport(Project project_, ReportManager creator_, String description_, Date creationTime_) {
        project = project_;
        creator = creator_;
        status = Status.OPENED;
        creationTime = creationTime_;
        description = description_;
        comments = new ArrayList<>();
    }

    public Project getProject() {
        return project;
    }

    public BugReport.Status getStatus() {
        return status;
    }

    public ReportManager getCreator() {
        return creator;
    }

    public Date getCreationTime() {
        return creationTime;
    }

    public String getDescription() {
        return description;
    }

    public ArrayList<String> getComments() {
        return comments;
    }

    public void addComment(ReportManager manager, String d) {
        comments.add("[" + (new Date()).toString() + "] Manager " + manager.toString() + ": "+ d);
    }

    public void addComment(ReportDeveloper developer, String d) {
        comments.add("[" + (new Date()).toString() + "] Developer " + developer.toString() + ": "+ d);
    }

    public boolean isOpened()   { return status.equals(Status.OPENED); }
    public boolean isAccepted() { return status.equals(Status.ACCEPTED); }
    public boolean isFixed()    { return status.equals(Status.FIXED); }
    public boolean isClosed()   { return status.equals(Status.CLOSED); }

    public void setOpened(ReportManager manager, String description) {
        status = Status.OPENED;
        addComment(manager, "OPENED");
        if (description != null && !description.isEmpty()) {
            addComment(manager, "Comment: " + description);
        }
    }

    public void setAccepted(ReportDeveloper developer) {
        status = Status.ACCEPTED;
        addComment(developer, "ACCEPTED");
    }

    public void setFixed(ReportDeveloper developer) {
        status = Status.FIXED;
        addComment(developer, "FIXED");
    }

    public void setClosed(ReportManager manager) {
        status = Status.CLOSED;
        addComment(manager,"CLOSED");
    }

}
