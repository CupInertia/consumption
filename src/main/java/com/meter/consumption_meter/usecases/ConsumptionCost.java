package com.meter.consumption_meter.usecases;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

@Builder
@Getter
@ToString
@EqualsAndHashCode
public class ConsumptionCost {
    private OffsetDateTime timestamp;
    private BigDecimal kilowattHoursConsumed;
    private BigDecimal cost;
}
