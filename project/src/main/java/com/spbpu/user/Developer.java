/**
 * Created by Azat on 26.03.2017.
 */

package com.spbpu.user;

import com.spbpu.project.BugReport;
import com.spbpu.project.Project;

public class Developer extends User implements ReportManager, ReportDeveloper, TicketDeveloper {

    private Project project;

    public Developer(String name_, String login_, String email_, Project project_) {
        super(name_, login_, email_);
        project = project_;
    }

    public Developer(User user, Project project_) {
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

}
