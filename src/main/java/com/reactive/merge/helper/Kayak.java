package com.reactive.merge.helper;

import java.time.Duration;

import reactor.core.publisher.Flux;

public class Kayak {

    public static Flux<Flight> getFlights() {
        //2초동안 빠른 publisher를 받아서 출력한다..
        return Flux.merge(
                           AmericanAirlines.getFlights(),
                           Emirates.getFlights(),
                           Qatar.getFlights()
                   )
                   .take(Duration.ofSeconds(2)); // take은 기간을 지정해서 해당 기간동안만 받을수도 있다
    }

}
