/**
 * Created by Azat on 26.03.2017.
 */

package com.spbpu.user;

import com.spbpu.exceptions.*;
import com.spbpu.project.Milestone;
import com.spbpu.project.Project;
import com.spbpu.storage.StorageRepository;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Manager extends User implements TicketManager {

    private StorageRepository repository;
    private List<Project> projects;

    public Manager(User user) {
        super(user);
        repository = new StorageRepository();
        projects = new ArrayList<>();
    }

    public Project createProject(String name) throws EndBeforeStartException, DBConnectionException, ProjectAlreadyExistsException {
        Project project = repository.addProject(name, this);//new Project(name, this);
        projects.add(project);
        return project;
    }

    public void addProject(Project project) throws NoRightsException {
        if (!project.getManager().equals(this)) throw new NoRightsException("Can't add project to wrong manager");
        if (!projects.contains(project)) projects.add(project);
    }

    public List<Project> getProjects() { return projects; }

    public Milestone createMilestone(Project project, Date start, Date end) throws Exception {
        if (!projects.contains(project))
            throw new NoRightsException("User " + getLogin() + " cannot change project " + project.getName());
        Milestone milestone = new Milestone(project, start, end);
        project.addMilestone(milestone);
        return milestone;
    }

    public void setActive(Milestone milestone)
            throws TwoActiveMilestonesException, NoRightsException, WrongStatusException {
        if (!projects.contains(milestone.getProject()))
            throw new NoRightsException("User " + getLogin() + " cannot change project " + milestone.getProject().getName());
        milestone.setActive();
    }

    public void closeMilestone(Milestone milestone)
            throws NoRightsException, MilestoneTicketsNotCLosedException, WrongStatusException {
        if (!projects.contains(milestone.getProject()))
            throw new NoRightsException("User " + getLogin() + " cannot change project " + milestone.getProject().getName());
        milestone.setClosed();
    }

    public void setTeamLeader(Project project, User user)
            throws DBConnectionException, EndBeforeStartException, NoRightsException, UserAlreadyHasRoleException {
        if (!projects.contains(project))
            throw new NoRightsException("User " + getLogin() + " cannot change project " + project.getName());
        TeamLeader tl = repository.getTeamLeader(user);
        project.setTeamLeader(tl);
        tl.addProject(project);
    }

    public void addDeveloper(Project project, User user)
            throws DBConnectionException, EndBeforeStartException, NoRightsException, UserAlreadyHasRoleException {
        if (!projects.contains(project))
            throw new NoRightsException("User " + getLogin() + " cannot change project " + project.getName());
        Developer dev = repository.getDeveloper(user);
        project.addDeveloper(dev);
        dev.addProject(project);
    }

    public void addTester(Project project, User user)
            throws DBConnectionException, EndBeforeStartException, NoRightsException, UserAlreadyHasRoleException {
        if (!projects.contains(project))
            throw new NoRightsException("User " + getLogin() + " cannot change project " + project.getName());
        Tester tester = repository.getTester(user);
        project.addTester(tester);
        tester.addProject(project);
    }
}
