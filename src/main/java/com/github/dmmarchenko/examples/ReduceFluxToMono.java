package com.github.dmmarchenko.examples;

import lombok.Value;
import reactor.core.publisher.Flux;

public class ReduceFluxToMono {

    public static void main(String[] args) {
        Person person = Flux.just("Marchenko", "Dmytro", "Viktorovych")
            .map(Person::new)
            .doOnNext(System.out::println)
            .reduce((left, right) -> new Person(left.getName() + " " + right.getName()))
            .block();
        System.out.println(person);
    }

    @Value
    private static class Person {

        String name;
    }
}