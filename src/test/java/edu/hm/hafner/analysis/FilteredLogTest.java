package edu.hm.hafner.analysis;

import org.junit.jupiter.api.Test;

import static edu.hm.hafner.analysis.assertions.Assertions.*;

/**
 * Tests the class {@link FilteredLog}.
 *
 * @author Ullrich Hafner
 */
class FilteredLogTest {
    private static final String TITLE = "Title: ";

    @Test
    void shouldLogNothing() {
        Report report = new Report();
        FilteredLog filteredLog = new FilteredLog(report, TITLE, 5);

        assertThat(report.getErrorMessages()).isEmpty();
        filteredLog.logSummary();
        assertThat(report.getErrorMessages()).isEmpty();
    }

    @Test
    void shouldLogAllErrors() {
        Report report = new Report();
        FilteredLog filteredLog = new FilteredLog(report, TITLE, 5);

        filteredLog.logError("1");
        filteredLog.logError("2");
        filteredLog.logError("3");
        filteredLog.logError("4");
        filteredLog.logError("5");

        assertThat(report.getErrorMessages()).containsExactly(TITLE, "1", "2", "3", "4", "5");
        filteredLog.logSummary();
        assertThat(report.getErrorMessages()).containsExactly(TITLE, "1", "2", "3", "4", "5");
        assertThat(filteredLog.size()).isEqualTo(5);
    }

    @Test
    void shouldSkipAdditionalErrors() {
        Report report = new Report();
        FilteredLog filteredLog = new FilteredLog(report, TITLE, 5);

        filteredLog.logError("1");
        filteredLog.logError("2");
        filteredLog.logError("3");
        filteredLog.logError("4");
        filteredLog.logError("5");
        filteredLog.logError("6");
        filteredLog.logError("7");

        assertThat(report.getErrorMessages()).containsExactly(TITLE, "1", "2", "3", "4", "5");
        filteredLog.logSummary();
        assertThat(report.getErrorMessages()).containsExactly(TITLE, "1", "2", "3", "4", "5",
                "  ... skipped logging of 2 additional errors ...");
        assertThat(filteredLog.size()).isEqualTo(7);
    }

    @Test
    void shouldLogExceptions() {
        Report report = new Report();
        FilteredLog filteredLog = new FilteredLog(report, TITLE, 1);

        filteredLog.logException(new IllegalArgumentException("Cause"), "Message");
        filteredLog.logException(new IllegalArgumentException(""), "Message");

        assertThat(report.getErrorMessages()).contains(TITLE,
                "Message", "java.lang.IllegalArgumentException: Cause",
                "\tat edu.hm.hafner.analysis.FilteredLogTest.shouldLogExceptions(FilteredLogTest.java:67)");
    }
}
