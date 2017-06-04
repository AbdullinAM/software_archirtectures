/**
 * Created by Azat on 26.03.2017.
 */

package com.spbpu.project;

import com.spbpu.exceptions.UserAlreadyHasRoleException;
import com.spbpu.user.*;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Project {

    private int id;
    private String name;
    private Manager manager;
    private TeamLeader teamLeader;
    private Set<Developer> developers;
    private Set<Tester> testers;
    private Set<Milestone> milestones;
    private Set<BugReport> reports;

    public Project(String name_, Manager manager_) {
        name = name_;
        manager = manager_;
        developers = new HashSet<>();
        testers = new HashSet<>();
        milestones = new HashSet<>();
        reports = new HashSet<>();
    }

    public Project(int id_, String name_, Manager manager_) {
        id = id_;
        name = name_;
        manager = manager_;
        developers = new HashSet<>();
        testers = new HashSet<>();
        milestones = new HashSet<>();
        reports = new HashSet<>();
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Name: " + name + "\n");
        sb.append("Manager: " + manager.getName() + "\n");
        sb.append("TeamLeader: " + ((teamLeader == null) ? "not set" : teamLeader.getName()) + "\n");
        for (Developer dev : developers)
            sb.append("Developer: " + dev.getName() + "\n");
        for (Tester tester : testers)
            sb.append("Tester: " + tester.getName() + "\n");
        for (Milestone milestone : milestones)
            sb.append("Milestone: " + milestone.toString() + "\n");
        for (BugReport report : reports)
            sb.append("Report: " + report.toString() + "\n");
        return sb.toString();
    }

    public void setId(int id_) { id = id_; }
    public int getId() { return id; }

    public boolean containsUser(User usr) {
        if (manager.equals(usr)) return true;
        else if (teamLeader != null && teamLeader.equals(usr)) return true;
        else if (developers.contains(usr)) return true;
        else if (testers.contains(usr)) return true;
        return false;
    }

    public void setTeamLeader(TeamLeader tl) throws UserAlreadyHasRoleException {
        if (containsUser(tl)) throw new UserAlreadyHasRoleException(tl.getLogin(), getName());
        teamLeader = tl;
    }

    public void addDeveloper(Developer d) throws UserAlreadyHasRoleException {
        if (containsUser(d)) throw new UserAlreadyHasRoleException(d.getLogin(), getName());
        developers.add(d);
    }

    public void addTester(Tester t) throws UserAlreadyHasRoleException {
        if (containsUser(t)) throw new UserAlreadyHasRoleException(t.getLogin(), getName());
        testers.add(t);
    }

    public void addReport(BugReport report) {
        assert report.getProject().equals(this);
        reports.add(report);
    }

    public void addMilestone(Milestone milestone) {
        assert milestone.getProject().equals(this);
        milestones.add(milestone);
    }

    public Milestone getActiveMilestone() {
        for (Milestone milestone : milestones)
            if (milestone.isActive()) return milestone;

        return null;
    }

    @Override
    public int hashCode() {
        return name.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) return false;
        Project project = (Project) obj;
        return name.equals(project.getName());
    }

    public String getName() { return name; }

    public Manager getManager() {
        return manager;
    }

    public TeamLeader getTeamLeader() {
        return teamLeader;
    }

    public Set<Developer> getDevelopers() {
        return developers;
    }

    public Set<Tester> getTesters() {
        return testers;
    }

    public Set<Milestone> getMilestones() {
        return milestones;
    }

    public Set<BugReport> getReports() {
        return reports;
    }

    public List<ReportCreator> getReportCreators() {
        List<ReportCreator> reportCreators = new ArrayList<>();
        if (teamLeader != null) reportCreators.add(teamLeader);
        for (Developer dev : developers) {
            reportCreators.add(dev);
        }
        for (Tester tester : testers) {
            reportCreators.add(tester);
        }
        return reportCreators;
    }

    public List<ReportDeveloper> getReportDevelopers() {
        List<ReportDeveloper> reportDevelopers = new ArrayList<>();
        reportDevelopers.add(teamLeader);
        for (Developer dev : developers) {
            reportDevelopers.add(dev);
        }
        return reportDevelopers;
    }

    public List<TicketManager> getTicketManagers() {
        List<TicketManager> managers = new ArrayList<>();
        managers.add(manager);
        if (teamLeader != null) managers.add(teamLeader);
        return managers;
    }

    public List<TicketDeveloper> getTicketDevelopers() {
        List<TicketDeveloper> devs = new ArrayList<>();
        if (teamLeader != null) devs.add(teamLeader);
        for (Developer developer : developers)
            devs.add(developer);

        return devs;
    }

}
