package com.meter.consumption_meter.usecases;

import com.meter.consumption_meter.domain.MeteringPoint;
import java.math.BigDecimal;
import java.util.Date;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

@Builder
@Getter
@ToString
@EqualsAndHashCode
public class ConsumptionCost {
    private MeteringPoint meteringPoint;
    private Date timestamp;
    private BigDecimal kiloWattHoursConsumed;
    private BigDecimal cost;
}
