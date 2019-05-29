package com.tjlcast.projectThread;

import com.codahale.metrics.Gauge;
import com.codahale.metrics.MetricRegistry;
import com.tjlcast.common.Event;
import com.tjlcast.common.EventConsumer;
import com.tjlcast.cpuMonitor.ProjectionMetrics;

import java.io.Closeable;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * Created by tangjialiang on 2017/12/15.
 */
public class SmartPool implements EventConsumer, Closeable {

    private final List<ExecutorService> threadPools ;
    private final EventConsumer downstream ;

    public SmartPool(int size, EventConsumer downstream) {
        this.downstream = downstream;

        List<ExecutorService> list = IntStream
                .range(0, size)
                .mapToObj(i -> Executors.newSingleThreadExecutor())
                .collect(Collectors.toList());

        this.threadPools = new CopyOnWriteArrayList<>(list) ;
    }

    @Override
    public Event consume(Event event) {
        final int threadIdx = event.getClientId() % threadPools.size() ;
        final ExecutorService executorService = threadPools.get(threadIdx) ;
        executorService.submit(() -> downstream.consume(event)) ;
        return null;
    }

    @Override
    public void close() throws IOException {
        threadPools.forEach(ExecutorService::shutdown);
    }
}

class SmartPoolPro implements EventConsumer, Closeable {
    List<LinkedBlockingDeque<Runnable>> queues ;
    private final List<ExecutorService> threadPools ;
    private final EventConsumer downstream ;

    public SmartPoolPro(int size, EventConsumer downstream, MetricRegistry metricRegistry) {
        this.downstream = downstream ;
        this.queues = IntStream
                .range(0, size)
                .mapToObj(i -> new LinkedBlockingDeque<Runnable>())
                .collect(Collectors.toList());
        List<ThreadPoolExecutor> list = queues
                .stream()
                .map(q -> new ThreadPoolExecutor(1,1, 0L, TimeUnit.MILLISECONDS, q))
                .collect(Collectors.toList());
        this.threadPools = new CopyOnWriteArrayList<>(list) ;
        metricRegistry.register(MetricRegistry.name(ProjectionMetrics.class, "queue"), (Gauge<Double>)this::averageQueueLength) ;


    }

    private double averageQueueLength() {
        double totalLength =
            queues
                .stream()
                .mapToDouble(LinkedBlockingDeque::size)
                .sum();
        return totalLength / queues.size();
    }


    @Override
    public Event consume(Event event) {
        final int threadIdx = event.getClientId() % threadPools.size() ;
        final ExecutorService executorService = threadPools.get(threadIdx) ;
        executorService.submit(() -> downstream.consume(event)) ;
        return null;
    }

    @Override
    public void close() throws IOException {
        threadPools.forEach(ExecutorService::shutdown);
    }
}
