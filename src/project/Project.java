/**
 * Created by Azat on 26.03.2017.
 */

package project;

import user.*;

import java.util.HashSet;

public class Project {

    private String name;
    private Manager manager;
    private TeamLeader teamLeader;
    private HashSet<Developer> developers;
    private HashSet<Tester> testers;
    private HashSet<Milestone> milestones;
    private HashSet<BugReport> reports;

    public Project(String name_, Manager manager_) {
        name = name_;
        manager = manager_;
        developers = new HashSet<>();
        testers = new HashSet<>();
        milestones = new HashSet<>();
        reports = new HashSet<>();
    }

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
    }

    public Manager getManager() {
        return manager;
    }

    public TeamLeader getTeamLeader() {
        return teamLeader;
    }

    public HashSet<Developer> getDevelopers() {
        return developers;
    }

    public HashSet<Tester> getTesters() {
        return testers;
    }

    public HashSet<Milestone> getMilestones() {
        return milestones;
    }

}
