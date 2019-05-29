package com.tjlcast.cpuMonitor;

import com.codahale.metrics.Histogram;
import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.Slf4jReporter;
import lombok.extern.slf4j.Slf4j;

import java.time.Duration;
import java.util.concurrent.TimeUnit;

/**
 * Created by tangjialiang on 2017/12/15.
 */
@Slf4j
public class ProjectionMetrics {

    private final Histogram latencyHist ;

    public ProjectionMetrics(MetricRegistry metricRegistry) {
        final Slf4jReporter reporter =
                Slf4jReporter.forRegistry(metricRegistry)
                        .outputTo(log)
                        .convertDurationsTo(TimeUnit.SECONDS)
                        .convertDurationsTo(TimeUnit.MILLISECONDS)
                        .build() ;

        reporter.start(1, TimeUnit.SECONDS);
        latencyHist = metricRegistry.histogram(MetricRegistry.name(ProjectionMetrics.class, "latency"));
    }

    public void latency(Duration duration) {
        latencyHist.update(duration.toMillis());
    }
}
