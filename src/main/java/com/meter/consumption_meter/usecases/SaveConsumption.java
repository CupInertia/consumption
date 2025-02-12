package com.meter.consumption_meter.usecases;

import com.meter.consumption_meter.domain.MeteringPoint;

public interface SaveConsumption {
    public void save(MeteringPoint meteringPoint, float numberOfKiloWattHours);
}
