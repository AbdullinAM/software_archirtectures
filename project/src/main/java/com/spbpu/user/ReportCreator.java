/**
 * Created by Azat on 26.03.2017.
 */

package com.spbpu.user;

import com.spbpu.exceptions.NoRightsException;
import com.spbpu.exceptions.NotAuthenticatedException;
import com.spbpu.project.BugReport;
import com.spbpu.project.Project;

public interface ReportCreator extends UserInterface {

    void managing(BugReport report);

    BugReport createReport(Project project, String description) throws NotAuthenticatedException, NoRightsException;

    default void commentReport(BugReport report, String comment) throws NoRightsException, NotAuthenticatedException {
        checkAuthenticated();
        report.addComment(this, comment);
    }

}
