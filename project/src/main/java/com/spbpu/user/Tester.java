/**
 * Created by Azat on 26.03.2017.
 */

package com.spbpu.user;

import com.spbpu.exceptions.NoRightsException;
import com.spbpu.exceptions.NotAuthenticatedException;
import com.spbpu.project.BugReport;
import com.spbpu.project.Project;

public class Tester extends User implements ReportCreator, ReportManager {

    private Project project;

    public Tester(User user, Project project_) {
        super(user);
        project = project_;
    }

    @Override
    public BugReport createReport(String description) {
        BugReport report = new BugReport(project, this, description);
        project.addReport(report);
        return report;
    }

    @Override
    public void commentReport(BugReport report, String comment) throws NoRightsException {
        if (report.getCreator().equals(this))
            report.addComment((ReportCreator) this, comment);
        else
            report.addComment((ReportManager) this, comment);
    }

}
