package com.github.dmmarchenko;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.DayOfWeek;
import java.time.Duration;
import java.time.Month;


@RestController
@Slf4j
public class Controller {

    @PostMapping("/home")
    public Mono<Void> home(@RequestBody InputRequest request) {
        Flux<Month> monthsFlux = Flux
                .fromIterable(request.getMonths())
                .delaySubscription(Duration.ofSeconds(1))
                .map(String::toUpperCase)
                .map(Month::valueOf)
                .sort()
                .log("Months");

        Flux<DayOfWeek> weekDaysFlux = Flux
                .fromIterable(request.getWeekDays())
                .delaySubscription(Duration.ofMillis(500))
                .map(String::toUpperCase)
                .map(DayOfWeek::valueOf)
                .sort()
                .log("WeekDays");

        return monthsFlux.then().and(weekDaysFlux.then());
    }
}
