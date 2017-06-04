/**
 * Created by Azat on 26.03.2017.
 */

package com.spbpu.project;

import com.spbpu.exceptions.EndBeforeStartException;
import com.spbpu.exceptions.MilestoneTicketsNotCLosedException;
import com.spbpu.exceptions.TwoActiveMilestonesException;
import com.spbpu.exceptions.WrongStatusException;

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
        this(-1, project_, Status.OPENED, startDate, endDate);
    }

    public Milestone(int id_, Project project_, Milestone.Status status_, Date startDate, Date endDate) throws EndBeforeStartException {
        if (endDate.before(startDate)) throw new EndBeforeStartException("Milestone end is before start");
        id = id_;
        project = project_;
        status = status_;
        this.startDate = startDate;
        this.endDate = endDate;
        tickets = new HashSet<>();

        activeDate = null;
        closingDate = null;
    }

    public void setId(int id_) { id = id_; }
    public int getId() { return id; }

    @Override
    public int hashCode() {return project.hashCode() + startDate.hashCode() + endDate.hashCode();}
    @Override
    public boolean equals(Object obj) {
        if (obj == null) return false;
        Milestone other = (Milestone) obj;
        return project.equals(other.getProject()) &&
                startDate.equals(other.getStartDate()) &&
                endDate.equals(other.getEndDate());
    }

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

    public void setActive() throws TwoActiveMilestonesException, WrongStatusException {
        if (isActive()) return;
        if (!isOpened()) throw new WrongStatusException(getStatus().name(), Status.ACTIVE.name());

        for (Milestone milestone : project.getMilestones()) {
            if (milestone.isActive()) throw new TwoActiveMilestonesException("Attempting to create two active milestones");
        }

        status = Status.ACTIVE;
        activeDate = new Date();
    }

    public void setClosed() throws MilestoneTicketsNotCLosedException, WrongStatusException {
        if (isClosed()) return;
        if (!isActive()) throw new WrongStatusException(getStatus().name(), Status.CLOSED.name());
        for (Ticket t : tickets)
            if (!t.isClosed()) throw new MilestoneTicketsNotCLosedException();
        closingDate = new Date();
        status = Status.CLOSED;
    }
}
