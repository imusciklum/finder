package aggreagator;

import dto.WordLocation;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Future;

import static org.junit.jupiter.api.Assertions.assertEquals;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class AggregatorImplTest {
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
    public void aggregateAndPrint_severalTaskWithTheSamePattern_printCorrect() {
        List<WordLocation> testWordLocations = List.of(new WordLocation(10, 11));

        String pattern = "test";
        Future<Map<String, List<WordLocation>>> future1 = createFutureWithMatcherTask(pattern, testWordLocations);
        List<WordLocation> testWordLocations2 = List.of(new WordLocation(20, 21));
        Future<Map<String, List<WordLocation>>> future2 = createFutureWithMatcherTask(pattern, testWordLocations2);

        AggregatorImpl aggregator = new AggregatorImpl();

        aggregator.aggregateAndPrint(List.of(future1, future2));

        assertEquals("test --> [[lineOffset=10, charOffset=11], [lineOffset=20, charOffset=21]]" + System.lineSeparator(), outContent.toString());

    }

    @Test
    public void aggregate_severalTaskWithTheDifferentPatterns_printCorrect() {
        List<WordLocation> testWordLocations = List.of(new WordLocation(10, 11));

        String pattern1 = "test1";
        String pattern2 = "test2";
        Future<Map<String, List<WordLocation>>> future1 = createFutureWithMatcherTask(pattern1, testWordLocations);
        List<WordLocation> testWordLocations2 = List.of(new WordLocation(20, 21));
        Future<Map<String, List<WordLocation>>> future2 = createFutureWithMatcherTask(pattern2, testWordLocations2);

        AggregatorImpl aggregator = new AggregatorImpl();

        aggregator.aggregateAndPrint(List.of(future1, future2));

        assertEquals("test2 --> [[lineOffset=20, charOffset=21]]" + System.lineSeparator() +
                        "test1 --> [[lineOffset=10, charOffset=11]]" + System.lineSeparator(),
                outContent.toString());

    }

    private Future<Map<String, List<WordLocation>>> createFutureWithMatcherTask(String pattern, List<WordLocation> wordLocations) {
        Map<String, List<WordLocation>> res = new HashMap<>();
        res.put(pattern, wordLocations);
        return CompletableFuture.completedFuture(res);
    }

}