package com.tjlcast.common;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomUtils;
import rx.Observable;
import rx.schedulers.Schedulers;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * Created by tangjialiang on 2017/12/15.
 */

@Slf4j
public class TjlEventStream implements EventStream {

    @Override
    public void consume(EventConsumer consumer) {
        observe()
                .subscribe(
                        consumer::consume,
                        e -> log.error("Error emitting event", e)
                );
    }

    public Observable<Event> observe() {
        return Observable
                .interval(1, TimeUnit.MILLISECONDS)
                .delay(x -> Observable.timer(RandomUtils.nextInt(0, 1_000), TimeUnit.MICROSECONDS))
                .map(x -> new Event(RandomUtils.nextInt(1_000, 1_100), UUID.randomUUID()))
                .flatMap(this::occasionallyDuplicate)
                .observeOn(Schedulers.io());
    }

    private Observable<Event> occasionallyDuplicate(Event x) {
        final Observable<Event> event = Observable.just(x);
        if (Math.random() >= 0.01) {
            return event;
        }
        final Observable<Event> duplicated = event.delay(RandomUtils.nextInt(10, 5_000), TimeUnit.MILLISECONDS);
        return event.concatWith(duplicated);
    }
}