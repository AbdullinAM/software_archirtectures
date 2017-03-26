/**
 * Created by Azat on 26.03.2017.
 */

package user;

import project.Milestone;
import project.Project;
import storage.StorageRepository;

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
}
