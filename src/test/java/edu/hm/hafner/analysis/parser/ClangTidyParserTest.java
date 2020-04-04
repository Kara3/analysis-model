package edu.hm.hafner.analysis.parser;

import edu.hm.hafner.analysis.AbstractParserTest;
import edu.hm.hafner.analysis.Report;
import edu.hm.hafner.analysis.Severity;
import edu.hm.hafner.analysis.assertions.SoftAssertions;

import static edu.hm.hafner.analysis.assertions.Assertions.*;

/**
 * Tests the class {@link ClangTidyParser}.
 *
 * @author Ryan Schaefer
 */
class ClangTidyParserTest extends AbstractParserTest {
    private static final String WARNING_TYPE = "Warning";

    ClangTidyParserTest() {
        super("ClangTidy.txt");
    }

    @Override
    protected ClangTidyParser createParser() {
        return new ClangTidyParser();
    }

    @Override
    protected void assertThatIssuesArePresent(final Report annotation, final SoftAssertions softly) {
        assertThat(annotation).hasSize(7);

        softly.assertThat(annotation.get(0))
                .hasLineStart(1)
                .hasColumnStart(8)
                .hasFileName("src/../src/main.cpp")
                .hasMessage("implicit conversion changes signedness: 'int' to 'uint32_t' (aka 'unsigned int')")
                .hasType(WARNING_TYPE)
                .hasCategory("clang-diagnostic-sign-conversion")
                .hasSeverity(Severity.WARNING_NORMAL);

        softly.assertThat(annotation.get(1))
                .hasLineStart(10)
                .hasColumnStart(20)
                .hasFileName("/src/main.cpp")
                .hasMessage("implicit conversion changes signedness: 'int' to 'uint32_t' (aka 'unsigned int')")
                .hasType(WARNING_TYPE)
                .hasCategory("clang-diagnostic-sign-conversion")
                .hasSeverity(Severity.WARNING_NORMAL);

        softly.assertThat(annotation.get(2))
                .hasLineStart(83)
                .hasColumnStart(20)
                .hasFileName("/path/to/project/src/test2.cpp")
                .hasMessage("implicit conversion changes signedness: 'int' to 'uint32_t' (aka 'unsigned int')")
                .hasType(WARNING_TYPE)
                .hasCategory("clang-diagnostic-sign-conversion")
                .hasSeverity(Severity.WARNING_NORMAL);

        softly.assertThat(annotation.get(3))
                .hasLineStart(25)
                .hasColumnStart(15)
                .hasFileName("/path/to/project/src/test2.cpp")
                .hasMessage("suggest braces around initialization of subobject")
                .hasType(WARNING_TYPE)
                .hasCategory("clang-diagnostic-missing-braces")
                .hasSeverity(Severity.WARNING_NORMAL);

        softly.assertThat(annotation.get(4))
                .hasLineStart(29)
                .hasColumnStart(15)
                .hasFileName("/path/to/project/src/test2.cpp")
                .hasMessage("suggest braces around initialization of subobject")
                .hasType(WARNING_TYPE)
                .hasCategory("clang-diagnostic-missing-braces")
                .hasSeverity(Severity.WARNING_NORMAL);

        softly.assertThat(annotation.get(5))
                .hasColumnStart(10)
                .hasFileName("/path/to/project/src/error_test.cpp")
                .hasMessage("'dbus/dbus.h' file not found")
                .hasType("Error")
                .hasCategory("clang-diagnostic-error")
                .hasSeverity(Severity.WARNING_HIGH);

        softly.assertThat(annotation.get(6))
                .hasLineStart(50)
                .hasColumnStart(57)
                .hasFileName("/var/lib/jenkins/workspace/job/user/project.cpp")
                .hasMessage("implicit conversion turns string literal into bool: 'const char [28]' to 'bool'")
                .hasType(WARNING_TYPE)
                .hasCategory("clang-diagnostic-string-conversion")
                .hasSeverity(Severity.WARNING_NORMAL);
    }

}
