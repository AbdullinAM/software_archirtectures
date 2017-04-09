/**
 * Created by Azat on 26.03.2017.
 */

package com.spbpu.user;

import com.spbpu.exceptions.AlreadyAcceptedException;
import com.spbpu.exceptions.NoRightsException;
import com.spbpu.exceptions.NotAuthenticatedException;
import com.spbpu.project.BugReport;
import com.spbpu.project.Project;
import com.spbpu.project.Ticket;

import java.util.ArrayList;
import java.util.List;

public class Developer extends User  implements ReportCreator, ReportDeveloper, TicketDeveloper {

    private Project project;
    private List<Ticket> assignedTickets;
    private List<BugReport> assignedBugReports;


    public Developer(User user, Project project_) {
        super(user);
        project = project_;
        assignedTickets = new ArrayList<>();
        assignedBugReports = new ArrayList<>();
    }

    public List<BugReport> getAssignedBugReports() {
        return assignedBugReports;
    }

    public List<Ticket> getAssignedTickets() {
        return assignedTickets;
    }

    @Override
    public BugReport createReport(String description) throws NotAuthenticatedException {
        checkAuthenticated();
        BugReport report = new BugReport(project, this, description);
        project.addReport(report);
        return report;
    }

    @Override
    public void commentReport(BugReport report, String comment) throws NoRightsException, NotAuthenticatedException {
        checkAuthenticated();
        if (report.getCreator().equals(this))
            report.addComment((ReportCreator) this, comment);
        else
            report.addComment((ReportDeveloper) this, comment);
    }

    @Override
    public void acceptReport(BugReport report) throws AlreadyAcceptedException, NotAuthenticatedException {
        checkAuthenticated();
        report.setAccepted(this);
        if (! assignedBugReports.contains(report)) assignedBugReports.add(report);
    }

    @Override
    public void acceptTicket(Ticket ticket) throws NoRightsException, NotAuthenticatedException {
        checkAuthenticated();
        ticket.setAccepted(this);
        if (! assignedTickets.contains(ticket)) assignedTickets.add(ticket);
    }
}
