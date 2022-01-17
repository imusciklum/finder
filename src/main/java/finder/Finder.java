package finder;

import aggreagator.Aggregator;
import aggreagator.AggregatorImpl;
import dto.WordLocation;
import reader.Reader;
import reader.ReaderImpl;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class Finder implements AutoCloseable {
    private final Reader reader;
    private final ExecutorService executorService;
    private final Set<Pattern> patterns;
    private final Integer chunkSize;
    private final Aggregator aggregator;

    private Finder(ExecutorService executorService, Set<String> patterns, int chunkSize, Reader reader, Aggregator aggregator) {
        this.reader = reader;
        this.executorService = executorService;
        this.patterns = patterns.stream().map(Pattern::compile).collect(Collectors.toSet());
        this.chunkSize = chunkSize;
        this.aggregator = aggregator;
    }

    public void find() throws IOException {
        List<Future<Map<String, List<WordLocation>>>> futures = new ArrayList<>();
        List<String> chunk;
        int readLineIndex = 1;

        do {
            chunk = reader.readLines(chunkSize);
            futures.add(executorService.submit(
                    new MatcherTask(new ArrayList<>(chunk), patterns, readLineIndex)));

            readLineIndex += chunk.size();
        } while (chunk.size() <= chunkSize && chunk.size() != 0);

        aggregator.aggregateAndPrint(futures);

    }

    @Override
    public void close() throws Exception {
        reader.close();
    }

    public static Finder of(ExecutorService executorService, Set<String> patterns, int chunkSize, String source) throws FileNotFoundException {
        if (chunkSize < 1) {
            throw new IllegalArgumentException("chunkSize should be greater than 0");
        }
        return new Finder(executorService, patterns, chunkSize, new ReaderImpl(source), new AggregatorImpl());
    }

    public static Finder of(ExecutorService executorService, Set<String> patterns, int chunkSize, String source, Aggregator aggregator) throws FileNotFoundException {
        if (chunkSize < 1) {
            throw new IllegalArgumentException("chunkSize should be greater than 0");
        }
        return new Finder(executorService, patterns, chunkSize, new ReaderImpl(source), aggregator);
    }

    public static Finder of(ExecutorService executorService, Set<String> patterns, int chunkSize, Reader reader, Aggregator aggregator) {
        if (chunkSize < 1) {
            throw new IllegalArgumentException("chunkSize should be greater than 0");
        }
        return new Finder(executorService, patterns, chunkSize, reader, aggregator);
    }

    public static Finder createDefault(ExecutorService executorService, Set<String> patterns, String source) throws FileNotFoundException {
        return new Finder(executorService, patterns, 1000, new ReaderImpl(source), new AggregatorImpl());
    }

}
