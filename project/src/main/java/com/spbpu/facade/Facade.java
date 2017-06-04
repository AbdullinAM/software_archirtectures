/**
 * Created by kivi on 22.05.17.
 */

package com.spbpu.facade;

import java.util.Date;
import java.util.List;

public interface Facade {

    void addUser(String login, String name, String email, String password) throws Exception;
    void authenticate(String login, String password) throws Exception;
    void signOut(String user) throws Exception;
    // get user information
    String getUserEmail(String user) throws Exception;
    String getUserName(String user) throws Exception;
    List<String> getMessages(String user) throws Exception;
    List<Pair<String, Integer>> getAssignedTickets(String user) throws Exception;
    List<Pair<String, Integer>> getAssignedReports(String user) throws Exception;
    // user availabilities
    void createProject(String user, String name) throws Exception;
    // get projects
    List<String> getManagedProjects(String user) throws Exception;
    List<String> getLeadedProjects(String user) throws Exception;
    List<String> getDevelopedProjects(String user) throws Exception;
    List<String> getTestedProjects(String user) throws Exception;
    List<String> getAllProjects(String user) throws Exception;
    Role getRoleForProject(String user, String project) throws Exception;
    // get other projects users
    String getProjectManager(String project) throws Exception;
    String getProjectTeamLeader(String project) throws Exception;
    List<String> getProjectDevelopers(String project) throws Exception;
    List<String> getProjectTesters(String project) throws Exception;
    // get project content
    List<Integer> getProjectTickets(String project) throws Exception;
    List<Integer> getProjectReports(String project) throws Exception;
    List<Integer> getProjectMilestones(String project) throws Exception;
    // set proect
    void setProjectTeamLeader(String manager, String project, String user) throws Exception;
    void addDeveloper(String manager, String project, String user) throws Exception;
    void addTester(String manager, String project, String user) throws Exception;
    // get bugreport information
    String getReportAuthor(String project, Integer report) throws Exception;
    String getReportAssignee(String project, Integer report) throws Exception;
    String getReportStatus(String project, Integer report) throws Exception;
    Date getReportCreationTime(String project, Integer report) throws Exception;
    String getReportDescription(String project, Integer report) throws Exception;
    List<Triple<Date, String, String>> getReportComments(String project, Integer report) throws Exception;
    // bugreport actions
    Integer createReport(String user, String project, String description) throws Exception;
    void reopenReport(String user, String project, Integer report, String comment) throws Exception;
    void acceptReport(String user, String project, Integer report) throws Exception;
    void fixReport(String user, String project, Integer report) throws Exception;
    void closeReport(String user, String project, Integer report) throws Exception;
    // get milestone information
    String getMilestoneStatus(String project, Integer milestone) throws Exception;
    List<Integer> getMilestoneTickets(String project, Integer milestone) throws Exception;
    Date getMilestoneStartDate(String project, Integer milestone) throws Exception;
    Date getMilestoneEndDate(String project, Integer milestone) throws Exception;
    Date getMilestoneActiveDate(String project, Integer milestone) throws Exception;
    Date getMilestoneClosingDate(String project, Integer milestone) throws Exception;
    // milestone actions
    Integer createMilestone(String user, String project, Date start, Date end) throws Exception;
    void activateMilestone(String user, String project, Integer milestone) throws Exception;
    void closeMilestone(String user, String project, Integer milestone) throws Exception;
    // get ticket information
    Integer getTicketMilestone(String project, Integer ticket) throws Exception;
    String getTicketAuthor(String project, Integer ticket) throws Exception;
    String getTicketStatus(String project, Integer ticket) throws Exception;
    List<String> getTicketAssignees(String project, Integer ticket) throws Exception;
    Date getTicketCreationTime(String project, Integer ticket) throws Exception;
    String getTicketTask(String project, Integer ticket) throws Exception;
    List<Triple<Date, String, String>> getTicketComments(String project, Integer ticket) throws Exception;
    // ticket actions
    Integer createTicket(String user, String project, Integer milestone, String task) throws Exception;
    void reopenTicket(String user, String project, Integer ticket, String comment) throws Exception;
    void acceptTicket(String user, String project, Integer ticket) throws Exception;
    void setTicketInProgress(String user, String project, Integer ticket) throws Exception;
    void finishTicket(String user, String project, Integer ticket) throws Exception;
    void closeTicket(String user, String project, Integer ticket) throws Exception;
    // assign developer to ticket
    void addTicketAssignee(String manage, String project, Integer id, String user) throws Exception;
}
