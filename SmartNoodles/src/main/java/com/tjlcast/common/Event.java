package com.tjlcast.common;

import lombok.Data;

import java.time.Instant;
import java.util.UUID;

/**
 * Created by tangjialiang on 2017/12/15.
 */

@Data
public class Event {

    private final Instant created = Instant.now();
    private final int clientId;
    private final UUID uuid;

}
