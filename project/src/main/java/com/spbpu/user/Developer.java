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

    private List<Project> projects;
    private List<Ticket> assignedTickets;
    private List<BugReport> managedBugReports;
    private List<BugReport> assignedBugReports;


    public Developer(User user) {
        super(user);
        projects = new ArrayList<>();
        assignedTickets = new ArrayList<>();
        managedBugReports = new ArrayList<>();
        assignedBugReports = new ArrayList<>();
    }

    public Developer(User user, List<Project> projects_) {
        super(user);
        projects = projects_;
        assignedTickets = new ArrayList<>();
        assignedBugReports = new ArrayList<>();
    }

    public void addProject(Project project) {
        projects.add(project);
    }

    public List<Project> getProjects() { return projects; }

    @Override
    public List<BugReport> getAssignedBugReports() {
        return assignedBugReports;
    }

    @Override
    public List<Ticket> getAssignedTickets() {
        return assignedTickets;
    }

    @Override
    public BugReport createReport(Project project, String description) throws NotAuthenticatedException,
            NoRightsException {
        if (!projects.contains(project))
            throw new NoRightsException(toString() + " cannot add report to " + project.getName());
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
    public void assign(BugReport report) {
        if (!assignedBugReports.contains(report))
            assignedBugReports.add(report);
    }

    @Override
    public void managing(BugReport report) {
        if(!managedBugReports.contains(report))
            managedBugReports.add(report);
    }

    @Override
    public void acceptReport(BugReport report) throws AlreadyAcceptedException, NotAuthenticatedException {
        checkAuthenticated();
        report.setAccepted(this);
        if (! assignedBugReports.contains(report)) assignedBugReports.add(report);
    }

    @Override
    public void assign(Ticket ticket) {
        if (!assignedTickets.contains(ticket)) assignedTickets.add(ticket);
    }

    @Override
    public void acceptTicket(Ticket ticket) throws NoRightsException, NotAuthenticatedException {
        checkAuthenticated();
        ticket.setAccepted(this);
        if (! assignedTickets.contains(ticket)) assignedTickets.add(ticket);
    }
}
