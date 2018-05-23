package com.github.dmmarchenko.examples;

import java.util.Objects;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import java.util.function.UnaryOperator;

import com.google.common.collect.ImmutableList;

import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

@Slf4j
public class ComplexFlow {

    public static void main(String[] args) {
        ImmutableList<Mono<ResponseBuilder>> monos = ImmutableList.of(
            createTask("Marchenko", 3).map(ResponseBuilderImpl::new),
            createTask("Dmytro", 3).map(ResponseBuilderImpl::new),
            createTask("Viktorovych", 3).map(ResponseBuilderImpl::new)
        );

        ResponseBuilder builder = Flux.fromIterable(monos)
            .flatMap(Function.identity())
            .reduce((x, y) -> x.andThen(y))
            .block();

        String fullName = builder.apply(new StringBuilder()).toString();

        log.info("Result: {}", fullName);
    }

    @SneakyThrows
    private static void waitSeconds(int seconds) {
        TimeUnit.SECONDS.sleep(seconds);
    }

    private static Mono<String> createTask(String value, int duration) {
        return Mono.fromSupplier(() -> {
            waitSeconds(duration);
            log.info("Completed task: {}", value);
            return value;
        });
    }

    @AllArgsConstructor
    private static class ResponseBuilderImpl implements ComplexFlow.ResponseBuilder {

        private String value;

        @Override
        public StringBuilder apply(final StringBuilder stringBuilder) {
            return stringBuilder
                .append(value)
                .append(" ");
        }
    }

    private interface ResponseBuilder extends UnaryOperator<StringBuilder> {

        default ResponseBuilder andThen(ResponseBuilder after) {
            Objects.requireNonNull(after);
            return (StringBuilder response) -> after.apply(apply(response));
        }
    }
}
