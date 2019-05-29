package com.tjlcast.demo;

import com.tjlcast.common.Event;
import org.apache.commons.lang3.RandomUtils;
import rx.Observable;

import rx.functions.Action1;
import rx.schedulers.Schedulers;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * Created by tangjialiang on 2017/12/15.
 */
public class GenerateData1 {
    public static void main(String[] args) {

        Observable<Event> rObservable = Observable
                .interval(1, TimeUnit.MILLISECONDS)
                .delay(x -> Observable.timer(RandomUtils.nextInt(0, 1_000), TimeUnit.MICROSECONDS))
                .map(x -> new Event(RandomUtils.nextInt(1_000, 1_100), UUID.randomUUID()))
                .observeOn(Schedulers.io());

        rObservable
                .subscribe(
                        new Action1<Event>() {
                            @Override
                            public void call(Event event) {
                                System.out.println(event.toString()) ;
                            }
                        }
                ) ;

        try {
            Thread.sleep((long)100000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public static void work(Long event) {
        System.out.println(event) ;
    }
}
