/**
 * Created by Azat on 28.03.2017.
 */
package com.spbpu.user;

import com.spbpu.exceptions.NoRightsException;
import com.spbpu.exceptions.NotAuthenticatedException;
import com.spbpu.project.BugReport;

public interface ReportManager extends UserInterface {

    default void commentReport(BugReport report, String comment) throws NoRightsException, NotAuthenticatedException {
        checkAuthenticated();
        report.addComment(this, comment);
    }

    default void reopenReport(BugReport report, String comment) throws NotAuthenticatedException {
        checkAuthenticated();
        report.setReopened(this, comment);
    }

    default void closeReport(BugReport report) throws NotAuthenticatedException {
        checkAuthenticated();
        report.setClosed(this);
    }
}
