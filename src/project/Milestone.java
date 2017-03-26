/**
 * Created by Azat on 26.03.2017.
 */

package project;

import java.util.Date;
import java.util.HashSet;

public class Milestone {

    public enum Status {
        OPENED,
        ACTIVE,
        CLOSED
    }

    private Project project;
    private Status status;
    private Date startDate;
    private Date activeDate;
    private Date endDate;
    private Date closingDate;
    private HashSet<Ticket> tickets;

    public Milestone(Project project_, Date startDate, Date endDate) {
        project = project_;
        status = Status.OPENED;
        this.startDate = startDate;
        this.endDate = endDate;
        tickets = new HashSet<>();

        activeDate = null;
        closingDate = null;
    }

    public Project getProject() {
        return project;
    }

    public void addTicket(Ticket ticket) {
        assert ticket.getMilestone().equals(this);
        tickets.add(ticket);
    }

    public Date getStartDate() {
        return startDate;
    }

    public Date getActiveDate() {
        return activeDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public Date getClosingDate() {
        return closingDate;
    }

    public boolean isOpened() { return status.equals(Status.OPENED); }
    public boolean isActive() { return status.equals(Status.ACTIVE); }
    public boolean isClosed() { return status.equals(Status.CLOSED); }

    public boolean setActive() {
        if (isActive()) return true;
        status = Status.ACTIVE;
        activeDate = new Date();
        return true;
    }

    public boolean setClosed() {
        if (isClosed()) return true;
        for (Ticket t : tickets)
            if (!t.isClosed()) return false;
        closingDate = new Date();
        return true;
    }
}
