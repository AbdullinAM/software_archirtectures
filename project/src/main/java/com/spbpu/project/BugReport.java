/**
 * Created by Azat on 26.03.2017.
 */

package com.spbpu.project;

import com.spbpu.exceptions.AlreadyAcceptedException;
import com.spbpu.exceptions.NoRightsException;
import com.spbpu.user.ReportDeveloper;
import com.spbpu.user.ReportCreator;
import com.spbpu.user.ReportManager;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class BugReport {

    public enum Status {
        OPENED,
        ACCEPTED,
        FIXED,
        CLOSED
    }

    private int id;
    private Project project;
    private ReportCreator creator;
    private ReportDeveloper assignee;
    private Status status;
    private Date creationTime;
    private String description;
    private List<Comment> comments;

    public BugReport(Project project_, ReportCreator creator_, String description_) {
        this(-1, project_, creator_, Status.OPENED, description_, new Date());
    }

    public BugReport(int id_, Project project_, ReportCreator creator_, BugReport.Status status_, String description_, Date creationTime_) {
        id = id_;
        project = project_;
        creator = creator_;
        assignee = null;
        status = status_;
        creationTime = creationTime_;
        description = description_;
        comments = new ArrayList<>();

        for (ReportDeveloper dev : project.getReportDevelopers()) {
            dev.notifyNew(this);
        }
    }

    public void setId(int id_) { id = id_; }
    public int getId() { return id; }

    public void addComment(Comment comment) { comments.add(comment); }
    public void setDeveloper(ReportDeveloper developer) { assignee = developer; }

    public Project getProject() {
        return project;
    }

    public BugReport.Status getStatus() {
        return status;
    }

    public ReportCreator getCreator() {
        return creator;
    }

    public ReportDeveloper getAssignee() { return assignee; }

    public Date getCreationTime() {
        return creationTime;
    }

    public String getDescription() {
        return description;
    }

    public List<Comment> getComments() {
        return comments;
    }

    public void addComment(ReportCreator creator_, String d) throws NoRightsException {
        if (!creator.equals(creator_)) throw new NoRightsException(creator_.toString() + " cannot comment " + toString());
        comments.add(new Comment(new Date(), creator_.getUser(), d));
    }

    public void addComment(ReportManager manager, String d) {
        comments.add(new Comment(new Date(), manager.getUser(), d));
    }

    public void addComment(ReportDeveloper developer, String d) throws NoRightsException {
        if (!assignee.equals(developer)) throw new NoRightsException(developer.toString() + " cannot comment " + toString());
        comments.add(new Comment(new Date(), developer.getUser(), d));
    }

    public boolean isOpened()   { return status.equals(Status.OPENED); }
    public boolean isAccepted() { return status.equals(Status.ACCEPTED); }
    public boolean isFixed()    { return status.equals(Status.FIXED); }
    public boolean isClosed()   { return status.equals(Status.CLOSED); }

    public void setReopened(ReportManager manager, String description) {
        status = Status.OPENED;
        addComment(manager, "OPENED");
        if (description != null && !description.isEmpty()) {
            addComment(manager, "Comment: " + description);
        }
    }

    public void setAccepted(ReportDeveloper developer) throws AlreadyAcceptedException {
        if (assignee != null && !assignee.equals(developer)) throw new AlreadyAcceptedException(assignee.toString() + " already accepted this report");

        status = Status.ACCEPTED;
        assignee = developer;
        try {
            addComment(developer, "ACCEPTED");
        } catch (NoRightsException e) {
            assert false;
        }
    }

    public void setFixed(ReportDeveloper developer) throws NoRightsException {
        if (!assignee.equals(developer)) throw new NoRightsException(developer.toString() + " cannot change " + toString());

        status = Status.FIXED;
        addComment(developer, "FIXED");
    }

    public void setClosed(ReportManager manager) {
        status = Status.CLOSED;
        addComment(manager,"CLOSED");
    }

}
