package alternativesolutions.queueimpl;

import dto.WordLocation;

import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class Main {
    public static void main(String[] args) {
        ConcurrentLinkedQueue<LineInfo> linesQueue = new ConcurrentLinkedQueue<>();
        int nThreads = Runtime.getRuntime().availableProcessors();
        ExecutorService executorService = Executors.newFixedThreadPool(nThreads * 2 - 1);
        Set<String> patterns = Set.of("James", "John", "Robert", "Michael", "William", "David", "Richard", "Charles", "Joseph", "Thomas", "Christopher", "Daniel", "Paul", "Mark", "Donald", "George", "Kenneth", "Steven", "Edward", "Brian", "Ronald", "Anthony", "Kevin", "Jason", "Matthew", "Gary", "Timothy", "Jose", "Larry", "Jeffrey", "Frank", "Scott", "Eric", "Stephen", "Andrew", "Raymond", "Gregory", "Joshua", "Jerry", "Dennis", "Walter", "Patrick", "Peter", "Harold", "Douglas", "Henry", "Carl", "Arthur", "Ryan", "Roger");
        List<Pattern> precompiledPatterns = patterns.stream().map(Pattern::compile).collect(Collectors.toList());
        AtomicBoolean readingIsEnded = new AtomicBoolean(false);
        executorService.submit(new ReaderRunnable(linesQueue, "src/main/resources/source.txt", readingIsEnded));
        ConcurrentHashMap<String, List<WordLocation>> results = new ConcurrentHashMap<>();

        IntStream.of(nThreads)
                .forEach(i -> executorService.submit(new MatcherRunnable(linesQueue, precompiledPatterns, results, readingIsEnded)));

        executorService.shutdown();

        while (!executorService.isTerminated()) {
        }

        results.forEach((key, value) -> System.out.println(key + " --> " + value));

    }
}
