/**
 * Created by Azat on 26.03.2017.
 */

package user;

import project.BugReport;
import project.Project;
import project.Ticket;

public class TeamLeader extends User implements ReportManager, ReportDeveloper, TicketManager, TicketDeveloper {

    private Project project;

    public TeamLeader(String name_, String login_, String email_, Project project_) {
        super(name_, login_, email_);
        project = project_;
    }

    public TeamLeader(User user, Project project_) {
        this(user.getName(), user.getLogin(), user.getMailAddress(), project_);
    }

    @Override
    public BugReport createReport(String description) {
        BugReport report = new BugReport(project, this, description);
        project.addReport(report);
        return report;
    }

    @Override
    public void commentReport(BugReport report, String comment) {
        if (report.getCreator().equals(this))
            report.addComment((ReportManager)this, comment);
        else
            report.addComment((ReportDeveloper) this, comment);
    }

    @Override
    public void commentTicket(Ticket ticket, String comment) {
        if (ticket.getCreator().equals(this))
            ticket.addComment((TicketManager) this, comment);
        else
            ticket.addComment((TicketDeveloper) this, comment);
    }
}
