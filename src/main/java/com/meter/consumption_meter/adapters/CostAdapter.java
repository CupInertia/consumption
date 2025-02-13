package com.meter.consumption_meter.adapters;

import com.meter.consumption_meter.domain.ports.out.CostPort;
import java.math.BigDecimal;
import java.util.Date;
import org.springframework.stereotype.Component;

@Component
public class CostAdapter implements CostPort {

    @Override
    public BigDecimal getCostPerKiloWattHour(Date date) {
        return BigDecimal.ONE;
    }
}
