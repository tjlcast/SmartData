package com.tjlcast.common;

/**
 * Created by tangjialiang on 2017/12/15.
 */

@FunctionalInterface
public interface EventConsumer {
    public Event consume(Event event) ;
}
