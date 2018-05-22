package com.github.dmmarchenko.examples;

import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;
import reactor.util.function.Tuple4;

@Slf4j
public class ComputeInParallel {

    public static void main(String[] args) {
        Tuple4<Integer, Integer, Integer, Integer> result = Mono.zip(
            call(() -> longTask(1)),
            call(() -> longTask(2)),
            call(ComputeInParallel::nullTask).defaultIfEmpty(0),
            call(() -> longTask(3)))
            .block();

        log.info("Result: {}", result);
    }

    private static <T> Mono<T> call(final Supplier<T> supplier) {
        return Mono.fromSupplier(supplier)
            .subscribeOn(Schedulers.parallel());
    }

    private static Integer nullTask() {
        return null;
    }

    @SneakyThrows
    private static Integer longTask(int id) {
        log.info("Starting task: {}", id);
        TimeUnit.SECONDS.sleep(5);
        log.info("Ending task: {}", id);
        return id * 2;
    }
}
