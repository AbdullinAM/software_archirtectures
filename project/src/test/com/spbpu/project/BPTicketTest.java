package com.spbpu.project;

import com.spbpu.exceptions.NoRightsException;
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
public class BPTicketTest extends TestCase {

    Project project;
    Manager manager;
    TeamLeader teamLeader;
    Tester tester;
    Developer developer;
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

        manager.addTester(project, repository.getUser("tester"));
        tester = (Tester)project.getTesters().toArray()[0];
        tester.signIn("pass");

        manager.addDeveloper(project, repository.getUser("developer"));
        developer = (Developer)project.getDevelopers().toArray()[0];
        developer.signIn("pass");
    }

    @After
    public void tearDown() throws Exception {
        repository.clear();
        repository = null;
        manager = null;
        teamLeader = null;
        tester = null;
        developer = null;
    }

    @Test
    public void testTicketBP() throws Exception {
        int oldDevMessagesSize = developer.getMessages().size();
        int oldTLMessagesSize = teamLeader.getMessages().size();
        Milestone milestone = manager.createMilestone(project, new Date(2017, 1, 1), new Date(2018, 1,1));

        Ticket ticket = manager.createTicket(milestone, "new task");
        assertTrue(ticket.isNew());

        ticket.addAssignee(teamLeader);
        ticket.addAssignee(developer);
        assertEquals(2, ticket.getAssignees().size());
        assertEquals(teamLeader, ticket.getAssignees().get(0));
        assertEquals(developer, ticket.getAssignees().get(1));

        assertEquals(oldDevMessagesSize + 1, developer.getMessages().size());
        assertEquals(oldTLMessagesSize + 1, teamLeader.getMessages().size());
        assertEquals("New ticket: " + ticket.toString(), developer.getMessages().get(oldDevMessagesSize).getMessage());
        assertEquals("New ticket: " + ticket.toString(), teamLeader.getMessages().get(oldTLMessagesSize).getMessage());

        developer.acceptTicket(ticket);
        assertTrue(ticket.isAccepted());

        teamLeader.setInProgress(ticket);
        assertTrue(ticket.isInProgress());

        developer.finishTicket(ticket);
        assertTrue(ticket.isFinished());

        manager.reopenTicket(ticket, "not closed");
        assertTrue(ticket.isNew());

        developer.acceptTicket(ticket);
        assertTrue(ticket.isAccepted());

        teamLeader.setInProgress(ticket);
        assertTrue(ticket.isInProgress());

        developer.finishTicket(ticket);
        assertTrue(ticket.isFinished());

        teamLeader.closeTicket(ticket);
        assertTrue(ticket.isClosed());
    }


    @Test
    public void testTicketNoRights() throws Exception {
        Milestone milestone = manager.createMilestone(project, new Date(2017, 1, 1), new Date(2018, 1,1));

        Ticket ticket = manager.createTicket(milestone, "new task");
        assertTrue(ticket.isNew());
        ticket.addAssignee(teamLeader);

        try {
            developer.acceptTicket(ticket);
            fail("Wrong developer accepted ticket");
        } catch (NoRightsException e) {
            assertTrue(e.getMessage(), true);
        }
    }
}