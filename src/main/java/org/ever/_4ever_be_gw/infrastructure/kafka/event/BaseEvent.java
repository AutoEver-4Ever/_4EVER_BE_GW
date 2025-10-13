package org.ever._4ever_be_gw.infrastructure.kafka.event;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;

@Getter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public abstract class BaseEvent {

    private String eventId;
    private String eventType;
    private LocalDateTime timestamp;
    private String source;
}
