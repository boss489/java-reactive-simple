package com.reactive.operation.zip;

import com.reactive.operation.common.Util;
import com.reactive.operation.zip.helper.ExternalServiceClient;

public class Lec07ZipAssignment {

	public static void main(String[] args) {
		var client = new ExternalServiceClient();
		client.getProduct(1)
			.subscribe(
				data -> System.out.println(data)
			);
		Util.sleepSeconds(5);
	}

}
