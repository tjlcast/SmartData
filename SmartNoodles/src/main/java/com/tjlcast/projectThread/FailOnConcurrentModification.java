package com.tjlcast.projectThread;

import com.tjlcast.common.Event;
import com.tjlcast.common.EventConsumer;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Created by tangjialiang on 2017/12/15.
 */

@Slf4j
public class FailOnConcurrentModification implements EventConsumer {

    private final ConcurrentMap<Integer, Lock> clientLocks = new ConcurrentHashMap<>() ;

    private final EventConsumer downstream ;

    public FailOnConcurrentModification(EventConsumer downstream) {
        this.downstream = downstream;
    }

    @Override
    public Event consume(Event event) {
        Lock lock = findClientLock(event);
        if (lock.tryLock()) {
            try {
                downstream.consume(event) ;
            } finally {
                lock.unlock();
            }
        } else {
            log.error("Client {} already being modified by another thread", event.getClientId()) ;
        }
        return event;
    }

    private Lock findClientLock(Event event) {
        return clientLocks.computeIfAbsent(
                event.getClientId(),
                clientId -> new ReentrantLock()
        ) ;
    }
}
