package com.meter.consumption_meter.domain.ports.out;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

public interface CostPort {
    public BigDecimal getCostPerKiloWattHourInCents(OffsetDateTime date);
}
