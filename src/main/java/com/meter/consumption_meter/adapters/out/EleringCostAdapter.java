package com.meter.consumption_meter.adapters.out;

import com.meter.consumption_meter.domain.ports.out.CostPort;
import java.math.BigDecimal;
import java.util.Date;
import org.springframework.stereotype.Component;

@Component
public class EleringCostAdapter implements CostPort {

    @Override
    public BigDecimal getCostPerKiloWattHourInCents(Date date) {
        return BigDecimal.ONE;
    }
}
