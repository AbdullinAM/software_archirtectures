/**
 * Created by Azat on 26.03.2017.
 */

package com.spbpu.user;

import com.spbpu.project.BugReport;
import com.spbpu.project.Project;

public class Tester extends User implements ReportManager {

    private Project project;

    public Tester(String name_, String login_, String email_, Project project_) {
        super(name_, login_, email_);
        project = project_;
    }
    public Tester(User user, Project project_) {
        this(user.getName(), user.getLogin(), user.getMailAddress(), project_);
    }

    @Override
    public BugReport createReport(String description) {
        BugReport report = new BugReport(project, this, description);
        project.addReport(report);
        return report;
    }

}
