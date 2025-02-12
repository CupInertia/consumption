package com.meter.consumption_meter.domain.ports.out;

import com.meter.consumption_meter.domain.Consumption;
import com.meter.consumption_meter.domain.Customer;
import java.util.List;

public interface ConsumptionPort {
    public List<Consumption> getConsumption(Customer customer);
}
