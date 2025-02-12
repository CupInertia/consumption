package com.meter.consumption_meter.adapters;

import com.meter.consumption_meter.domain.Customer;
import java.util.UUID;
import org.springframework.core.convert.converter.Converter;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;

@Component
public class CustomerEntityConverter implements Converter<CustomerEntity, Customer> {

    @Override
    @Nullable public Customer convert(final CustomerEntity source) {
        if (source == null) return null;

        return Customer.builder()
                .ID(UUID.fromString(source.getID()))
                .firstName(source.getFirstName())
                .lastName(source.getLastName())
                .username(source.getUsername())
                .password(source.getPassword())
                .build();
    }
}
