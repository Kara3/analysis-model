package edu.hm.hafner.analysis.parser.violations;

import java.util.List;

import edu.hm.hafner.analysis.Issue;
import edu.hm.hafner.analysis.IssueBuilder;
import edu.hm.hafner.analysis.IssueParser;
import edu.hm.hafner.analysis.ParsingCanceledException;
import edu.hm.hafner.analysis.ParsingException;
import edu.hm.hafner.analysis.ReaderFactory;
import edu.hm.hafner.analysis.Report;
import edu.hm.hafner.analysis.Severity;

import se.bjurr.violations.lib.model.SEVERITY;
import se.bjurr.violations.lib.model.Violation;
import se.bjurr.violations.lib.parsers.ViolationsParser;

/**
 * Adapter for {@link ViolationsParser} instances. Converts the results of a {@link ViolationsParser} into a static
 * analysis {@link Report}.
 *
 * @author Ullrich Hafner
 */
public abstract class AbstractViolationAdapter extends IssueParser {
    private static final long serialVersionUID = 7203311857999721045L;

    @SuppressWarnings({"illegalcatch", "OverlyBroadCatchBlock"})
    @Override
    public Report parse(final ReaderFactory readerFactory)
            throws ParsingCanceledException, ParsingException {
        try {
            ViolationsParser parser = createParser();
            List<Violation> violations = parser.parseReportOutput(readerFactory.readString());
            return convertToReport(violations);
        }
        catch (Exception exception) {
            throw new ParsingException(exception);
        }
    }

    /**
     * Creates a new parser instance.
     *
     * @return the {@link ViolationsParser} instance
     */
    abstract ViolationsParser createParser();

    /**
     * Converts the list of violations to a corresponding report of issues.
     *
     * @param violations
     *         the violations
     *
     * @return the report
     */
    Report convertToReport(final List<Violation> violations) {
        Report report = new Report();
        for (Violation violation : violations) {
            if (isValid(violation)) {
                report.add(convertToIssue(violation));
            }
        }
        return report;
    }

    /**
     * Returns whether this violation is valid and should be converted to an {@link Issue}. Return {@code false} if the
     * specified violation is a false positive or should not be counted.
     *
     * @param violation
     *         the violation to check
     *
     * @return {@code true} if the violation is valid, {@code false} otherwise
     */
    boolean isValid(final Violation violation) {
        return true;
    }

    /**
     * Converts the specified violation to a corresponding {@link Issue} instance.
     *
     * @param violation
     *         the violation
     *
     * @return corresponding {@link Issue}
     */
    Issue convertToIssue(final Violation violation) {
        IssueBuilder builder = createIssueBuilder(violation);
        extractAdditionalProperties(builder, violation);

        return builder.build();
    }

    /**
     * Converts the specified violation to a corresponding {@link IssueBuilder} instance.
     *
     * @param violation
     *         the violation
     *
     * @return corresponding {@link IssueBuilder} instance
     */
    IssueBuilder createIssueBuilder(final Violation violation) {
        IssueBuilder builder = new IssueBuilder();
        builder.setSeverity(convertSeverity(violation.getSeverity(), violation))
                .setFileName(violation.getFile())
                .setMessage(violation.getMessage())
                .setLineStart(violation.getStartLine())
                .setLineEnd(violation.getEndLine())
                .setColumnStart(violation.getColumn())
                .setType(violation.getRule())
                .setCategory(violation.getCategory());
        return builder;
    }

    /**
     * Sub-classes may add additional {@link IssueBuilder} properties based on the content of the specified {@link
     * Violation}. This default implementation is empty.
     *
     * @param builder
     *         the issue builder to change
     * @param violation
     *         the violation instance
     */
    void extractAdditionalProperties(final IssueBuilder builder, final Violation violation) {
        // default implementation is empty
    }

    /**
     * Computes the {@link Severity} from the specified {@link SEVERITY}. Sub-classes may override and use any of the
     * properties of the provided violation.
     *
     * @param severity
     *         the severity
     * @param violation
     *         the violation instance
     *
     * @return the {@link Severity}
     */
    @SuppressWarnings("unused")
    Severity convertSeverity(final SEVERITY severity, final Violation violation) {
        if (severity == SEVERITY.ERROR) {
            return Severity.WARNING_HIGH;
        }
        if (severity == SEVERITY.WARN) {
            return Severity.WARNING_NORMAL;
        }
        return Severity.WARNING_LOW;
    }
}
