package com.github.dmmarchenko.examples;

import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;
import reactor.core.scheduler.Schedulers;

@Slf4j
public class Subscribe {

    public static void main(String[] args) {
        Flux.range(1, 10)
            .doOnNext(number -> log.info("Next number: {}", number))
            .publishOn(Schedulers.parallel())
            .subscribe(number -> log.info("{}", number));
    }
}
