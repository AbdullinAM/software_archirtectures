/**
 * Created by kivi on 22.05.17.
 */

package com.spbpu.facade;

import com.spbpu.exceptions.NoRightsException;
import com.spbpu.project.BugReport;
import com.spbpu.project.Milestone;
import com.spbpu.project.Project;
import com.spbpu.project.Ticket;
import com.spbpu.storage.StorageRepository;
import com.spbpu.user.*;
import javafx.fxml.FXML;
import org.omg.CORBA.TIMEOUT;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

public class FacadeImpl implements Facade {

    private StorageRepository repository;

    public FacadeImpl() {
        repository = new StorageRepository();
    }

    @Override
    public boolean authenticate(String login, String password) throws Exception {
        return repository.authenticateUser(login, password);
    }

    @Override
    public String getUserEmail(String user) throws Exception {
        return repository.getUser(user).getMailAddress();
    }

    @Override
    public String getUserName(String user) throws Exception {
        return repository.getUser(user).getName();
    }

    @Override
    public List<String> getMessages(String user) throws Exception {
        return repository.getUser(user).getMessages().stream().map(message -> message.getMessage()).collect(Collectors.toList());
    }

    @Override
    public boolean createProject(String user, String name) throws Exception {
        if (repository.getProject(name) != null) return false;
        Manager manager = repository.getManager(repository.getUser(user));
        Project project = manager.createProject(name);
        repository.update();
        return true;
    }

    @Override
    public List<String> getManagedProjects(String user) throws Exception {
        Manager manager = repository.getManager(repository.getUser(user));
        return manager.getProjects().stream().
                map(project -> project.getName()).
                collect(Collectors.toList());
    }

    @Override
    public List<String> getLeadedProjects(String user) throws Exception {
        TeamLeader teamLeader = repository.getTeamLeader(repository.getUser(user));
        return teamLeader.getProjects().stream().
                map(project -> project.getName()).
                collect(Collectors.toList());
    }

    @Override
    public List<String> getDevelopedProjects(String user) throws Exception {
        Developer developer = repository.getDeveloper(repository.getUser(user));
        return developer.getProjects().stream().
                map(project -> project.getName()).
                collect(Collectors.toList());
    }

    @Override
    public List<String> getTestedProjects(String user) throws Exception {
        Tester tester = repository.getTester(repository.getUser(user));
        return tester.getProjects().stream().
                map(project -> project.getName()).
                collect(Collectors.toList());
    }

    @Override
    public List<String> getAllProjects(String user) throws Exception {
        List<String> all = getManagedProjects(user);
        all.addAll(getLeadedProjects(user));
        all.addAll(getDevelopedProjects(user));
        all.addAll(getTestedProjects(user));
        return all;
    }

    @Override
    public Role getRoleForProject(String user, String project) throws Exception {
        Project proj = repository.getProject(project);
        User usr = repository.getUser(user);
        if (proj.getManager().equals(usr)) return Role.MANAGER;
        else if (proj.getTeamLeader() != null && proj.getTeamLeader().equals(usr)) return Role.TEAMLEADER;
        else if (proj.getDevelopers().contains(usr)) return Role.DEVELOPER;
        else if (proj.getTesters().contains(usr)) return Role.TESTER;
        return Role.NONE;
    }

    @Override
    public String getProjectManager(String project) throws Exception {
        Project proj = repository.getProject(project);
        return proj.getManager().getLogin();
    }

    @Override
    public String getProjectTeamLeader(String project) throws Exception {
        Project proj = repository.getProject(project);
        return proj.getTeamLeader().getLogin();
    }

    @Override
    public List<String> getProjectDevelopers(String project) throws Exception {
        Project proj = repository.getProject(project);
        return proj.getDevelopers().stream().
                map(developer -> developer.getLogin()).
                collect(Collectors.toList());
    }

    @Override
    public List<String> getProjectTesters(String project) throws Exception {
        Project proj = repository.getProject(project);
        return proj.getTesters().stream().
                map(tester -> tester.getLogin()).
                collect(Collectors.toList());
    }

    @Override
    public List<Integer> getProjectTickets(String project) throws Exception {
        Project proj = repository.getProject(project);
        return proj.getMilestones().stream().
                flatMap(milestone -> milestone.getTickets().stream()).
                map(ticket -> ticket.getId()).
                collect(Collectors.toList());
    }

    @Override
    public List<Integer> getProjectReports(String project) throws Exception {
        Project proj = repository.getProject(project);
        return proj.getReports().stream().
                map(report -> report.getId()).
                collect(Collectors.toList());
    }

    @Override
    public List<Integer> getProjectMilestones(String project) throws Exception {
        Project proj = repository.getProject(project);
        return proj.getMilestones().stream().
                map(milestone -> milestone.getId()).
                collect(Collectors.toList());
    }

    @Override
    public String getReportAuthor(String project, Integer report) throws Exception {
        Project proj = repository.getProject(project);
        for (BugReport it : proj.getReports())
            if (it.getId() == report) return it.getCreator().getUser().getLogin();
        return null;
    }

    @Override
    public String getReportAssignee(String project, Integer report) throws Exception {
        Project proj = repository.getProject(project);
        for (BugReport it : proj.getReports())
            if (it.getId() == report) {
                if (it.getAssignee() != null)
                    return it.getAssignee().getUser().getLogin();
                break;
            }
        return null;
    }

    @Override
    public String getReportStatus(String project, Integer report) throws Exception {
        Project proj = repository.getProject(project);
        for (BugReport it : proj.getReports())
            if (it.getId() == report) return it.getStatus().name();
        return null;
    }

    @Override
    public Date getReportCreationTime(String project, Integer report) throws Exception {
        Project proj = repository.getProject(project);
        for (BugReport it : proj.getReports())
            if (it.getId() == report) return it.getCreationTime();
        return null;
    }

    @Override
    public String getReportDescription(String project, Integer report) throws Exception {
        Project proj = repository.getProject(project);
        for (BugReport it : proj.getReports())
            if (it.getId() == report) return it.getDescription();
        return null;
    }

    @Override
    public List<Pair<String, String>> getReportComments(String project, Integer report) throws Exception {
        Project proj = repository.getProject(project);
        for (BugReport it : proj.getReports())
            if (it.getId() == report)
                return it.getComments().stream().
                        map(comment -> new Pair<String, String>(comment.getCommenter().getLogin(), comment.getComment())).
                        collect(Collectors.toList());
        return null;
    }

    @Override
    public Integer createReport(String user, String project, String description) throws Exception {
        Project proj = repository.getProject(project);
        User usr = repository.getUser(user);
        ReportCreator creator = null;
        for (ReportCreator it : proj.getReportCreators())
            if (it.getId() == usr.getId()) {
                creator = it;
                break;
            }
        if (creator == null) return null;

        BugReport report = creator.createReport(proj, description);
        repository.update();
        return report.getId();
    }

    @Override
    public boolean reopenReport(String user, String project, Integer report, String comment) throws Exception {
        Project proj = repository.getProject(project);
        User usr = repository.getUser(user);
        ReportManager manager = repository.getTester(usr);
        for (BugReport it : proj.getReports())
            if (it.getId() == report) {
                manager.reopenReport(it, comment);
                repository.update();
                return true;
            }
        return false;
    }

    @Override
    public boolean acceptReport(String user, String project, Integer report) throws Exception {
        Project proj = repository.getProject(project);
        User usr = repository.getUser(user);
        ReportDeveloper dev = null;
        for (ReportDeveloper it : proj.getReportDevelopers())
            if (it.getId() == usr.getId()) {
                dev = it;
                break;
            }
        if (dev == null) return false;

        for (BugReport it : proj.getReports())
            if (it.getId() == report) {
                it.setAccepted(dev);
                repository.update();
                return true;
            }
        return false;
    }

    @Override
    public boolean fixReport(String user, String project, Integer report) throws Exception {
        Project proj = repository.getProject(project);
        User usr = repository.getUser(user);
        ReportDeveloper dev = null;
        for (ReportDeveloper it : proj.getReportDevelopers())
            if (it.getId() == usr.getId()) {
                dev = it;
                break;
            }
        if (dev == null) return false;

        for (BugReport it : proj.getReports())
            if (it.getId() == report) {
                it.setFixed(dev);
                repository.update();
                return true;
            }
        return false;
    }

    @Override
    public boolean closeReport(String user, String project, Integer report) throws Exception {
        Project proj = repository.getProject(project);
        User usr = repository.getUser(user);
        ReportManager manager = repository.getTester(usr);
        for (BugReport it : proj.getReports())
            if (it.getId() == report) {
                it.setClosed(manager);
                repository.update();
                return true;
            }
        return false;
    }

    @Override
    public String getMilestoneStatus(String project, Integer milestone) throws Exception {
        Project proj = repository.getProject(project);
        for (Milestone it : proj.getMilestones())
            if (it.getId() == milestone) {
                return it.getStatus().name();
            }
        return null;
    }

    @Override
    public List<Integer> getMilestoneTickets(String project, Integer milestone) throws Exception {
        Project proj = repository.getProject(project);
        for (Milestone it : proj.getMilestones())
            if (it.getId() == milestone) {
                return it.getTickets().stream().
                        map(ticket -> ticket.getId()).
                        collect(Collectors.toList());
            }
        return null;
    }

    @Override
    public Date getMilestoneStartDate(String project, Integer milestone) throws Exception {
        Project proj = repository.getProject(project);
        for (Milestone it : proj.getMilestones())
            if (it.getId() == milestone) {
                return it.getStartDate();
            }
        return null;
    }

    @Override
    public Date getMilestoneEndDate(String project, Integer milestone) throws Exception {
        Project proj = repository.getProject(project);
        for (Milestone it : proj.getMilestones())
            if (it.getId() == milestone) {
                return it.getEndDate();
            }
        return null;
    }

    @Override
    public Date getMilestoneActiveDate(String project, Integer milestone) throws Exception {
        Project proj = repository.getProject(project);
        for (Milestone it : proj.getMilestones())
            if (it.getId() == milestone) {
                return it.getActiveDate();
            }
        return null;
    }

    @Override
    public Date getMilestoneClosingDate(String project, Integer milestone) throws Exception {
        Project proj = repository.getProject(project);
        for (Milestone it : proj.getMilestones())
            if (it.getId() == milestone) {
                return it.getClosingDate();
            }
        return null;
    }

    @Override
    public Integer createMilestone(String user, String project, Date start, Date end) throws Exception {
        Project proj = repository.getProject(project);
        Manager manager = repository.getManager(repository.getUser(user));
        Milestone ml = manager.createMilestone(proj, start, end);
        repository.update();
        return ml.getId();
    }

    @Override
    public boolean activateMilestone(String user, String project, Integer milestone) throws Exception {
        Project proj = repository.getProject(project);
        if (!proj.getManager().getLogin().equals(user)) return false;
        for (Milestone it : proj.getMilestones())
            if (it.getId() == milestone) {
                it.setActive();
                repository.update();
                return true;
            }
        return false;
    }

    @Override
    public boolean closeMilestone(String user, String project, Integer milestone) throws Exception {
        Project proj = repository.getProject(project);
        if (!proj.getManager().getLogin().equals(user)) return false;
        for (Milestone it : proj.getMilestones())
            if (it.getId() == milestone) {
                it.setClosed();
                repository.update();
                return true;
            }
        return false;
    }

    @Override
    public Integer getTicketMilestone(String project, Integer ticket) throws Exception {
        Project proj = repository.getProject(project);
        List<Ticket> all = proj.getMilestones().stream().
                flatMap(milestone -> milestone.getTickets().stream()).
                collect(Collectors.toList());
        for (Ticket it : all)
            if (it.getId() == ticket) return it.getMilestone().getId();
        return null;
    }

    @Override
    public String getTicketAuthor(String project, Integer ticket) throws Exception {
        Project proj = repository.getProject(project);
        List<Ticket> all = proj.getMilestones().stream().
                flatMap(milestone -> milestone.getTickets().stream()).
                collect(Collectors.toList());
        for (Ticket it : all)
            if (it.getId() == ticket) return it.getCreator().getUser().getLogin();
        return null;
    }

    @Override
    public String getTicketStatus(String project, Integer ticket) throws Exception {
        Project proj = repository.getProject(project);
        List<Ticket> all = proj.getMilestones().stream().
                flatMap(milestone -> milestone.getTickets().stream()).
                collect(Collectors.toList());
        for (Ticket it : all)
            if (it.getId() == ticket) return it.getStatus().name();
        return null;
    }

    @Override
    public List<String> getTicketAssignees(String project, Integer ticket) throws Exception {
        Project proj = repository.getProject(project);
        List<Ticket> all = proj.getMilestones().stream().
                flatMap(milestone -> milestone.getTickets().stream()).
                collect(Collectors.toList());
        for (Ticket it : all)
            if (it.getId() == ticket)
                return it.getAssignees().stream().
                        map(assignee -> assignee.getUser().getLogin()).
                        collect(Collectors.toList());
        return null;
    }

    @Override
    public Date getTicketCreationTime(String project, Integer ticket) throws Exception {
        Project proj = repository.getProject(project);
        List<Ticket> all = proj.getMilestones().stream().
                flatMap(milestone -> milestone.getTickets().stream()).
                collect(Collectors.toList());
        for (Ticket it : all)
            if (it.getId() == ticket) return it.getCreationTime();
        return null;
    }

    @Override
    public String getTicketTask(String project, Integer ticket) throws Exception {
        Project proj = repository.getProject(project);
        List<Ticket> all = proj.getMilestones().stream().
                flatMap(milestone -> milestone.getTickets().stream()).
                collect(Collectors.toList());
        for (Ticket it : all)
            if (it.getId() == ticket) return it.getTask();
        return null;
    }

    @Override
    public List<Pair<String, String>> getTicketComments(String project, Integer ticket) throws Exception {
        Project proj = repository.getProject(project);
        List<Ticket> all = proj.getMilestones().stream().
                flatMap(milestone -> milestone.getTickets().stream()).
                collect(Collectors.toList());
        for (Ticket it : all)
            if (it.getId() == ticket)
                return it.getComments().stream().
                        map(comment -> new Pair<String, String>(comment.getCommenter().getLogin(), comment.getComment())).
                        collect(Collectors.toList());
        return null;
    }

    @Override
    public Integer createTicket(String user, String project, Integer milestone, String task) throws Exception {
        Project proj = repository.getProject(project);
        Milestone ml = null;
        for (Milestone it : proj.getMilestones())
            if (it.getId() == milestone) {
                ml = it;
                break;
            }
        if (ml == null) return null;

        for (TicketManager it : proj.getTicketManagers()) {
            if (it.getUser().getLogin().equals(user)) {
                Ticket t = it.createTicket(ml, task);
                repository.update();
                return t.getId();
            }
        }

        return null;
    }

    @Override
    public boolean reopenTicket(String user, String project, Integer ticket, String comment) throws Exception {
        Project proj = repository.getProject(project);
        TicketManager manager = null;
        for (TicketManager it : proj.getTicketManagers())
            if (it.getUser().getLogin().equals(user)) {
                manager = it;
                break;
            }
        if (manager == null) return false;

        List<Ticket> all = proj.getMilestones().stream().
                flatMap(milestone -> milestone.getTickets().stream()).
                collect(Collectors.toList());
        for (Ticket it : all)
            if (it.getId() == ticket) {
                it.setNew(manager, comment);
                repository.update();
                return true;
            }
        return false;
    }

    @Override
    public boolean acceptTicket(String user, String project, Integer ticket) throws Exception {
        Project proj = repository.getProject(project);
        TicketDeveloper dev = null;
        for (TicketDeveloper it : proj.getTicketDevelopers())
            if (it.getUser().getLogin().equals(user)) {
                dev = it;
                break;
            }
        if (dev == null) return false;

        for (Ticket it : dev.getAssignedTickets())
            if (it.getId() == ticket) {
                it.setAccepted(dev);
                repository.update();
                return true;
            }

        return false;
    }

    @Override
    public boolean setTicketInProgress(String user, String project, Integer ticket) throws Exception {
        Project proj = repository.getProject(project);
        TicketDeveloper dev = null;
        for (TicketDeveloper it : proj.getTicketDevelopers())
            if (it.getUser().getLogin().equals(user)) {
                dev = it;
                break;
            }
        if (dev == null) return false;

        for (Ticket it : dev.getAssignedTickets())
            if (it.getId() == ticket) {
                it.setInProgress(dev);
                repository.update();
                return true;
            }

        return false;
    }

    @Override
    public boolean finishTicket(String user, String project, Integer ticket) throws Exception {
        Project proj = repository.getProject(project);
        TicketDeveloper dev = null;
        for (TicketDeveloper it : proj.getTicketDevelopers())
            if (it.getUser().getLogin().equals(user)) {
                dev = it;
                break;
            }
        if (dev == null) return false;

        for (Ticket it : dev.getAssignedTickets())
            if (it.getId() == ticket) {
                it.setFinished(dev);
                repository.update();
                return true;
            }

        return false;
    }

    @Override
    public boolean closeTicket(String user, String project, Integer ticket) throws Exception {
        Project proj = repository.getProject(project);
        TicketManager manager = null;
        for (TicketManager it : proj.getTicketManagers())
            if (it.getUser().getLogin().equals(user)) {
                manager = it;
                break;
            }
        if (manager == null) return false;

        List<Ticket> all = proj.getMilestones().stream().
                flatMap(milestone -> milestone.getTickets().stream()).
                collect(Collectors.toList());
        for (Ticket it : all)
            if (it.getId() == ticket) {
                it.setClosed(manager);
                repository.update();
                return true;
            }
        return false;
    }
}
