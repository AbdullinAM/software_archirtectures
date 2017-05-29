/**
 * Created by Azat on 26.03.2017.
 */

package com.spbpu.user;

import com.spbpu.exceptions.AlreadyAcceptedException;
import com.spbpu.exceptions.NoRightsException;
import com.spbpu.exceptions.NotAuthenticatedException;
import com.spbpu.project.BugReport;

import java.util.List;

public interface ReportDeveloper extends UserInterface {

    void assign(BugReport report);

    default void notifyNew(BugReport report) {
        StringBuilder builder = new StringBuilder();
        builder.append("New bug report: ");
        builder.append(report.toString());
        addMessage(builder.toString());
    }

    default void acceptReport(BugReport report) throws AlreadyAcceptedException, NotAuthenticatedException {
        checkAuthenticated();
        report.setAccepted(this);
    }

    default void fixReport(BugReport report) throws NoRightsException, NotAuthenticatedException {
        checkAuthenticated();
        report.setFixed(this);
    }

    List<BugReport> getAssignedBugReports();
}
