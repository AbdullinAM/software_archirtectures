/**
 * Created by kivi on 22.05.17.
 */

package com.spbpu.facade;

import java.util.Date;
import java.util.List;

public interface Facade {

    boolean authenticate(String login, String password) throws Exception;
    // get projects
    List<String> getManagedProjects(String user) throws Exception;
    List<String> getLeadedProjects(String user) throws Exception;
    List<String> getDevelopedProjects(String user) throws Exception;
    List<String> getTestedProjects(String user) throws Exception;
    List<String> getAllProjects(String user) throws Exception;
    String getRoleForProject(String user, String project) throws Exception;
    // get other projects users
    String getProjectManager(String project) throws Exception;
    String getProjectTeamLeader(String project) throws Exception;
    List<String> getProjectDevelopers(String project) throws Exception;
    List<String> getProjectTesters(String project) throws Exception;
    // get project content
    List<Integer> getProjectTickets(String project) throws Exception;
    List<Integer> getProjectReports(String project) throws Exception;
    List<Integer> getProjectMilestones(String project) throws Exception;
    // get bugreport information
    String getReportProject(Integer report) throws Exception;
    String getReportAuthor(Integer report) throws Exception;
    String getReportAssignee(Integer report) throws Exception;
    String getReportStatus(Integer report) throws Exception;
    Date getReportCreationTime(Integer report) throws Exception;
    String getReportDescription(Integer report) throws Exception;
    List<Pair<String, String>> getReportComments(Integer report) throws Exception;
    // bugreport actions
    Integer createReport(String user, String project, String description) throws Exception;
    boolean reopenReport(String user, String report) throws Exception;
    boolean acceptReport(String user, Integer report) throws Exception;
    boolean fixReport(String user, Integer report) throws Exception;
    boolean closeReport(String user, Integer report) throws Exception;
    // get milestone information
    String getMilestoneProject(Integer milestone) throws Exception;
    String getMilestoneStatus(Integer milestone) throws Exception;
    List<Integer> getMilestoneTickets(Integer milestone) throws Exception;
    Date getMilestoneStartDate(Integer milestone) throws Exception;
    Date getMilestoneEndDate(Integer milstone) throws Exception;
    Date getMilestoneActiveDate(Integer milestone) throws Exception;
    Date getMilestoneClosingDate(Integer milstone) throws Exception;
    // milestone actions
    Integer createMilestone(String user, String project, Date start, Date end) throws Exception;
    boolean activateMilestone(String user, Integer milestone) throws Exception;
    boolean closeMilestone(String user, Integer milestone) throws Exception;
    // get ticket information
    String getTicketProject(Integer ticket) throws Exception;
    Integer getTicketMilestone(Integer ticket) throws Exception;
    String getTicketAuthor(Integer ticket) throws Exception;
    String getTicketStatus(Integer ticket) throws Exception;
    List<String> getTicketAssignees(Integer ticket) throws Exception;
    Date getTicketCreationTime(Integer ticket) throws Exception;
    String getTicketTask(Integer ticket) throws Exception;
    List<Pair<String, String>> getTicketComments(Integer ticket) throws Exception;
    // ticket actions
    Integer createTicket(String user, Integer milestone, Date start, String task) throws Exception;
    boolean reopenTicket(String user, Integer ticket) throws Exception;
    boolean acceptTicket(String user, Integer ticket) throws Exception;
    boolean setTicketInProgress(String user, Integer ticker) throws Exception;
    boolean finishTicket(String user, Integer ticket) throws Exception;
    boolean closeTicket(String user, Integer ticket) throws Exception;
}
