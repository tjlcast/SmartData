package com.tjlcast.projectThread;

import com.tjlcast.common.Event;
import com.tjlcast.common.EventConsumer;
import com.tjlcast.cpuMonitor.ProjectionMetrics;
import lombok.extern.slf4j.Slf4j;

import java.time.Duration;
import java.time.Instant;

/**
 * Created by tangjialiang on 2017/12/15.
 */
@Slf4j
public class ClientProjection implements EventConsumer {

    private final ProjectionMetrics metrics ;

    public ClientProjection(ProjectionMetrics metrics) {
        this.metrics = metrics ;
    }

    @Override
    public Event consume(Event event) {
        metrics.latency(Duration.between(event.getCreated(), Instant.now()));

        // some work here.
        Sleeper.randSleep(10, 1);

        return event ;
    }
}
