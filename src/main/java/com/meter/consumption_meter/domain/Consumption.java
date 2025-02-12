package com.meter.consumption_meter.domain;

import java.util.Date;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

@Getter
@Builder
@ToString
@EqualsAndHashCode
public class Consumption {
    private Date timeOfReading;
    private float numberOfKiloWattHours;
}
