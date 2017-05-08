package com.spbpu.project;

import com.spbpu.exceptions.AlreadyAcceptedException;
import com.spbpu.exceptions.NoRightsException;
import com.spbpu.exceptions.NotAuthenticatedException;
import com.spbpu.storage.StorageRepository;
import com.spbpu.user.Developer;
import com.spbpu.user.Manager;
import com.spbpu.user.TeamLeader;
import com.spbpu.user.Tester;
import junit.framework.TestCase;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import static org.junit.Assert.*;

/**
 * Created by Azat on 30.03.2017.
 */
public class BPBugReportTest extends TestCase {

    Project project;
    Manager manager;
    TeamLeader teamLeader;
    Tester tester;
    Developer developer;
    Developer wrongDev;
    StorageRepository repository;

    @Before
    public void setUp() throws Exception {
        /// Adding users to repository
        repository = new StorageRepository();
        repository.addUser("manager", "Manager", "man@mail.com", "pass");
        repository.addUser("developer", "Developer", "dev@mail.com", "pass");
        repository.addUser("wrongDev", "Developer", "dev@mail.com", "pass");
        repository.addUser("teamleader", "teamleader", "tl@mail.com", "pass");
        repository.addUser("tester", "Tester", "test@mail.com", "pass");

        /// Creating project and assigning users
        manager = new Manager(repository.getUser("manager"));
        manager.signIn("pass");
        project = manager.createProject("New project");

        manager.setTeamLeader(project, repository.getUser("teamleader"));
        teamLeader = project.getTeamLeader();
        teamLeader.signIn("pass");

        manager.addTester(project, repository.getUser("tester"));
        tester = (Tester)project.getTesters().toArray()[0];
        tester.signIn("pass");

        manager.addDeveloper(project, repository.getUser("developer"));
        developer = (Developer)project.getDevelopers().toArray()[0];
        developer.signIn("pass");

        /// Creating other project and developer
        Project project2 = manager.createProject("Other project");
        manager.addDeveloper(project2, repository.getUser("wrongDev"));
        wrongDev = (Developer)project2.getDevelopers().toArray()[0];
        wrongDev.signIn("pass");
    }

    @After
    public void tearDown() throws Exception {
        repository.clear();
        repository = null;
        manager = null;
        teamLeader = null;
        tester = null;
        developer = null;
        wrongDev = null;
    }

    @Test
    public void testBugReportBP() throws Exception {
        BugReport report = teamLeader.createReport("Bug in code");
        assertTrue("Report is new", report.isOpened());

        assertEquals(1, project.getReports().size());
        assertTrue(project.getReports().contains(report));

        assertEquals(1, developer.getMessages().size());
        assertEquals("New bug report: " + report.toString(), developer.getMessages().get(0));

        developer.acceptReport(report);
        assertTrue("Report not accepted", report.isAccepted());

        assertEquals(report, developer.getAssignedBugReports().get(0));

        developer.fixReport(report);
        assertTrue("Report not fixed", report.isFixed());

        tester.reopenReport(report, "Not fixed");
        assertTrue("Report not reopened", report.isOpened());

        developer.acceptReport(report);
        assertTrue("Report not accepted", report.isAccepted());

        developer.fixReport(report);
        assertTrue("Report not fixed", report.isFixed());

        tester.closeReport(report);
        assertTrue("Report not closed", report.isClosed());
    }

    @Test
    public void testAlreadyAccepted() throws Exception {
        BugReport report = teamLeader.createReport("Bug in code");
        assertTrue("Report is new", report.isOpened());

        developer.acceptReport(report);
        assertTrue("Report accepted", report.isAccepted());

        try {
            teamLeader.acceptReport(report);
            fail("Doubly accepted");
        } catch (AlreadyAcceptedException e) {
            assertTrue(e.getMessage(), true);
        }
    }

    @Test
    public void testNoRights() throws Exception {
        BugReport report = teamLeader.createReport("Bug in code");
        assertTrue("Report is new", report.isOpened());

        developer.acceptReport(report);
        assertTrue("Report accepted", report.isAccepted());

        try {
            wrongDev.fixReport(report);
            fail("report changed by someone who has no rights");
        } catch (NoRightsException e) {
            assertTrue(e.getMessage(), true);
        }

        try {
            wrongDev.commentReport(report, "Can't comment this");
            fail("report changed by someone who has no rights");
        } catch (NoRightsException e) {
            assertTrue(e.getMessage(), true);
        }
    }
}