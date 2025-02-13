package com.meter.consumption_meter.domain.ports.out;

import java.math.BigInteger;
import java.util.Date;

public interface CostPort {
    public BigInteger getCostPerKiloWattHour(Date date);
}
