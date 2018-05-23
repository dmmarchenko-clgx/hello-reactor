package com.github.dmmarchenko.examples;

import java.util.concurrent.TimeUnit;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

@Slf4j
public class InnerMonos {

    public static void main(String[] args) {
        Mono.fromRunnable(() -> {
            log.info("Starting first mono");
            Mono.zip(
                waitingMono(1, "waiting-1"),
                waitingMono(1, "waiting-1"),
                waitingMono(1, "waiting-1"))
                .block();
            log.info("Ending first mono");
        })
            .then(waitingMono(2, "Then waiting"))
            .subscribeOn(Schedulers.elastic())
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
