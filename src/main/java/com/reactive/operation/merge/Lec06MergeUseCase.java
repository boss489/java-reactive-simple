package com.reactive.operation.merge;

import com.reactive.operation.common.Util;
import com.reactive.operation.merge.helper.Kayak;

public class Lec06MergeUseCase {

    public static void main(String[] args) {

        Kayak.getFlights()
                .subscribe(Util.subscriber());


        Util.sleepSeconds(3);


    }

}
