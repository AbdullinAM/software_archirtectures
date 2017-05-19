/**
 * Created by Azat on 26.03.2017.
 */

package com.spbpu.project;

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

    public void setId(int id_) { id = id_; }
    public int getId() { return id; }

    public void setTeamLeader(TeamLeader tl) {
        teamLeader = tl;
    }

    public void addDeveloper(Developer d) {
        developers.add(d);
    }

    public void addTester(Tester t) {
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
        reportCreators.add(teamLeader);
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
