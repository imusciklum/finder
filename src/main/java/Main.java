import finder.Finder;

import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Main {
    public static void main(String[] args) throws Exception {
        int nThreads = Runtime.getRuntime().availableProcessors();
        String source = "src/main/resources/source.txt";
        int countOfThread = nThreads * 2 - 1;

        ExecutorService executorService = Executors.newFixedThreadPool(countOfThread);
        Set<String> patterns = Set.of("James", "John", "Robert", "Michael", "William", "David", "Richard", "Charles",
              "Joseph", "Thomas", "Christopher", "Daniel", "Paul", "Mark", "Donald", "George", "Kenneth", "Steven",
              "Edward", "Brian", "Ronald", "Anthony", "Kevin", "Jason", "Matthew", "Gary", "Timothy", "Jose", "Larry",
              "Jeffrey", "Frank", "Scott", "Eric", "Stephen", "Andrew", "Raymond", "Gregory", "Joshua", "Jerry", "Dennis",
              "Walter", "Patrick", "Peter", "Harold", "Douglas", "Henry", "Carl", "Arthur", "Ryan", "Roger");
        try (Finder finder = Finder.createDefault(executorService, patterns, source)) {
            finder.find();
        }

        executorService.shutdown();
    }

}
