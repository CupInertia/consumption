package com.meter.consumption_meter.domain.ports.out;

import com.meter.consumption_meter.domain.Customer;
import java.util.UUID;

public interface CustomerPort {
    public Customer getCustomer(UUID customerId);

    public Customer getCustomer(String userName);
}
