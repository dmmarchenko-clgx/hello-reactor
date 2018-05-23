package com.github.dmmarchenko.examples;

import java.util.Map;
import java.util.function.Function;

import com.google.common.collect.ImmutableMap;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Slf4j
public class Expand {

    private static final Map<String, String> countryMap = ImmutableMap.<String, String>builder()
        .put("Kiyv", "Ukraine")
        .put("New York", "USA")
        .build();

    public static void main(String[] args) {
        Mono<String> nameMono = Mono.just("Dima");
        Mono<Integer> ageMono = Mono.just(26);
        Mono<String> cityMono = Mono.just("Kiyv");
        Mono<String> countryMono = cityMono.map(countryMap::get);
        Mono<Boolean> adultMono = ageMono.map(age -> age >= 18);

        Person grandpa = new Person("Vasyl", 80, true, "Nizhyn", "Ukraine", null, null);
        Person father = new Person("Viktor", 57, true, "Nizhyn", "Ukraine", grandpa, null);
        Person mother = new Person("Ludmila", 57, true, "Nizhyn", "Ukraine", null, null);

        Mono<Person> personMono = Mono.zip(nameMono, ageMono, adultMono, cityMono, countryMono)
            .map(tuple -> new Person(tuple.getT1(), tuple.getT2(), tuple.getT3(), tuple.getT4(), tuple.getT5(), father, mother));

        personMono.expandDeep(person -> Flux
            .just(Mono.justOrEmpty(person.getFather()), Mono.justOrEmpty(person.getMother())).flatMap(Function.identity()))
            .subscribe(p -> log.info("Expanded person: {}", p));
    }

    @Data
    @AllArgsConstructor
    private static class Person {

        String name;
        int age;
        boolean adult;
        String city;
        String country;
        @Setter
        Person father;
        @Setter
        Person mother;
    }

}
