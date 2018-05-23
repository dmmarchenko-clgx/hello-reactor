package com.github.dmmarchenko.examples;

import java.util.concurrent.TimeUnit;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

@Slf4j
public class InnerMonos {

    public static void main(String[] args) {
        Mono.defer(() ->
            Mono.just("hello")
                .subscribeOn(Schedulers.newSingle("Very first"))
                .flatMap(value -> Mono
                    .just(value + "!")
                    .doOnNext(innerValue -> log.info("Bottom value: {}", innerValue))
                    .subscribeOn(Schedulers.newSingle("Bottom"))
                )
                .map(String::toUpperCase)
                .doOnNext(value -> log.info("Middle value: {}", value))
                .subscribeOn(Schedulers.newSingle("Middle"))
        )
            .doOnNext(value -> log.info("Top value: {}", value))
            .subscribeOn(Schedulers.newSingle("Top"))
            .block();
    }

    @SneakyThrows
    private static void waitSeconds(int seconds) {
        TimeUnit.SECONDS.sleep(seconds);
    }

    private static Mono<Object> waitingMono(int seconds, String monoName) {
        return Mono.fromRunnable(() -> {
            log.info("Starting {}", monoName);
            waitSeconds(seconds);
            log.info("Ending {}", monoName);
        }).subscribeOn(Schedulers.parallel());
    }
}
