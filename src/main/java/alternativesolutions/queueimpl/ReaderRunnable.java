package alternativesolutions.queueimpl;

import lombok.SneakyThrows;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

public class ReaderRunnable implements Runnable {
    private final ConcurrentLinkedQueue<LineInfo> blockingQueue;
    private final String source;
    private final AtomicBoolean readingIsEnded;

    public ReaderRunnable(ConcurrentLinkedQueue<LineInfo> blockingQueue, String source, AtomicBoolean readingIsEnded) {
        this.blockingQueue = blockingQueue;
        this.source = source;
        this.readingIsEnded = readingIsEnded;
    }

    @SneakyThrows
    @Override
    public void run() {
        AtomicInteger index = new AtomicInteger(1);
        Files.lines(Path.of(source)).forEach(line -> {
            LineInfo LineInfo = new LineInfo(line, index.get());
            blockingQueue.add(LineInfo);
            index.getAndIncrement();
        });
        readingIsEnded.set(true);

    }
}