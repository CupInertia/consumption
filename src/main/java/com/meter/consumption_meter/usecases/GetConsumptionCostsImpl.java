package com.meter.consumption_meter.usecases;

import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Component;

import com.meter.consumption_meter.domain.ports.out.CostPort;
import com.meter.consumption_meter.domain.ports.out.CustomerPort;

@Component
public class GetConsumptionCostsImpl implements GetConsumptionCosts {

    private final CustomerPort customerPort;
    private final CostPort costPort;

    @Override
    public List<ConsumptionCost> get(UUID customerId) {

        return List.of();
    }
}
