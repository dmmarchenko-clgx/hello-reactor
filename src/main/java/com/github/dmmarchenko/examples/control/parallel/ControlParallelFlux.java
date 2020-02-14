package com.github.dmmarchenko.examples.control.parallel;

import java.util.Iterator;
import java.util.List;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;
import reactor.core.scheduler.Schedulers;

@SuppressWarnings("Duplicates")
@Slf4j
public class ControlParallelFlux {

    private static final int CHUNK_SIZE = 20;
    private static final Semaphore semaphore = new Semaphore(1);
    private static volatile boolean stopped = false;

    public static void main(String[] args) throws Exception {
        List<Integer> ids = IntStream.range(0, 3000)
            .boxed()
            .collect(Collectors.toList());
        semaphore.acquire();

        Iterator<Integer> iterator = ids.iterator();
        Flux.<Integer>generate(sink -> {
            if (iterator.hasNext() && !stopped) {
                sink.next(iterator.next());
            } else {
                sink.complete();
            }
        })
            .buffer(CHUNK_SIZE)
            .doOnNext(chunk -> log.info("Take {} elements to processing", chunk.size()))
            .parallel()
            .runOn(Schedulers.parallel())
            .map(ControlParallelFlux::processChunk)
            .doOnNext(len -> log.info("Processed {} elements", len))
            .sequential()
            .reduce(Integer::sum)
            .subscribe(result -> {
                log.info("Gor final result: {}", result);
                semaphore.release();
            }, error -> {
                log.error("Error occured. ", error);
                semaphore.release();
            });

        semaphore.acquire();
    }

    @SneakyThrows
    private static Integer processChunk(List<Integer> chunk) {
        TimeUnit.SECONDS.sleep(1);

        if (chunk.get(0).equals(2000)) {
            stopped = true;
        }

        return chunk.size();
    }
}
