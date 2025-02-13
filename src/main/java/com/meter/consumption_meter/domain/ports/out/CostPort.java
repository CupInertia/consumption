package com.meter.consumption_meter.domain.ports.out;

import java.math.BigDecimal;
import java.util.Date;

public interface CostPort {
    public BigDecimal getCostPerKiloWattHour(Date date);
}
