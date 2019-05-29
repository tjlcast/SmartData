package com.tjlcast;

import com.codahale.metrics.MetricRegistry;
import com.tjlcast.common.Event;
import com.tjlcast.common.TjlEventStream;
import com.tjlcast.cpuMonitor.ProjectionMetrics;
import com.tjlcast.projectThread.*;

import java.util.UUID;

/**
 * Created by tangjialiang on 2017/12/15.
 *
 */

public class App {

    public static void main(String[] args) {
        failBoot();

        try {
            Thread.sleep(10000000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public static void oldBoot() {
        MetricRegistry metricRegistry = new MetricRegistry();
        ProjectionMetrics metrics = new ProjectionMetrics(metricRegistry);

        ClientProjection clientProjection = new ClientProjection(metrics);
        NaivePool naivePool = new NaivePool(10, clientProjection);

        TjlEventStream es = new TjlEventStream();  //some real implementation here
        es.consume(naivePool) ;
    }

    public static void failBoot() {
        MetricRegistry metricRegistry = new MetricRegistry();
        ClientProjection clientProjection = new ClientProjection(new ProjectionMetrics(metricRegistry));

        FailOnConcurrentModification failOnConcurrentModification = new FailOnConcurrentModification(clientProjection);
        NaivePool naivePool = new NaivePool(10, failOnConcurrentModification, metricRegistry);

        TjlEventStream tjlEventStream = new TjlEventStream();
        tjlEventStream.consume(naivePool) ;
    }

    public static void testBoot() {
        MetricRegistry metricRegistry = new MetricRegistry();
        ProjectionMetrics metrics = new ProjectionMetrics(metricRegistry);

        ClientProjection clientProjection = new ClientProjection(metrics);
        NaivePool naivePool = new NaivePool(10, clientProjection);

        Event event = new Event(10, UUID.randomUUID()) ;
        naivePool.consume(event) ;
    }

    public static void testBootG() {
        MetricRegistry metricRegistry = new MetricRegistry();
        ClientProjection clientProjection =
                new ClientProjection(new ProjectionMetrics(metricRegistry));
        FailOnConcurrentModification concurrentModification =
                new FailOnConcurrentModification(clientProjection);
        SmartPool smartPool =
                new SmartPool(12, concurrentModification);
        IgnoreDuplicates withoutDuplicates =
                new IgnoreDuplicates(smartPool, metricRegistry);

        TjlEventStream tjlEventStream = new TjlEventStream();
        tjlEventStream.consume(withoutDuplicates);
    }

    public static void finaBoot() {
        /**
         *
         1\ 首先我们应用IgnoreDuplicates以排除重复的event
         2\ 然后我们调用SmartPool，它总是会将给定的clientId送到指定的单线程池，接着在那个线程完成后续操作
         3\ 最终ClientProjection被调用以执行真正的业务逻辑
         */
        MetricRegistry metricRegistry = new MetricRegistry();
        ClientProjection clientProjection = new ClientProjection(new ProjectionMetrics(metricRegistry));

        FailOnConcurrentModification failOnConcurrentModification = new FailOnConcurrentModification(null);

        SmartPool smartPool = new SmartPool(10, null);

        IgnoreDuplicates ignoreDuplicates = new IgnoreDuplicates(null, metricRegistry);

        TjlEventStream tjlEventStream = new TjlEventStream();
        tjlEventStream.consume(ignoreDuplicates);

    }

}
