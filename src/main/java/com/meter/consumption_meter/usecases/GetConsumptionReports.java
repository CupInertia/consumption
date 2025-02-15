package com.meter.consumption_meter.usecases;

import java.util.List;
import java.util.UUID;

public interface GetConsumptionReports {
    public List<ConsumptionReport> get(UUID customerId);
}
