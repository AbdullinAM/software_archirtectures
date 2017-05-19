/**
 * Created by Azat on 26.03.2017.
 */

package com.spbpu.user;

import com.spbpu.exceptions.NoRightsException;
import com.spbpu.exceptions.NotAuthenticatedException;
import com.spbpu.project.BugReport;
import com.spbpu.project.Project;

import java.util.ArrayList;
import java.util.List;

public class Tester extends User implements ReportCreator, ReportManager {

    private List<Project> projects;
    private List<BugReport> managedBugReports;

    public Tester(User user) {
        super(user);
        projects = new ArrayList<>();
        managedBugReports = new ArrayList<>();
    }

    public Tester(User user, List<Project> projects_) {
        super(user);
        projects = projects_;
    }

    public List<Project> getProjects() { return projects; }

    public void addProject(Project project) {
        projects.add(project);
    }

    @Override
    public void managing(BugReport report) {
        if(!managedBugReports.contains(report))
            managedBugReports.add(report);
    }

    @Override
    public BugReport createReport(Project project, String description) throws NotAuthenticatedException,
            NoRightsException {
        if (!projects.contains(project))
            throw new NoRightsException(toString() + " cannot add report to " + project.getName());
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
