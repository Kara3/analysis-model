package edu.hm.hafner.analysis.parser;

import java.util.Iterator;

import edu.hm.hafner.analysis.AbstractParserTest;
import edu.hm.hafner.analysis.Issue;
import edu.hm.hafner.analysis.IssueBuilder;
import edu.hm.hafner.analysis.Report;
import edu.hm.hafner.analysis.Severity;
import edu.hm.hafner.analysis.assertions.SoftAssertions;

import static edu.hm.hafner.analysis.assertions.Assertions.*;

/**
 * Tests the class {@link AcuCobolParser}.
 */
class AcuCobolParserTest extends AbstractParserTest {
    private static final String CATEGORY = new IssueBuilder().build().getCategory();

    AcuCobolParserTest() {
        super("acu.txt");
    }

    @Override
    protected void assertThatIssuesArePresent(final Report report, final SoftAssertions softly) {
        assertThat(report).hasSize(4);

        Iterator<Issue> iterator = report.iterator();
        softly.assertThat(iterator.next())
                .hasSeverity(Severity.WARNING_NORMAL)
                .hasCategory(CATEGORY)
                .hasLineStart(39)
                .hasLineEnd(39)
                .hasMessage("Imperative statement required")
                .hasFileName("COPY/zzz.CPY");
        softly.assertThat(iterator.next())
                .hasSeverity(Severity.WARNING_NORMAL)
                .hasCategory(CATEGORY)
                .hasLineStart(111)
                .hasLineEnd(111)
                .hasMessage("Don't run with knives")
                .hasFileName("C:/Documents and Settings/xxxx/COB/bbb.COB");
        softly.assertThat(iterator.next())
                .hasSeverity(Severity.WARNING_NORMAL)
                .hasCategory(CATEGORY)
                .hasLineStart(115)
                .hasLineEnd(115)
                .hasMessage("Don't run with knives")
                .hasFileName("C:/Documents and Settings/xxxx/COB/bbb.COB");
        softly.assertThat(iterator.next())
                .hasSeverity(Severity.WARNING_NORMAL)
                .hasCategory(CATEGORY)
                .hasLineStart(123)
                .hasLineEnd(123)
                .hasMessage("I'm a green banana")
                .hasFileName("C:/Documents and Settings/xxxx/COB/ccc.COB");
    }

    @Override
    protected AcuCobolParser createParser() {
        return new AcuCobolParser();
    }
}

