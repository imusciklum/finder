package aggreagator;

import dto.WordLocation;

import java.util.List;
import java.util.Map;
import java.util.concurrent.Future;

public interface Aggregator {
    void aggregateAndPrint(List<Future<Map<String, List<WordLocation>>>> futures);
}
