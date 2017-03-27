/**
 * Created by Azat on 26.03.2017.
 */

package com.spbpu.user;

import com.spbpu.project.Milestone;
import com.spbpu.project.Project;
import com.spbpu.storage.StorageRepository;

import java.util.ArrayList;
import java.util.Date;

public class Manager extends User implements TicketManager {

    private ArrayList<Project> projects;

    public Manager(String name_, String login_, String email_) {
        super(name_, login_, email_);
    }

    public Manager(User user, Project project_) {
        this(user.getName(), user.getLogin(), user.getMailAddress());
    }

    public Project createProject(String name) {
        Project project = (new StorageRepository()).addProject(name, this);
        return project;
    }

    public Milestone createMilestone(Project project, Date start, Date end) {
        Milestone milestone = new Milestone(project, start, end);
        project.addMilestone(milestone);
        return milestone;
    }

    public boolean setActive(Milestone milestone) {
        return milestone.setActive();
    }

    public boolean closeMilestone(Milestone milestone) {
        return milestone.setClosed();
    }

    public void setTeamLeader(Project project, User user) {
        TeamLeader tl = new TeamLeader(user, project);
        project.setTeamLeader(tl);
    }

    public void addDeveloper(Project project, User user) {
        Developer dev = new Developer(user, project);
        project.addDeveloper(dev);
    }

    public void addTester(Project project, User user) {
        Tester tester = new Tester(user, project);
        project.addTester(tester);
    }
}
