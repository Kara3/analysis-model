package edu.hm.hafner.analysis.parser.dry.dupfinder;

import java.io.Serializable;
import java.util.Objects;

import org.junit.jupiter.api.Test;

import edu.hm.hafner.analysis.AbstractParserTest;
import edu.hm.hafner.analysis.DuplicationGroup;
import edu.hm.hafner.analysis.Issue;
import edu.hm.hafner.analysis.Report;
import edu.hm.hafner.analysis.Severity;
import edu.hm.hafner.analysis.assertions.SoftAssertions;

import static edu.hm.hafner.analysis.assertions.Assertions.*;

/**
 * Tests the extraction of Resharper DupFinder analysis results.
 *
 * @author Rafal Jasica
 */
class DupFinderParserTest extends AbstractParserTest {
    /** First line in publisher. */
    private static final int PUBLISHER_LINE = 12;
    /** First line in reporter. */
    private static final int REPORTER_LINE = 26;
    /** File name of reporter. */
    private static final String REPORTER = "test/Reporter.cs";
    /** File name of publisher. */
    private static final String PUBLISHER = "test/Publisher.cs";
    /** Source code. */
    private static final String CODE_FRAGMENT = "if (items == null) throw new ArgumentNullException(\"items\");";

    DupFinderParserTest() {
        super("with-sourcecode.xml");
    }

    @Override
    protected DupFinderParser createParser() {
        return new DupFinderParser(50, 25);
    }

    @Override
    protected void assertThatIssuesArePresent(final Report report,
            final SoftAssertions softly) {
        softly.assertThat(report).hasSize(2);

        Issue publisher = report.get(0);
        Issue reporter = report.get(1);

        assertThatReporterAndPublisherDuplicationsAreCorrectlyLinked(publisher, reporter);

        Serializable additionalProperties = publisher.getAdditionalProperties();
        softly.assertThat(additionalProperties).isEqualTo(reporter.getAdditionalProperties());
        softly.assertThat(additionalProperties).isInstanceOf(DuplicationGroup.class);
        softly.assertThat(((DuplicationGroup) Objects.requireNonNull(additionalProperties)).getCodeFragment())
                .isEqualTo(CODE_FRAGMENT);
    }

    private void assertThatReporterAndPublisherDuplicationsAreCorrectlyLinked(
            final Issue publisher, final Issue reporter) {
        assertThat(publisher)
                .hasLineStart(PUBLISHER_LINE).hasLineEnd(PUBLISHER_LINE + 11)
                .hasFileName(PUBLISHER)
                .hasSeverity(Severity.WARNING_LOW);
        assertThat(reporter)
                .hasLineStart(REPORTER_LINE).hasLineEnd(REPORTER_LINE + 11)
                .hasFileName(REPORTER)
                .hasSeverity(Severity.WARNING_LOW);

        assertThat(Objects.requireNonNull(publisher.getAdditionalProperties()))
                .isEqualTo(Objects.requireNonNull(reporter.getAdditionalProperties()));
    }

    /**
     * Verifies the parser on a report that contains one duplication in two files. The report does not contain a code
     * fragment.
     */
    @Test
    void scanFileWithoutSourceCode() {
        Report report = parse("without-sourcecode.xml");

        assertThat(report).hasSize(2);

        Issue publisher = report.get(0);
        Issue reporter = report.get(1);

        assertThatReporterAndPublisherDuplicationsAreCorrectlyLinked(publisher, reporter);

        assertThat(reporter.getDescription()).isEmpty();
        assertThat(publisher.getDescription()).isEmpty();
    }

    @Test
    void shouldIgnoreOtherFile() {
        Report report = parse("otherfile.xml");

        assertThat(report).hasSize(0);
    }

    @Test
    void shouldAssignPriority() {
        Report report;

        report = parse(12, 5);
        assertThat(report).hasSize(2);
        assertThat(report.get(0)).hasSeverity(Severity.WARNING_HIGH);

        report = parse(13, 5);
        assertThat(report).hasSize(2);
        assertThat(report.get(0)).hasSeverity(Severity.WARNING_NORMAL);

        report = parse(100, 12);
        assertThat(report).hasSize(2);
        assertThat(report.get(0)).hasSeverity(Severity.WARNING_NORMAL);

        report = parse(100, 13);
        assertThat(report).hasSize(2);
        assertThat(report.get(0)).hasSeverity(Severity.WARNING_LOW);
    }

    private Report parse(final int highThreshold, final int normalThreshold) {
        DupFinderParser parser = new DupFinderParser(highThreshold, normalThreshold);
        return parser.parse(createReaderFactory("without-sourcecode.xml"));
    }
}

