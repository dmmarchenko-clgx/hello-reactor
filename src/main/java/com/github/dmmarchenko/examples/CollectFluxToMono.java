package com.github.dmmarchenko.examples;

import java.util.List;

import com.google.common.collect.ImmutableList;

import lombok.Value;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public class CollectFluxToMono {

    public static void main(String[] args) {
        Mono<List<Person>> listMono = Flux.just("Dima", "Natasha", "Kevin", "Petro")
            .map(Person::new)
            .map(ImmutableList::of)
            .flatMap(Mono::just)
            //.doOnNext(System.out::println)
            .flatMap(Flux::fromIterable)
            .collectList();

        //System.out.println(listMono.block());
        System.out.println("------------");

        Person person = Flux.just("Marchenko", "Dmytro", "Viktorovych")
            .map(Person::new)
            .doOnNext(System.out::println)
            .reduce((left, right) -> new Person(left.getName() + " " + right.getName()))
            .block();
        System.out.println(person);
    }
}

@Value
class Person {

    String name;
}
