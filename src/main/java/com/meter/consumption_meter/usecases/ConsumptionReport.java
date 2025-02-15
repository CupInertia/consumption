package com.meter.consumption_meter.usecases;

import java.util.List;
import java.util.UUID;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ConsumptionReport {
    private String meterAddress;
    private UUID meterID;

    private List<ConsumptionCost> costs;
}
