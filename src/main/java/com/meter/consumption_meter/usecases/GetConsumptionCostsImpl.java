package com.meter.consumption_meter.usecases;

import com.meter.consumption_meter.domain.Consumption;
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
            if (!consumption.isEmpty()) {
                final var sortedConsumption =
                        consumption.stream()
                                .sorted(
                                        (a, b) ->
                                                a.getTimeOfReading()
                                                        .compareTo(b.getTimeOfReading()))
                                .collect(Collectors.toList());

                final var readingIterator = sortedConsumption.iterator();

                Consumption previousReading = readingIterator.next();
                BigDecimal costThatMonth = BigDecimal.ZERO;
                BigDecimal wattsThatMonth = BigDecimal.ZERO;
                var costThatHour =
                        costPort.getCostPerKiloWattHourInCents(previousReading.getTimeOfReading());

                wattsThatMonth = BigDecimal.valueOf(previousReading.getWattHours());
                costThatMonth =
                        costThatMonth.add(
                                costThatHour.multiply(
                                        BigDecimal.valueOf(previousReading.getWattHours())));

                while (readingIterator.hasNext()) {
                    final var reading = readingIterator.next();

                    if (isNextConsumptionPeriod(reading, previousReading)) {
                        consumptionCosts.add(
                                ConsumptionCost.builder()
                                        .cost(costThatMonth)
                                        .kiloWattHoursConsumed(
                                                wattsThatMonth.divide(BigDecimal.valueOf(1000)))
                                        .meteringPoint(meteringPoint)
                                        .timestamp(previousReading.getTimeOfReading())
                                        .build());

                        costThatMonth = BigDecimal.ZERO;
                        wattsThatMonth = BigDecimal.ZERO;
                    }

                    costThatHour =
                            costPort.getCostPerKiloWattHourInCents(reading.getTimeOfReading());
                    wattsThatMonth = wattsThatMonth.add(BigDecimal.valueOf(reading.getWattHours()));
                    costThatMonth =
                            costThatMonth.add(
                                    costThatHour.multiply(
                                            BigDecimal.valueOf(reading.getWattHours())));
                    previousReading = reading;

                    if (!readingIterator.hasNext()) {
                        consumptionCosts.add(
                                ConsumptionCost.builder()
                                        .cost(costThatMonth)
                                        .kiloWattHoursConsumed(
                                                wattsThatMonth.divide(BigDecimal.valueOf(1000)))
                                        .meteringPoint(meteringPoint)
                                        .timestamp(previousReading.getTimeOfReading())
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
