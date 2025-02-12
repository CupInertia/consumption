package com.meter.consumption_meter.adapters;

import org.springframework.data.repository.CrudRepository;

public interface CustomerRepository extends CrudRepository<CustomerEntity, String> {
    public CustomerEntity findByUsername(String username);
}
