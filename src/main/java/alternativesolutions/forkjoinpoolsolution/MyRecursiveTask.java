package alternativesolutions.forkjoinpoolsolution;

import dto.WordLocation;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ForkJoinTask;
import java.util.concurrent.RecursiveTask;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class MyRecursiveTask extends RecursiveTask<Map<String, List<WordLocation>>> {
    private final List<String> lines;
    private final int threshold;
    private final List<Pattern> patterns;
    private int indexOfFirstLine;

    public MyRecursiveTask(List<String> lines, int threshold, List<Pattern> patterns, int indexOfFirstLine) {
        this.lines = lines;
        this.threshold = threshold;
        this.patterns = patterns;
        this.indexOfFirstLine = indexOfFirstLine;
    }

    @Override
    protected Map<String, List<WordLocation>> compute() {
        if (lines.size() > threshold) {
            List<RecursiveTask<Map<String, List<WordLocation>>>> subTasks = createSubTasks();
            for (RecursiveTask<Map<String, List<WordLocation>>> subTask : subTasks) {
                subTask.fork();
            }

            return subTasks.stream()
                    .map(ForkJoinTask::join)
                    .flatMap(a -> a.entrySet().stream())
                    .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue,
                            (l1, l2) -> Stream.concat(l1.stream(), l2.stream()).collect(Collectors.toList())));
        } else {
            return calculateResult(lines);
        }
    }

    private Map<String, List<WordLocation>> calculateResult(List<String> newLines) {
        Map<String, List<WordLocation>> results = patterns.stream()
                .collect(Collectors.toMap(Pattern::pattern, p -> new ArrayList<>()));

        for (String line : newLines) {
            for (Pattern pattern : patterns) {
                Matcher matcher = pattern.matcher(line);
                while (matcher.find()) {
                    results.get(pattern.pattern()).add(
                            new WordLocation(indexOfFirstLine, matcher.start() + 1));
                }
            }
            indexOfFirstLine++;
        }

        return results;
    }

    private List<RecursiveTask<Map<String, List<WordLocation>>>> createSubTasks() {
        int middleIndex = lines.size() / 2;
        return List.of(
                new MyRecursiveTask(lines.subList(0, middleIndex), threshold, patterns, indexOfFirstLine),
                new MyRecursiveTask(lines.subList(middleIndex, lines.size()), threshold, patterns, indexOfFirstLine + middleIndex));
    }

}