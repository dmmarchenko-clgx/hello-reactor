package com.github.dmmarchenko.examples;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.Comparator;
import java.util.Objects;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import java.util.function.UnaryOperator;

import com.google.common.collect.ImmutableList;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Scheduler;
import reactor.core.scheduler.Schedulers;

@Slf4j
public class CoordinationParallelFlow {

    public static void main(String[] args) {
        run(3);
    }

    private static void run(int repeat) {
        long start = System.currentTimeMillis();
        for (int i = 0; i < repeat; i++) {
            ImmutableList<Mono<ResponseBuilder>> monos = ImmutableList.of(
                createTask("Marchenko", 1, 3),
                createTask("Dmytro", 2, 2),
                createTask("Viktorovych", 3, 1)
            );

            ResponseBuilder builder = Flux.fromIterable(monos)
                .map(mono -> mono.subscribeOn(Schedulers.parallel()))
                .flatMap(Function.identity())
                .sort(Comparator.comparing(ResponseBuilder::getOrder))
                .reduce((x, y) -> x.andThen(y))
                .block();

            String fullName = builder.apply(new StringBuilder()).toString();

            log.info("Result: {}", fullName);
            assertEquals(fullName, "Marchenko Dmytro Viktorovych ");
        }
        long end = System.currentTimeMillis();
        long duration = (end - start) / 1000;
        log.info("Total duration: {}", duration);
        log.info("Duration per iteration: {}", duration / repeat);
        assertTrue(duration < repeat * 5);
    }

    @SneakyThrows
    private static void waitSeconds(int seconds) {
        TimeUnit.SECONDS.sleep(seconds);
    }

    private static Mono<ResponseBuilder> createTask(String value, int order, int duration) {
        return Mono.fromSupplier(() -> {
            waitSeconds(duration);
            log.info("Completed task: {}", value);
            return value;
        }).map(stringValue -> new ResponseBuilderImpl(stringValue, order));
    }

    @AllArgsConstructor
    @Getter
    private static class ResponseBuilderImpl implements CoordinationParallelFlow.ResponseBuilder {

        private String value;
        private int order;

        @Override
        public StringBuilder apply(final StringBuilder stringBuilder) {
            return stringBuilder
                .append(value)
                .append(" ");
        }
    }

    private interface ResponseBuilder extends UnaryOperator<StringBuilder> {

        default int getOrder() {
            return 0;
        }

        default ResponseBuilder andThen(ResponseBuilder after) {
            Objects.requireNonNull(after);
            return (StringBuilder response) -> after.apply(apply(response));
        }
    }
}
