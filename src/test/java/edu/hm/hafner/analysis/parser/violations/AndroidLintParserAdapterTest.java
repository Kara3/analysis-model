package edu.hm.hafner.analysis.parser.violations;

import edu.hm.hafner.analysis.AbstractParserTest;
import edu.hm.hafner.analysis.Report;
import edu.hm.hafner.analysis.Severity;
import edu.hm.hafner.analysis.assertions.SoftAssertions;

/**
 * Tests the class {@link AndroidLintParserAdapter}.
 *
 * @author Ullrich Hafner
 */
class AndroidLintParserAdapterTest extends AbstractParserTest {
    AndroidLintParserAdapterTest() {
        super("android-lint.xml");
    }

    @Override
    protected void assertThatIssuesArePresent(final Report report, final SoftAssertions softly) {
        softly.assertThat(report).hasSize(2);
        softly.assertThat(report.get(0))
                .hasFileName("app/src/main/res/layout/fragment_main.xml")
                .hasCategory("Correctness")
                .hasLineStart(10)
                .hasLineEnd(10)
                .hasColumnStart(9)
                .hasColumnEnd(9)
                .hasSeverity(Severity.WARNING_NORMAL);
        softly.assertThat(report.get(0).getMessage()).contains("ScrollView size validation");
        softly.assertThat(report.get(1))
                .hasFileName(
                        ".gradle/caches/modules-2/files-2.1/com.squareup.okio/okio/1.4.0/5b72bf48563ea8410e650de14aa33ff69a3e8c35/okio-1.4.0.jar")
                .hasCategory("Correctness")
                .hasLineStart(0)
                .hasLineEnd(0)
                .hasColumnStart(0)
                .hasColumnEnd(0)
                .hasSeverity(Severity.WARNING_HIGH);
        softly.assertThat(report.get(1).getMessage()).contains("Package not included in Android");
    }

    @Override
    protected AndroidLintParserAdapter createParser() {
        return new AndroidLintParserAdapter();
    }
}
