package reader;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.io.IOException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class ReaderImplTest {
    public static final String BASE_TEST_FILE = "src/test/resources/test.txt";

    @ParameterizedTest
    @ValueSource(ints = {5, 10, Integer.MAX_VALUE})
    public void read_numberOfLinesGreaterThanLinesInDocument_ok(int countOfLine) throws IOException {
        ReaderImpl reader = new ReaderImpl(BASE_TEST_FILE);

        List<String> lines = reader.readLines(countOfLine);

        assertEquals(4, lines.size());
        assertEquals("1 The Project Gutenberg EBook of The Adventures of Sherlock Holmes", lines.get(0));
        assertEquals("2 by Sir Arthur Conan Doyle", lines.get(1));
        assertEquals("3 test", lines.get(2));
        assertEquals("4 (#15 in our series by Sir Arthur Conan Doyle)", lines.get(3));
    }

    @Test
    public void read_numberOfLinesLessThanLinesInDocument_returnOnlyRequiredCount() throws IOException {
        int countOfLine = 1;
        ReaderImpl reader = new ReaderImpl(BASE_TEST_FILE);

        List<String> strings = reader.readLines(countOfLine);

        assertEquals(1, strings.size());
        assertEquals("1 The Project Gutenberg EBook of The Adventures of Sherlock Holmes", strings.get(0));
    }

    @Test
    public void read_numberOfLinesTheSameAsLinesInDocument_returnOnlyRequiredCount() throws IOException {
        int countOfLine = 4;
        ReaderImpl reader = new ReaderImpl(BASE_TEST_FILE);

        List<String> strings = reader.readLines(countOfLine);

        assertEquals(countOfLine, strings.size());
        assertEquals("1 The Project Gutenberg EBook of The Adventures of Sherlock Holmes", strings.get(0));
        assertEquals("2 by Sir Arthur Conan Doyle", strings.get(1));
        assertEquals("3 test", strings.get(2));
        assertEquals("4 (#15 in our series by Sir Arthur Conan Doyle)", strings.get(3));
    }

    @Test
    public void read_readTwoLinesSequentiallyByOneChunkSize_returnCorrectStrings() throws IOException {
        String source = "src/test/resources/test.txt";
        int countOfLine = 1;
        ReaderImpl reader = new ReaderImpl(source);

        List<String> firstBatch = reader.readLines(countOfLine);

        assertEquals(countOfLine, firstBatch.size());
        assertEquals("1 The Project Gutenberg EBook of The Adventures of Sherlock Holmes", firstBatch.get(0));

        List<String> secondBatch = reader.readLines(countOfLine);

        assertEquals(countOfLine, firstBatch.size());
        assertEquals("2 by Sir Arthur Conan Doyle", secondBatch.get(0));
    }

    @ParameterizedTest
    @ValueSource(ints = {-1, 0, Integer.MAX_VALUE})
    public void read_incorrectCountOfLines_throwException() throws IOException {
        String source = "src/test/resources/test.txt";
        int countOfLine = -1;
        ReaderImpl reader = new ReaderImpl(source);

        assertThrows(IllegalArgumentException.class, () -> reader.readLines(countOfLine));
    }
}