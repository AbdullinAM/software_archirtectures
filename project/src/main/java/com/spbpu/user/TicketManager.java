/**
 * Created by Azat on 26.03.2017.
 */

package com.spbpu.user;

import com.spbpu.exceptions.MilestoneAlreadyClosedException;
import com.spbpu.exceptions.NoRightsException;
import com.spbpu.exceptions.NotAuthenticatedException;
import com.spbpu.project.Milestone;
import com.spbpu.project.Project;
import com.spbpu.project.Ticket;

public interface TicketManager  extends UserInterface {

    default Ticket createTicket(Milestone milestone, String task) throws NotAuthenticatedException, MilestoneAlreadyClosedException {
        checkAuthenticated();
        if (milestone.isClosed()) throw new MilestoneAlreadyClosedException();
        Ticket ticket = new Ticket(milestone, this, task);
        milestone.addTicket(ticket);
        return ticket;
    }

    default void addAssignee(Ticket ticket, TicketDeveloper developer) throws NotAuthenticatedException, NoRightsException {
        checkAuthenticated();
        Project project = ticket.getMilestone().getProject();
        if (!project.getTicketDevelopers().contains(developer))
            throw new NoRightsException("User " + developer.getUser().getLogin() + " has no rights for project " + project.getName());
        ticket.addAssignee(developer);
    }

    default void commentTicket(Ticket ticket, String comment) throws NoRightsException, NotAuthenticatedException {
        checkAuthenticated();
        ticket.addComment(this, comment);
    }

    default void reopenTicket(Ticket ticket, String comment) throws NotAuthenticatedException {
        checkAuthenticated();
        ticket.setNew(this, comment);
    }

    default void closeTicket(Ticket ticket) throws NotAuthenticatedException {
        checkAuthenticated();
        ticket.setClosed(this);
    }

}
