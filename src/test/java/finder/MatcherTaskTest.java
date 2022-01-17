package finder;

import dto.WordLocation;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

import static org.junit.jupiter.api.Assertions.assertEquals;

class MatcherTaskTest {

    @Test
    public void matcher_severalLinesContainsPattern_returnCorrectWordLocation() {
        List<String> lines = List.of("First line", "Second line", "Third line");
        Pattern pattern = Pattern.compile("line");
        int indexOfFirstLine = 1;
        MatcherTask task = new MatcherTask(lines, Set.of(pattern), indexOfFirstLine);
        Map<String, List<WordLocation>> result = task.call();

        assertEquals(1, result.size());
        List<WordLocation> wordLocations = result.get(pattern.pattern());
        assertEquals(wordLocations.size(), 3);

        assertLineResult(wordLocations.get(0), indexOfFirstLine, 7);
        assertLineResult(wordLocations.get(1), indexOfFirstLine + 1, 8);
        assertLineResult(wordLocations.get(2), indexOfFirstLine + 2, 7);
    }

    @Test
    public void matcher_lineContainsPatternSeveralTimes_returnCorrectWordLocations() {
        List<String> lines = List.of("First line line");
        Pattern pattern = Pattern.compile("line");
        int indexOfFirstLine = 1;
        MatcherTask task = new MatcherTask(lines, Set.of(pattern), indexOfFirstLine);
        Map<String, List<WordLocation>> result = task.call();

        assertEquals(1, result.size());
        List<WordLocation> wordLocations = result.get(pattern.pattern());
        assertEquals(wordLocations.size(), 2);

        assertLineResult(wordLocations.get(0), indexOfFirstLine, 7);
        assertLineResult(wordLocations.get(1), indexOfFirstLine, 12);
    }

    @Test
    public void matcher_lineContainsSeveralPatternsSeveralTimes_returnCorrectWordLocations() {
        List<String> lines = List.of("First line test", "Second test line", "Third");
        Pattern linePattern = Pattern.compile("line");
        Pattern testPattern = Pattern.compile("test");
        int indexOfFirstLine = 1;
        MatcherTask task = new MatcherTask(lines, Set.of(linePattern, testPattern), indexOfFirstLine);
        Map<String, List<WordLocation>> result = task.call();

        assertEquals(2, result.size());
        List<WordLocation> lineWordLocations = result.get(linePattern.pattern());
        assertEquals(lineWordLocations.size(), 2);

        assertLineResult(lineWordLocations.get(0), indexOfFirstLine, 7);
        assertLineResult(lineWordLocations.get(1), indexOfFirstLine+1, 13);

        List<WordLocation> testWordLocations = result.get(testPattern.pattern());
        assertEquals(testWordLocations.size(), 2);

        assertLineResult(testWordLocations.get(0), indexOfFirstLine, 12);
        assertLineResult(testWordLocations.get(1), indexOfFirstLine + 1, 8);
    }

    @Test
    public void matcher_findPatternAsPartOfWord_matchesAreFound() {
        List<String> lines = List.of("First lines", "Secondline", "Third aline");
        Pattern pattern = Pattern.compile("line");
        int indexOfFirstLine = 1;
        MatcherTask task = new MatcherTask(lines, Set.of(pattern), indexOfFirstLine);
        Map<String, List<WordLocation>> result = task.call();

        assertEquals(1, result.size());
        List<WordLocation> wordLocations = result.get(pattern.pattern());
        assertEquals(wordLocations.size(), 3);

        assertLineResult(wordLocations.get(0), indexOfFirstLine, 7);
        assertLineResult(wordLocations.get(1), indexOfFirstLine + 1, 7);
        assertLineResult(wordLocations.get(2), indexOfFirstLine + 2, 8);
    }

    @Test
    public void matcher_findPatternIgnoreCase_matchesAreNotFound() {
        List<String> lines = List.of("First Line", "SecondLine");
        Pattern pattern = Pattern.compile("line");
        int indexOfFirstLine = 1;
        MatcherTask task = new MatcherTask(lines, Set.of(pattern), indexOfFirstLine);
        Map<String, List<WordLocation>> result = task.call();

        assertEquals(1, result.size());
        List<WordLocation> wordLocations = result.get(pattern.pattern());
        assertEquals(wordLocations.size(), 0);
    }

    public void assertLineResult(WordLocation wordLocation, Integer expectedLineOffset, Integer expectedCharOffset) {
        assertEquals(expectedLineOffset, wordLocation.getLineOffset());
        assertEquals(expectedCharOffset, wordLocation.getCharOffset());
    }
}