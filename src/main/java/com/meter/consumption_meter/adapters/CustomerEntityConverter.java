package com.meter.consumption_meter.adapters;

import com.meter.consumption_meter.domain.Consumption;
import com.meter.consumption_meter.domain.Customer;
import com.meter.consumption_meter.domain.MeteringPoint;
import java.util.List;
import java.util.UUID;
import org.springframework.core.convert.converter.Converter;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;

@Component
public class CustomerEntityConverter implements Converter<CustomerEntity, Customer> {

    @Override
    @Nullable public Customer convert(final CustomerEntity source) {
        if (source == null) return null;

        final List<MeteringPoint> meteringPoints =
                source.getMeteringPoints().stream().map(this::mapMeteringPoint).toList();

        return Customer.builder()
                .ID(UUID.fromString(source.getID()))
                .firstName(source.getFirstName())
                .lastName(source.getLastName())
                .username(source.getUsername())
                .password(source.getPassword())
                .meteringPoints(meteringPoints)
                .build();
    }

    private MeteringPoint mapMeteringPoint(MeteringPointEntity meteringPoint) {
        final var consumptionReadings =
                meteringPoint.getConsumption().stream().map(this::mapConsumption).toList();

        return MeteringPoint.builder()
                .address(meteringPoint.getAddress())
                .id(UUID.fromString(meteringPoint.getId()))
                .consumption(consumptionReadings)
                .build();
    }

    private Consumption mapConsumption(ConsumptionEntity consumptionEntity) {
        return Consumption.builder()
                .timeOfReading(consumptionEntity.getTimestamp())
                .wattHours(consumptionEntity.getWattHours())
                .build();
    }
}
