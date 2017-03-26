/**
 * Created by Azat on 26.03.2017.
 */

package user;

import project.BugReport;

public interface ReportManager {
    
    BugReport createReport(String description);

    default void commentReport(BugReport report, String comment) {
        report.addComment(this, comment);
    }

    default void reopenReport(BugReport report, String comment) {
        report.setOpened(this, comment);
    }

    default void closeReport(BugReport report) {
        report.setClosed(this);
    }
}
