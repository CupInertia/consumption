package com.meter.consumption_meter;

import com.meter.consumption_meter.adapters.ConsumptionEntity;
import com.meter.consumption_meter.adapters.CustomerEntity;
import com.meter.consumption_meter.adapters.CustomerRepository;
import com.meter.consumption_meter.adapters.MeteringPointEntity;
import java.sql.Date;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.UUID;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class DataSimulationConfig {

    @Bean
    public CommandLineRunner dataSimulator(
            CustomerRepository customerRepository, PasswordEncoder passwordEncoder) {

        return (args) -> {
            if (customerRepository.count() == 0) {
                final var customerA = new CustomerEntity();
                customerA.setID(createID());
                customerA.setFirstName("Billy");
                customerA.setLastName("Bonanza");
                customerA.setUsername("billy");
                customerA.setPassword(passwordEncoder.encode("bonanza"));
                customerA.addMeteringPoint(createMeteringPoint("Address A"));
                customerA.addMeteringPoint(createMeteringPoint("Address B"));

                customerRepository.save(customerA);

                final var customerB = new CustomerEntity();
                customerB.setID(createID());
                customerB.setFirstName("John");
                customerB.setLastName("Doe");
                customerB.setUsername("john");
                customerB.setPassword(passwordEncoder.encode("doe"));
                customerB.addMeteringPoint(createMeteringPoint("Address C"));
                customerB.addMeteringPoint(createMeteringPoint("Address D"));
                customerB.addMeteringPoint(createMeteringPoint("Address E"));

                customerRepository.save(customerB);
            }
        };
    }

    private MeteringPointEntity createMeteringPoint(final String address) {
        final List<ConsumptionEntity> consumptionrReadings = new ArrayList<>();
        for (int i = -12; i < 0; i++) {
            final var calendar = Calendar.getInstance();
            calendar.add(Calendar.MONTH, i);
            final var consumption = new ConsumptionEntity();
            consumption.setId(createID());
            consumption.setTimestamp(new Date(calendar.getTimeInMillis()));
            consumption.setWattHours(Double.valueOf(Math.random() * 100000).longValue());

            consumptionrReadings.add(consumption);
        }

        final var meteringPoint = new MeteringPointEntity();
        meteringPoint.setId(createID());
        meteringPoint.setAddress(address);
        consumptionrReadings.forEach(meteringPoint::addConsumption);

        return meteringPoint;
    }

    private String createID() {
        return UUID.randomUUID().toString();
    }
}
