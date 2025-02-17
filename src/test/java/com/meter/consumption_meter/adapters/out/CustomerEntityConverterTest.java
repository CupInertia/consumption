package com.meter.consumption_meter.adapters.out;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;
import org.junit.Test;

public class CustomerEntityConverterTest {

    private final CustomerEntityConverter converter = new CustomerEntityConverter();

    @Test
    public void conversCustomerToItsEntity() {
        // given
        final var customerID = UUID.randomUUID();
        final var firstName = "First name";
        final var lastName = "Last name";

        final var consumption = new ConsumptionEntity();
        consumption.setId(UUID.randomUUID().toString());
        consumption.setTimestamp(OffsetDateTime.now());
        consumption.setWattHours(100);

        final var meteringPoint = new MeteringPointEntity();
        meteringPoint.setAddress("An address");
        meteringPoint.setId(UUID.randomUUID().toString());
        meteringPoint.setConsumption(List.of(consumption));

        final var customerEntity = new CustomerEntity();
        customerEntity.setID(customerID.toString());
        customerEntity.setFirstName(firstName);
        customerEntity.setLastName(lastName);
        customerEntity.setPassword("secret");
        customerEntity.setUsername("Username");
        customerEntity.setMeteringPoints(List.of(meteringPoint));

        // when
        final var customer = converter.convert(customerEntity);

        // then
        assertThat(customer.getID(), is(customerID));
        assertThat(customer.getFirstName(), is(customerEntity.getFirstName()));
        assertThat(customer.getLastName(), is(customerEntity.getLastName()));
        assertThat(customer.getUsername(), is(customerEntity.getUsername()));
        assertThat(customer.getPassword(), is(customer.getPassword()));
        assertThat(customer.getMeteringPoints(), is(not(empty())));
    }
}
