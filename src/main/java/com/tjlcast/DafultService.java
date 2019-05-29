package com.tjlcast;

import rx.Observable;
import rx.Subscriber;

/**
 * Created by tangjialiang on 2017/12/15.
 */

public class DafultService {

    public static void main(String[] args) {


        Observable observable = Observable.create(new Observable.OnSubscribe<String>() {
            @Override
            public void call(Subscriber<? super String> subscriber) {
                subscriber.onNext("Hello");
                subscriber.onNext("Hi");
                subscriber.onNext("Aloha");
                subscriber.onCompleted();
            }
        });

        Observable observable1 = Observable.just("Hello", "Hi", "Aloha");

        Subscriber<String> subscriber = new Subscriber<String>() {
            @Override
            public void onNext(String s) {
                System.out.println("Item: " + s);
            }

            @Override
            public void onCompleted() {
                System.out.println("Completed!");
            }

            @Override
            public void onError(Throwable e) {
                System.out.println("Error!");
            }
        };

        observable.subscribe(subscriber);

    }
}
