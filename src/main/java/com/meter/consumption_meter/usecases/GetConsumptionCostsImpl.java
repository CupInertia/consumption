package com.meter.consumption_meter.usecases;

import com.meter.consumption_meter.domain.Consumption;
import com.meter.consumption_meter.domain.MeteringPoint;
import com.meter.consumption_meter.domain.ports.out.CostPort;
import com.meter.consumption_meter.domain.ports.out.CustomerPort;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class GetConsumptionCostsImpl implements GetConsumptionCosts {

    private final CustomerPort customerPort;
    private final CostPort costPort;

    @Override
    public List<ConsumptionCost> get(UUID customerId) {
        final var consumptionCosts = new ArrayList<ConsumptionCost>();

        final var customer = customerPort.getCustomer(customerId);
        final var meteringPoints = customer.getMeteringPoints();
        for (final var meteringPoint : meteringPoints) {
            final var consumption = meteringPoint.getConsumption();
            if (consumption.size() == 1) {
                final var reading = consumption.get(0);

                consumptionCosts.add(calculateCost(reading, meteringPoint));
            } else {
                final var sortedConsumption =
                        consumption.stream()
                                .sorted(
                                        (a, b) ->
                                                a.getTimeOfReading()
                                                        .compareTo(b.getTimeOfReading()))
                                .collect(Collectors.toList());

                final var readingIterator = sortedConsumption.iterator();

                var previousReading = readingIterator.next();
                while (readingIterator.hasNext()) {
                    final var reading = readingIterator.next();

                    if (isNextConsumptionPeriod(reading, previousReading)) {
                        consumptionCosts.add(calculateCost(previousReading, meteringPoint));
                    }

                    previousReading = reading;

                    if (!readingIterator.hasNext()) {
                        consumptionCosts.add(calculateCost(previousReading, meteringPoint));
                    }
                }
            }
        }

        return consumptionCosts;
    }

    private ConsumptionCost calculateCost(
            final Consumption reading, final MeteringPoint meteringPoint) {
        final var wattHoursConsumed = BigDecimal.valueOf(reading.getWattHours());
        return ConsumptionCost.builder()
                .cost(
                        wattHoursConsumed.multiply(
                                costPort.getCostPerKiloWattHourInCents(reading.getTimeOfReading())))
                .kiloWattHoursConsumed(wattHoursConsumed.divide(BigDecimal.valueOf(1000)))
                .meteringPoint(meteringPoint)
                .timestamp(reading.getTimeOfReading())
                .build();
    }

    private boolean isNextConsumptionPeriod(
            final Consumption readingA, final Consumption readingB) {
        return readingA.getTimeOfReading().getMonth() != readingB.getTimeOfReading().getMonth();
    }
}
