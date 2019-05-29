package com.tjlcast.projectThread;

import com.codahale.metrics.Gauge;
import com.codahale.metrics.MetricRegistry;
import com.tjlcast.common.Event;
import com.tjlcast.common.EventConsumer;
import com.tjlcast.cpuMonitor.ProjectionMetrics;

import java.io.Closeable;
import java.io.IOException;
import java.util.concurrent.*;

/**
 * Created by tangjialiang on 2017/12/15.
 */
public class NaivePool implements EventConsumer, Closeable{

    private final EventConsumer downstream ;
    private final ExecutorService executorService ;

    public NaivePool(int size, EventConsumer downstream) {
        this.executorService = Executors.newFixedThreadPool(size);
        this.downstream = downstream;
    }


    public NaivePool(int size, EventConsumer downstream, MetricRegistry metricRegistry) {
        LinkedBlockingQueue<Runnable> queue = new LinkedBlockingQueue<>();
        String name = MetricRegistry.name(ProjectionMetrics.class, "queue");

        Gauge<Integer> gauge = queue::size;
        metricRegistry.register(name, gauge);

        //this.executorService = Executors.newFixedThreadPool(size) ;
        this.executorService = new ThreadPoolExecutor(size, size, 0L, TimeUnit.MILLISECONDS, queue) ;
        this.downstream = downstream ;
    }

    @Override
    public Event consume(Event event) {
        executorService.submit(() -> downstream.consume(event)) ;
        return event;
    }

    @Override
    public void close() throws IOException {
        executorService.shutdown();
    }
}
