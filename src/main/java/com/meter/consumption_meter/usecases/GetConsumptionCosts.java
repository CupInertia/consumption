package com.meter.consumption_meter.usecases;

import java.util.List;
import java.util.UUID;

public interface GetConsumptionCosts {
    public List<ConsumptionCost> get(UUID customerId);
}
