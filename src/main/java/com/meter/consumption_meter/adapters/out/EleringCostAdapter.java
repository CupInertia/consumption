package com.meter.consumption_meter.adapters.out;

import com.meter.consumption_meter.domain.ports.out.CostPort;
import java.math.BigDecimal;
import java.time.OffsetDateTime;
import org.springframework.stereotype.Component;

@Component
public class EleringCostAdapter implements CostPort {

    @Override
    public BigDecimal getCostPerKiloWattHourInCents(final OffsetDateTime date) {
        return BigDecimal.ONE;
    }
}
