package aggreagator;

import dto.WordLocation;
import exception.AggregatorException;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.function.BinaryOperator;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class AggregatorImpl implements Aggregator {

    @Override
    public void aggregateAndPrint(List<Future<Map<String, List<WordLocation>>>> futures) {
        Map<String, List<WordLocation>> searchResult = futures.stream()
                .map(this::getResult)
                .flatMap(entry -> entry.entrySet().stream())
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, mergeLists()));

        print(searchResult);
    }

    private BinaryOperator<List<WordLocation>> mergeLists() {
        return (l1, l2) -> Stream.concat(l1.stream(), l2.stream()).collect(Collectors.toList());
    }

    private Map<String, List<WordLocation>> getResult(Future<Map<String, List<WordLocation>>> future) {
        try {
            return future.get();
        } catch (InterruptedException | ExecutionException e) {
            throw new AggregatorException("One of the tasks failed", e);
        }
    }

    private void print(Map<String, List<WordLocation>> result) {
        result.forEach((key, value) -> System.out.println(key + " --> " + value));
    }
}
