package com.reactive.startWith.helper;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.reactive.common.Util;

import reactor.core.publisher.Flux;

public class NameGenrator {

	private static final Logger log = LoggerFactory.getLogger(NameGenrator.class);
	private final List<String> redis = new ArrayList<>(); //start with로 캐싱 효과를 누린다

	public Flux<String> generateNames(){
		return Flux.generate((sink -> {
			log.info("Generating name");
			Util.sleepSeconds(1);
			var name = Util.faker().name().fullName();
			redis.add(name);
			sink.next(name);
		})).startWith(redis)
			.cast(String.class);
	}
}
