package com.tjlcast.projectThread;

import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.Timer;
import com.tjlcast.common.Event;
import com.tjlcast.common.EventConsumer;
import lombok.extern.slf4j.Slf4j;


import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Created by tangjialiang on 2017/12/15.
 */
@Slf4j
public class WaitOnConcurrentModification implements EventConsumer {

    private final ConcurrentMap<Integer, Lock> clientLocks = new ConcurrentHashMap<>() ;
    private final EventConsumer downstream ;
    private final Timer lockWait ;

    public WaitOnConcurrentModification(EventConsumer downstream, MetricRegistry metricRegistry) {
        this.downstream = downstream;
        this.lockWait = metricRegistry.timer(MetricRegistry.name(WaitOnConcurrentModification.class, "lockWait"));
    }

    @Override
    public Event consume(Event event) {
        try {
            final Lock lock = findClientLock(event) ;
            final Timer.Context time = lockWait.time() ;
            try {
                final boolean locked = lock.tryLock(1, TimeUnit.SECONDS) ;
                time.stop() ;
                if(locked) {
                    downstream.consume(event) ;
                }
            } finally {
                lock.unlock();
            }
        } catch (InterruptedException e) {
            log.warn("Interrupted", e) ;
        }
        return event ;
    }

    private Lock findClientLock(Event event) {
        Lock lock = clientLocks.computeIfAbsent(
                event.getClientId(),
                clientId -> new ReentrantLock());

        return lock ;
    }
}
