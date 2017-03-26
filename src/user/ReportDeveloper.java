/**
 * Created by Azat on 26.03.2017.
 */

package user;

import project.BugReport;

public interface ReportDeveloper {
    default void acceptReport(BugReport report) {
        report.setAccepted(this);
    }

    default void commentReport(BugReport report, String comment) {
        report.addComment(this, comment);
    }

    default void fixReport(BugReport report) {
        report.setFixed(this);
    }
}
