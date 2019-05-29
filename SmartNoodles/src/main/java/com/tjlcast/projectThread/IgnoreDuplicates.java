package com.tjlcast.projectThread;

import com.codahale.metrics.Gauge;
import com.codahale.metrics.Meter;
import com.codahale.metrics.Metric;
import com.codahale.metrics.MetricRegistry;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.tjlcast.common.Event;
import com.tjlcast.common.EventConsumer;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * Created by tangjialiang on 2017/12/16.
 */
public class IgnoreDuplicates implements EventConsumer {

    private final EventConsumer downstream ;
    private final Meter duplicates ;

    private Cache<UUID, UUID> sessUuids = CacheBuilder.newBuilder()
            .expireAfterWrite(10, TimeUnit.SECONDS)
            .build() ;

    public IgnoreDuplicates(EventConsumer downstream, MetricRegistry metricRegistry) {
        this.downstream = downstream ;
        this.duplicates = metricRegistry.meter(MetricRegistry.name(IgnoreDuplicates.class, "duplicates")) ;
        metricRegistry.register(MetricRegistry.name(IgnoreDuplicates.class, "cacheSize"), (Gauge<Long>) sessUuids::size) ;
    }


    @Override
    public Event consume(Event event) {
        final UUID uuid = event.getUuid() ;
        if (sessUuids.asMap().putIfAbsent(uuid, uuid)==null) {
            return downstream.consume(event) ;
        } else {
            duplicates.mark();
            return event ;
        }
    }
}
