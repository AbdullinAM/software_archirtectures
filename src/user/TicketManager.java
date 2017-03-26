/**
 * Created by Azat on 26.03.2017.
 */

package user;

import project.Milestone;
import project.Ticket;

public interface TicketManager {

    default Ticket createTicket(Milestone milestone, String task) {
        Ticket ticket = new Ticket(milestone, this, task);
        milestone.addTicket(ticket);
        return ticket;
    }

    default void commentTicket(Ticket ticket, String comment) {
        ticket.addComment(this, comment);
    }

    default void reopenTicket(Ticket ticket, String comment) {
        ticket.setNew(this, comment);
    }

    default void closeTicket(Ticket ticket) {
        ticket.setClosed(this);
    }

}
