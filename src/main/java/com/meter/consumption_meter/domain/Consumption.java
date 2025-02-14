package com.meter.consumption_meter.domain;

import java.time.OffsetDateTime;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

@Getter
@Builder
@ToString
@EqualsAndHashCode
public class Consumption {
    private OffsetDateTime timeOfReading;
    private long wattHours;
}
