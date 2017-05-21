package com.spbpu;

import com.spbpu.exceptions.AlreadyAcceptedException;
import com.spbpu.exceptions.EndBeforeStartException;
import com.spbpu.exceptions.NoRightsException;
import com.spbpu.exceptions.NotAuthenticatedException;
import com.spbpu.project.BugReport;
import com.spbpu.project.Project;
import com.spbpu.storage.StorageRepository;
import com.spbpu.storage.user.DeveloperMapper;
import com.spbpu.storage.user.ManagerMapper;
import com.spbpu.storage.user.UserMapper;
import com.spbpu.user.Manager;
import com.spbpu.user.TeamLeader;
import com.spbpu.user.User;

import java.io.IOException;
import java.sql.SQLException;

public class Main {

    public static void main(String[] args) throws IOException, SQLException, EndBeforeStartException, NotAuthenticatedException, NoRightsException, AlreadyAcceptedException {
        ManagerMapper mmapper = new ManagerMapper();
        Manager manager = mmapper.findByLogin("user1");
        System.out.println(manager.getId());
        System.out.println(manager.toString());
        for (Project project : manager.getProjects()) {
            System.out.println(project.toString());
            TeamLeader tl = project.getTeamLeader();
            BugReport report = (BugReport) project.getReports().toArray()[0];
            tl.acceptReport(report);
        }
        mmapper.update(manager);
        mmapper.closeConnection();
    }
}
