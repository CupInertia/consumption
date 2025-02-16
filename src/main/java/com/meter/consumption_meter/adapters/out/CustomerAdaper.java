package com.meter.consumption_meter.adapters.out;

import com.meter.consumption_meter.domain.Customer;
import com.meter.consumption_meter.domain.ports.out.CustomerPort;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CustomerAdaper implements CustomerPort {

    private final CustomerRepository customerRepository;
    private final CustomerEntityConverter customerEntityConverter;

    @Override
    public Customer getCustomer(UUID customerId) {
        final var customer = customerRepository.findById(customerId.toString()).orElseThrow();
        return customerEntityConverter.convert(customer);
    }

    @Override
    public Customer getCustomer(String userName) {
        final var customer = customerRepository.findByUsername(userName).orElseThrow();
        return customerEntityConverter.convert(customer);
    }
}
