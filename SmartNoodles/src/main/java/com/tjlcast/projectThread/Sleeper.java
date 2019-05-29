package com.tjlcast.projectThread;

import java.util.Random;
import java.util.concurrent.TimeUnit;

/**
 * Created by tangjialiang on 2017/12/15.
 */
public class Sleeper {

    private static final Random RANDOM = new Random() ;

    static void randSleep(double mean, double stdDev) {
        final double micros = 1_000 * (mean + RANDOM.nextGaussian() * stdDev);

        try {
            TimeUnit.MICROSECONDS.sleep((long)micros);
        } catch (InterruptedException e) {
            throw new RuntimeException(e) ;
        }
    }
}
