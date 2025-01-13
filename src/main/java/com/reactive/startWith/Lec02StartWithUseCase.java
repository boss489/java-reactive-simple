package com.reactive.startWith;

import com.reactive.common.Util;
import com.reactive.startWith.helper.NameGenrator;

public class Lec02StartWithUseCase {

	public static void main(String[] args) {
		var nameGenrator = new NameGenrator();

		nameGenrator.generateNames()
			.take(2)
			.subscribe(Util.subscriber("sam"));

		nameGenrator.generateNames()
			.take(2)
			.subscribe(Util.subscriber("mike"));


		nameGenrator.generateNames()
			.take(2)
			.subscribe(Util.subscriber("jake"));

	}
}
