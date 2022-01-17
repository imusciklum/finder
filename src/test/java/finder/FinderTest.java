package finder;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintStream;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class FinderTest {
    private final PrintStream standardOut = System.out;
    private ByteArrayOutputStream outContent = new ByteArrayOutputStream();

    @BeforeEach
    public void setUp() {
        outContent = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outContent));
    }

    @AfterAll
    public void tearDown() {
        System.setOut(standardOut);
    }

    @Test
    public void creatingFinder_incorrectSource_throwException() {
        assertThrows(FileNotFoundException.class,
                () -> Finder.createDefault(Executors.newSingleThreadExecutor(), Set.of("test"), "test"));
    }

    @ParameterizedTest
    @ValueSource(ints = {-1, 0})
    public void creatingFinder_incorrectChunkSize_throwException(int chunkSize) {
        assertThrows(IllegalArgumentException.class,
                () -> Finder.of(Executors.newSingleThreadExecutor(), Set.of("test"), chunkSize, "src/test/resources/test.txt"));
    }

    @ParameterizedTest
    @ValueSource(ints = {1, 3, 5, 1000, Integer.MAX_VALUE})
    public void find_findOnePattern_printCorrectResult(int chunkSize) throws IOException {
        String source = "src/test/resources/test.txt";
        int countOfThread = Runtime.getRuntime().availableProcessors();
        ExecutorService executorService = Executors.newFixedThreadPool(countOfThread);
        Finder finder = Finder.of(executorService, Set.of("Arthur"), chunkSize, source);
        finder.find();

        assertEquals("Arthur --> [[lineOffset=2, charOffset=10], [lineOffset=4, charOffset=29]]" + System.lineSeparator(),
                outContent.toString());

    }

    @ParameterizedTest
    @ValueSource(ints = {1,
            3, 5, 1000, Integer.MAX_VALUE
    })
    public void find_findSeveralPatterns_printCorrectResult(int chunkSize) throws IOException {
        String source = "src/test/resources/test.txt";
        int countOfThread = Runtime.getRuntime().availableProcessors();
        ExecutorService executorService = Executors.newFixedThreadPool(countOfThread);
        Finder finder = Finder.of(executorService, Set.of("Arthur", "test"), chunkSize, source);
        finder.find();

        assertEquals("test --> [[lineOffset=3, charOffset=3]]" + System.lineSeparator() +
                "Arthur --> [[lineOffset=2, charOffset=10], [lineOffset=4, charOffset=29]]" + System.lineSeparator(), outContent.toString());

    }

    @ParameterizedTest
    @ValueSource(ints = {1, 2, 3})
    public void find_chunkSizeLessThanCountOfLinesInDocument_printCorrectResult(int chunkSize) throws IOException {
        String source = "src/test/resources/test.txt";
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        Finder finder = Finder.of(executorService, Set.of("Arthur", "test"), chunkSize, source);
        finder.find();

        assertEquals("test --> [[lineOffset=3, charOffset=3]]" + System.lineSeparator() +
                "Arthur --> [[lineOffset=2, charOffset=10], [lineOffset=4, charOffset=29]]" + System.lineSeparator(), outContent.toString());

    }

    @ParameterizedTest
    @ValueSource(ints = {5, 10, 1000, Integer.MAX_VALUE})
    public void find_chunkSizeGreaterThanCountOfLinesInDocument_printCorrectResult(int chunkSize) throws IOException {
        String source = "src/test/resources/test.txt";
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        Finder finder = Finder.of(executorService, Set.of("Arthur", "test"), chunkSize, source);
        finder.find();

        assertEquals("test --> [[lineOffset=3, charOffset=3]]" + System.lineSeparator() +
                "Arthur --> [[lineOffset=2, charOffset=10], [lineOffset=4, charOffset=29]]" + System.lineSeparator(), outContent.toString());

    }

    @Test
    public void find_chunkSizeTheSameAsCountOfLinesInDocument_printCorrectResult() throws IOException {
        int chunkSize = 4;
        String source = "src/test/resources/test.txt";
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        String arthur = "Arthur";
        String test = "test";
        Finder finder = Finder.of(executorService, Set.of(arthur, test), chunkSize, source);
        finder.find();

        assertEquals("test --> [[lineOffset=3, charOffset=3]]" + System.lineSeparator() +
                "Arthur --> [[lineOffset=2, charOffset=10], [lineOffset=4, charOffset=29]]" + System.lineSeparator(), outContent.toString());
    }

}