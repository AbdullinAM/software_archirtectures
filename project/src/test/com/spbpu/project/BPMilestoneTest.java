package com.spbpu.project;

import com.spbpu.exceptions.EndBeforeStartException;
import com.spbpu.exceptions.MilestoneTicketsNotCLosedException;
import com.spbpu.exceptions.TwoActiveMilestonesException;
import com.spbpu.storage.StorageRepository;
import com.spbpu.user.Developer;
import com.spbpu.user.Manager;
import com.spbpu.user.TeamLeader;
import com.spbpu.user.Tester;
import junit.framework.TestCase;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.Date;

import static org.junit.Assert.*;

/**
 * Created by Azat on 09.04.2017.
 */
public class BPMilestoneTest extends TestCase {

    Project project;
    Manager manager;
    TeamLeader teamLeader;
    StorageRepository repository;

    @Before
    public void setUp() throws Exception {
        /// Adding users to repository
        repository = new StorageRepository();

        /// Creating project and assigning users
        manager = repository.getManager(repository.getUser("manager"));
        manager.signIn("pass");
        project = manager.createProject("New project");

        manager.setTeamLeader(project, repository.getUser("teamleader"));
        teamLeader = project.getTeamLeader();
        teamLeader.signIn("pass");
    }

    @After
    public void tearDown() throws Exception {
        repository.clear();
        repository = null;
        manager = null;
        teamLeader = null;
    }

    @Test
    public void testMilestoneBP() throws Exception {
        Milestone milestone = manager.createMilestone(project, new Date(2017, 1, 1), new Date(2017, 1, 2));
        assertTrue(milestone.isOpened());
        assertEquals(1, project.getMilestones().size());

        milestone.setActive();
        assertEquals(milestone, project.getActiveMilestone());

        Ticket ticket = manager.createTicket(milestone, "new task");
        ticket.addAssignee(teamLeader);
        teamLeader.acceptTicket(ticket);
        assertEquals(1, milestone.getTickets().size());

        try {
            milestone.setClosed();
            fail("Cannot close milestone while tickets are opened");
        } catch (MilestoneTicketsNotCLosedException e) {
            assertTrue("Cannot close milestone while tickets are opened", true);
        }
        assertFalse(milestone.isClosed());

        teamLeader.closeTicket(ticket);

        try {
            milestone.setClosed();
            assertTrue("Cannot close milestone while tickets are opened", true);
        } catch (MilestoneTicketsNotCLosedException e) {
            assertTrue("Cannot close milestone while tickets are opened", false);
        }
        assertTrue(milestone.isClosed());
    }

    @Test
    public void testMilestoneWrongDates() throws Exception {
        try {
            Milestone milestone = manager.createMilestone(project, new Date(2017, 2, 1), new Date(2017, 1, 2));
            fail("Created wrong milestone");

        } catch (EndBeforeStartException e) {
            assertTrue(e.getMessage(), true);
        }
    }

    @Test
    public void testTwoActiveMilestones() throws Exception {
        Milestone milestone = manager.createMilestone(project, new Date(2017, 1, 1), new Date(2017, 1, 2));
        assertTrue(milestone.isOpened());
        assertEquals(1, project.getMilestones().size());


        Milestone milestone2 = manager.createMilestone(project, new Date(2017, 2, 1), new Date(2017, 2, 2));
        assertTrue(milestone2.isOpened());
        assertEquals(2, project.getMilestones().size());

        try {
            milestone.setActive();
        } catch (Exception e) {
            fail(e.getMessage());
        }

        try {
            milestone2.setActive();
        } catch (TwoActiveMilestonesException e) {
            assertTrue(e.getMessage(), true);
        }
    }
}