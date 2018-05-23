package com.github.dmmarchenko.examples;

import reactor.core.publisher.Flux;

public class Merge {

    public static void main(String[] args) {
        Flux.merge(Flux.range(1, 10), Flux.range(30, 40))
            .subscribe(System.out::println);
    }
}
