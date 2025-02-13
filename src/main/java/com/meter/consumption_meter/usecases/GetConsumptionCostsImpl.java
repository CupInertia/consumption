package com.meter.consumption_meter.usecases;

import com.meter.consumption_meter.domain.Consumption;
import com.meter.consumption_meter.domain.ports.out.CostPort;
import com.meter.consumption_meter.domain.ports.out.CustomerPort;
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

                consumptionCosts.add(
                        ConsumptionCost.builder()
                                .cost(
                                        reading.getNumberOfKiloWattHours()
                                                .multiply(
                                                        costPort.getCostPerKiloWattHour(
                                                                reading.getTimeOfReading())))
                                .meteringPoint(meteringPoint)
                                .timestamp(reading.getTimeOfReading())
                                .build());
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
                var previousNumberOfKiloWattHours = previousReading.getNumberOfKiloWattHours();
                var previousTimeOfReading = previousReading.getTimeOfReading();
                while (readingIterator.hasNext()) {
                    final var reading = readingIterator.next();

                    if (isNextConsumptionPeriod(reading, previousReading)) {
                        consumptionCosts.add(
                                ConsumptionCost.builder()
                                        .cost(
                                                previousNumberOfKiloWattHours.multiply(
                                                        costPort.getCostPerKiloWattHour(
                                                                previousTimeOfReading)))
                                        .numberOfKiloWattHours(previousNumberOfKiloWattHours)
                                        .meteringPoint(meteringPoint)
                                        .timestamp(previousTimeOfReading)
                                        .build());
                    }

                    previousReading = reading;
                    previousNumberOfKiloWattHours = reading.getNumberOfKiloWattHours();
                    previousTimeOfReading = reading.getTimeOfReading();

                    if (!readingIterator.hasNext()) {
                        consumptionCosts.add(
                                ConsumptionCost.builder()
                                        .cost(
                                                previousNumberOfKiloWattHours.multiply(
                                                        costPort.getCostPerKiloWattHour(
                                                                previousReading
                                                                        .getTimeOfReading())))
                                        .numberOfKiloWattHours(previousNumberOfKiloWattHours)
                                        .meteringPoint(meteringPoint)
                                        .timestamp(previousTimeOfReading)
                                        .build());
                    }
                }
            }
        }

        return consumptionCosts;
    }

    private boolean isNextConsumptionPeriod(
            final Consumption readingA, final Consumption readingB) {
        return readingA.getTimeOfReading().getMonth() != readingB.getTimeOfReading().getMonth();
    }
}
