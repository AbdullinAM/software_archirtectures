/**
 * Created by Azat on 26.03.2017.
 */

package com.spbpu.project;

import com.spbpu.exceptions.EndBeforeStartException;
import com.spbpu.exceptions.TwoActiveMilestonesException;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

public class Milestone {

    public enum Status {
        OPENED,
        ACTIVE,
        CLOSED
    }

    private int id;
    private Project project;
    private Status status;
    private Date startDate;
    private Date activeDate;
    private Date endDate;
    private Date closingDate;
    private Set<Ticket> tickets;

    public Milestone(Project project_, Date startDate, Date endDate) throws EndBeforeStartException {
        this(-1, project_, startDate, endDate);
    }

    public Milestone(int id_, Project project_, Date startDate, Date endDate) throws EndBeforeStartException {
        if (endDate.before(startDate)) throw new EndBeforeStartException("Milestone end is before start");
        id = id_;
        project = project_;
        status = Status.OPENED;
        this.startDate = startDate;
        this.endDate = endDate;
        tickets = new HashSet<>();

        activeDate = null;
        closingDate = null;
    }

    public void setId(int id_) { id = id_; }
    public int getId() { return id; }

    public Milestone.Status getStatus() { return status; }

    public Project getProject() {
        return project;
    }

    public void setTickets(Set<Ticket> tickets_) { tickets = tickets_; }
    public Set<Ticket> getTickets() {
        return tickets;
    }

    public void addTicket(Ticket ticket) {
        assert ticket.getMilestone().equals(this);
        tickets.add(ticket);
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setActiveDate(Date date) {
        activeDate = date;
    }
    public Date getActiveDate() {
        return activeDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setClosingDate(Date date) {
        closingDate = date;
    }
    public Date getClosingDate() {
        return closingDate;
    }

    public boolean isOpened() { return status.equals(Status.OPENED); }
    public boolean isActive() { return status.equals(Status.ACTIVE); }
    public boolean isClosed() { return status.equals(Status.CLOSED); }

    public boolean setActive() throws TwoActiveMilestonesException {
        if (isActive()) return true;

        for (Milestone milestone : project.getMilestones()) {
            if (milestone.isActive()) throw new TwoActiveMilestonesException("Attempting to create two active milestones");
        }

        status = Status.ACTIVE;
        activeDate = new Date();
        return true;
    }

    public boolean setClosed() {
        if (isClosed()) return true;
        for (Ticket t : tickets)
            if (!t.isClosed()) return false;
        closingDate = new Date();
        status = Status.CLOSED;
        return true;
    }
}
