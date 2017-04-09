/**
 * Created by Azat on 26.03.2017.
 */

package com.spbpu.user;

import com.spbpu.exceptions.AlreadyAcceptedException;
import com.spbpu.exceptions.NoRightsException;
import com.spbpu.exceptions.NotAuthenticatedException;
import com.spbpu.project.BugReport;

public interface ReportDeveloper extends UserInterface {

    default void notifyNew(BugReport report) {}

    default void acceptReport(BugReport report) throws AlreadyAcceptedException, NotAuthenticatedException {
        checkAuthenticated();
        report.setAccepted(this);
    }

    default void fixReport(BugReport report) throws NoRightsException, NotAuthenticatedException {
        checkAuthenticated();
        report.setFixed(this);
    }
}
