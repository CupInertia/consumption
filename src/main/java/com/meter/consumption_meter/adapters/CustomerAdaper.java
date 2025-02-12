package com.meter.consumption_meter.adapters;

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
    public Customer save(Customer customer) {
        final var customerEntity =
                CustomerEntity.builder()
                        .ID(customer.getID().toString())
                        .firstName(customer.getFirstName())
                        .lastName(customer.getLastName())
                        .username(customer.getUsername())
                        .password(customer.getPassword())
                        .build();

        return customerEntityConverter.convert(customerRepository.save(customerEntity));
    }

    @Override
    public Customer getCustomer(UUID customerId) {
        final var customer = customerRepository.findById(customerId.toString()).orElseThrow();
        return customerEntityConverter.convert(customer);
    }
}
