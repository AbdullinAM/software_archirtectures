/**
 * Created by Azat on 26.03.2017.
 */

package com.spbpu.user;

import com.spbpu.project.Ticket;

public interface TicketDeveloper {
    default boolean acceptTicket(Ticket ticket) {
        return ticket.setAccepted(this);
    }

    default boolean setInProgress(Ticket ticket) {
        return ticket.setInProgress(this);
    }

    default boolean finishTicket(Ticket ticket) {
        return ticket.setFinished(this);
    }

    default void commentTicket(Ticket ticket, String comment) {
        ticket.addComment(this, comment);
    }
}
