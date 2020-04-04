package edu.hm.hafner.analysis;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.charset.Charset;
import java.nio.charset.MalformedInputException;
import java.nio.file.InvalidPathException;

import org.apache.commons.lang3.builder.HashCodeBuilder;

import edu.hm.hafner.util.VisibleForTesting;

/**
 * Creates fingerprints for a set of issues.
 *
 * @author Ullrich Hafner
 */
public class FingerprintGenerator {
    /**
     * Creates fingerprints for the specified set of issues.
     *
     * @param algorithm
     *         fingerprinting algorithm
     * @param report
     *         the issues to analyze
     * @param charset
     *         the character set to use when reading the source files
     */
    public void run(final FullTextFingerprint algorithm, final Report report, final Charset charset) {
        FilteredLog log = new FilteredLog(report, "Can't create fingerprints for some files:");
        int sum = 0;
        for (Issue issue : report) {
            if (!issue.hasFingerprint()) {
                sum += computeFingerprint(issue, algorithm, charset, log);
            }
        }
        report.logInfo("-> created fingerprints for %d issues (skipped %d issues)", sum, report.size() - sum);
        log.logSummary();
    }

    private int computeFingerprint(final Issue issue, final FullTextFingerprint algorithm, final Charset charset,
            final FilteredLog log) {
        String absolutePath = issue.getAbsolutePath();
        try {
            if (issue.hasFileName()) {
                String digest = algorithm.compute(absolutePath, issue.getLineStart(), charset);
                issue.setFingerprint(digest);
                return 1;
            }
        }
        catch (FileNotFoundException exception) {
            log.logError("- '%s' file not found", absolutePath);
        }
        catch (IOException | InvalidPathException | UncheckedIOException exception) {
            if (exception.getCause() instanceof MalformedInputException) {
                log.logError("- '%s', provided encoding '%s' seems to be wrong", absolutePath, charset);
            }
            else {
                log.logError("- '%s', IO exception has been thrown: %s", absolutePath, exception);
            }
        }
        issue.setFingerprint(createDefaultFingerprint(issue));
        return 0;
    }

    @VisibleForTesting
    static String createDefaultFingerprint(final Issue issue) {
        HashCodeBuilder builder = new HashCodeBuilder();
        return String.format("FALLBACK-%x",
                builder.append(issue.getBaseName())
                        .append(issue.getType())
                        .append(issue.getCategory())
                        .append(issue.getSeverity())
                        .append(issue.getOrigin())
                        .append(issue.getLineStart()).build());
    }
}
