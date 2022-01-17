package finder;

import dto.WordLocation;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class MatcherTask implements Callable<Map<String, List<WordLocation>>> {
    private final List<String> lines;
    private final Set<Pattern> patterns;
    private int indexOfFirstLine;

    public MatcherTask(List<String> lines, Set<Pattern> patterns, int indexOfFirstLine) {
        this.lines = lines;
        this.patterns = patterns;
        this.indexOfFirstLine = indexOfFirstLine;
    }

    @Override
    public Map<String, List<WordLocation>> call() {
        Map<String, List<WordLocation>> results = patterns.stream()
                .collect(Collectors.toMap(Pattern::pattern, p -> new ArrayList<>()));

        for (String line : lines) {
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

}
