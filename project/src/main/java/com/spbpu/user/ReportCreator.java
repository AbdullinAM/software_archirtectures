/**
 * Created by Azat on 26.03.2017.
 */

package com.spbpu.user;

import com.spbpu.exceptions.NoRightsException;
import com.spbpu.exceptions.NotAuthenticatedException;
import com.spbpu.project.BugReport;

public interface ReportCreator extends UserInterface {

    BugReport createReport(String description) throws NotAuthenticatedException;

    default void commentReport(BugReport report, String comment) throws NoRightsException, NotAuthenticatedException {
        checkAuthenticated();
        report.addComment(this, comment);
    }

}
