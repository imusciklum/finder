package alternativesolutions.forkjoinpoolsolution;

import dto.WordLocation;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ForkJoinPool;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class Main {
    public static void main(String[] args) throws IOException {
        ForkJoinPool forkJoinPool = ForkJoinPool.commonPool();

            long start  = System.currentTimeMillis();
            Path fileName = Path.of("src/main/resources/source.txt");
            List<String> actual = Files.readAllLines(fileName);

            Set<String> patterns = Set.of("James", "John", "Robert", "Michael", "William", "David", "Richard", "Charles", "Joseph", "Thomas", "Christopher", "Daniel", "Paul", "Mark", "Donald", "George", "Kenneth", "Steven", "Edward", "Brian", "Ronald", "Anthony", "Kevin", "Jason", "Matthew", "Gary", "Timothy", "Jose", "Larry", "Jeffrey", "Frank", "Scott", "Eric", "Stephen", "Andrew", "Raymond", "Gregory", "Joshua", "Jerry", "Dennis", "Walter", "Patrick", "Peter", "Harold", "Douglas", "Henry", "Carl", "Arthur", "Ryan", "Roger");

            List<Pattern> precompiledPatterns = patterns.stream().map(Pattern::compile).collect(Collectors.toList());

            MyRecursiveTask arthur = new MyRecursiveTask(actual, 1000, precompiledPatterns, 1);

            Map<String, List<WordLocation>> result = forkJoinPool.invoke(arthur);
            result.forEach((key, value) -> System.out.println(key + " --> " + value));

            long end = System.currentTimeMillis();
            System.out.println((end - start) / 1000d);

        }
}
