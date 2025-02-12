package com.meter.consumption_meter.domain.ports.out;

import java.util.Date;

public interface CostPort {
    public float getCostPerKiloWattHour(Date date);
}
