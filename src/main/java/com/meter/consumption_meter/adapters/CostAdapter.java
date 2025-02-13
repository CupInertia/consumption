package com.meter.consumption_meter.adapters;

import com.meter.consumption_meter.domain.ports.out.CostPort;
import java.math.BigInteger;
import java.util.Date;
import org.springframework.stereotype.Component;

@Component
public class CostAdapter implements CostPort {

    @Override
    public BigInteger getCostPerKiloWattHour(Date date) {
        return BigInteger.ONE;
    }
}
