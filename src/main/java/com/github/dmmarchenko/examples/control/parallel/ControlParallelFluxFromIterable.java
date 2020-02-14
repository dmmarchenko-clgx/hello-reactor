package com.github.dmmarchenko.examples.control.parallel;

import java.util.List;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;
import reactor.core.scheduler.Schedulers;

@Slf4j
public class ControlParallelFluxFromIterable {

    private static final int CHUNK_SIZE = 20;
    private static final Semaphore semaphore = new Semaphore(1);

    public static void main(String[] args) throws Exception {
        List<Integer> ids = IntStream.range(0, 3000)
            .boxed()
            .collect(Collectors.toList());
        semaphore.acquire();

        Flux.fromIterable(ids)
            .buffer(CHUNK_SIZE)
            .doOnNext(chunk -> log.info("Take {} elements to processing", chunk.size()))
            .parallel()
            .runOn(Schedulers.parallel())
            .map(ControlParallelFluxFromIterable::processChunk)
            .doOnNext(len -> log.info("Processed {} elements", len))
            .sequential()
            .reduce(Integer::sum)
            .subscribe(result -> {
                log.info("Gor final result: {}", result);
                semaphore.release();
            });

        semaphore.acquire();
    }

    @SneakyThrows
    private static Integer processChunk(List<Integer> chunk) {
        TimeUnit.SECONDS.sleep(1);
        return chunk.size();
    }
}
