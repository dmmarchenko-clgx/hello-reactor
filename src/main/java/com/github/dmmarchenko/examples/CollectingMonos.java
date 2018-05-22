package com.github.dmmarchenko.examples;

import java.time.Duration;
import java.util.function.Function;

import lombok.Value;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;
import reactor.util.function.Tuple2;

@Slf4j
public class CollectingMonos {

    public static void main(String[] args) {
        Person person = Mono.zip(getName(), getAge())
            .map(createPerson())
            .map(CollectingMonos::uppercase)
            .block();

        log.info("Created person: {}", person);
    }

    private static Function<Tuple2<String, Integer>, Person> createPerson() {
        return tuple -> {
            log.info("Creating Person");
            return new Person(tuple.getT1(), tuple.getT2());
        };
    }

    private static Mono<String> getName() {
        log.info("Getting name");
        return Mono.just("Dima")
            .delayElement(Duration.ofSeconds(2))
            .doOnNext(name -> log.info("Got name"));
    }

    private static Mono<Integer> getAge() {
        log.info("Getting age");
        return Mono.just(26)
            .delayElement(Duration.ofSeconds(2))
            .doOnNext(age -> log.info("Got age"));
    }

    private static Person uppercase(Person person) {
        log.info("Uppercasing");
        return new Person(person.getName().toUpperCase(), person.getAge());
    }

    @Value
    private static class Person {

        String name;
        int age;
    }
}
