package alternativesolutions.queueimpl;

import dto.WordLocation;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class MatcherRunnable implements Runnable {
    private final ConcurrentLinkedQueue<LineInfo> blockingQueue;
    private final List<Pattern> patterns;
    private final ConcurrentHashMap<String, List<WordLocation>> result;
    private final AtomicBoolean readingIsEnded;

    public MatcherRunnable(ConcurrentLinkedQueue<LineInfo> blockingQueue, List<Pattern> patterns, ConcurrentHashMap<String, List<WordLocation>> result, AtomicBoolean readingIsEnded) {
        this.blockingQueue = blockingQueue;
        this.patterns = patterns;
        this.result = result;
        this.readingIsEnded = readingIsEnded;
    }

    @Override
    public void run() {
        LineInfo lineInfo;
        while ((lineInfo = blockingQueue.poll()) != null || !readingIsEnded.get()) {

            if (lineInfo == null) {
                continue;
            }
            Map<String, List<WordLocation>> match = match(patterns, lineInfo.getLine(), lineInfo.getLineIndex());

            match.forEach((key, value) -> {
                List<WordLocation> existedValue = result.get(key);
                if (existedValue == null) {
                    result.put(key, value);
                } else {
                    existedValue.addAll(value);
                }
            });
        }
    }

    public Map<String, List<WordLocation>> match(List<Pattern> patterns, String text, int indexOfLine) {
        Map<String, List<WordLocation>> results = patterns.stream()
                .collect(Collectors.toMap(Pattern::pattern, p -> new ArrayList<>()));

        for (Pattern pattern : patterns) {
            java.util.regex.Matcher matcher = pattern.matcher(text);
            while (matcher.find()) {
                results.get(pattern.pattern()).add(
                        new WordLocation(indexOfLine, matcher.start() + 1));
            }
        }
        return results;
    }
}
