package com.meter.consumption_meter.adapters.out;

import java.util.Optional;
import org.springframework.data.repository.CrudRepository;

public interface CustomerRepository extends CrudRepository<CustomerEntity, String> {
    public Optional<CustomerEntity> findByUsername(String username);
}
