package edu.hm.hafner.analysis;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UncheckedIOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import javax.xml.stream.XMLStreamReader;

import org.apache.commons.io.input.BOMInputStream;

import com.google.errorprone.annotations.MustBeClosed;

import edu.umd.cs.findbugs.annotations.Nullable;

/**
 * Provides a {@link ReaderFactory} that returns readers for a given file.
 *
 * @author Ullrich Hafner
 */
public class FileReaderFactory extends ReaderFactory {
    private final Path file;
    private final String fileName;
    @Nullable
    private Charset charset;
    private final boolean isCharsetUndetected;

    /**
     * Creates a new factory to read the specified file with a given charset.
     *
     * @param file
     *         the file to open
     * @param charset
     *         the charset to use when reading the file (or {@code null} if the charset should be detected)
     */
    public FileReaderFactory(final Path file, final @Nullable Charset charset) {
        super(StandardCharsets.UTF_8);

        this.file = file;
        this.charset = charset;
        isCharsetUndetected = charset == null;
        fileName = file.toAbsolutePath().toString().replace('\\', '/');
    }

    /**
     * Creates a new factory to read the specified file. The charset will be detected from xml header.
     *
     * @param file
     *         the file to open
     */
    public FileReaderFactory(final Path file) {
        this(file, null);
    }

    @Override @MustBeClosed
    public Reader create() {
        try {
            if (isCharsetUndetected) {
                charset = detectCharset(Files.newInputStream(file));
            }
            InputStream inputStream = Files.newInputStream(file);

            return new InputStreamReader(new BOMInputStream(inputStream), getCharset());
        }
        catch (FileNotFoundException | InvalidPathException exception) {
            throw new ParsingException(exception, "Can't find file '%s'", fileName);
        }
        catch (IOException | UncheckedIOException exception) {
            throw new ParsingException(exception, "Can't parse file '%s'", fileName);
        }
    }

    @Nullable
    private Charset detectCharset(final InputStream inputStream) throws IOException {
        try (Reader reader = new InputStreamReader(inputStream, StandardCharsets.US_ASCII)) {
            XMLStreamReader xmlStreamReader = new SecureXmlParserFactory().createXmlStreamReader(reader);
            String encodingTitle = xmlStreamReader.getCharacterEncodingScheme();
            if (encodingTitle != null) {
                return Charset.forName(encodingTitle);
            }
        }
        catch (IllegalArgumentException ignore) {
            // Ignore it the charset couldn't be detected
        }

        return null;
    }

    /**
     * Returns the absolute path of the resource. The file name uses UNIX path separators.
     *
     * @return the file name
     */
    @Override
    public String getFileName() {
        return fileName;
    }

    @Override
    public Charset getCharset() {
        if (charset == null) {
            return super.getCharset();
        }
        return charset;
    }
}
