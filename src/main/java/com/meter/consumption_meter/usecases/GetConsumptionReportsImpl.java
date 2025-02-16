package com.meter.consumption_meter.usecases;

import com.meter.consumption_meter.domain.Consumption;
import com.meter.consumption_meter.domain.MeteringPoint;
import com.meter.consumption_meter.domain.ports.out.CostPort;
import com.meter.consumption_meter.domain.ports.out.CustomerPort;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class GetConsumptionReportsImpl implements GetConsumptionReports {

    private final CustomerPort customerPort;
    private final CostPort costPort;

    @Override
    public List<ConsumptionReport> get(UUID customerId) {
        final var customer = customerPort.getCustomer(customerId);
        final var meteringPoints = customer.getMeteringPoints();

        var consumptionReports = new ArrayList<ConsumptionReport>();
        for (final var meteringPoint : meteringPoints) {
            final var consumptionReport =
                    ConsumptionReport.builder()
                            .meterAddress(meteringPoint.getAddress())
                            .meterID(meteringPoint.getId());

            final var consumptionCosts = new ArrayList<ConsumptionCost>();

            final var consumption =
                    meteringPoint.getConsumption().stream()
                            .sorted((a, b) -> a.getTimeOfReading().compareTo(b.getTimeOfReading()))
                            .collect(Collectors.toList());

            if (!consumption.isEmpty()) {
                final var consumptionIterator = consumption.iterator();

                Consumption previousReading = consumptionIterator.next();
                BigDecimal costThatMonth = BigDecimal.ZERO;
                BigDecimal kilowattHoursThatMonth = BigDecimal.ZERO;
                var costThatHour =
                        costPort.getPricePerKilowattWithVAT(previousReading.getTimeOfReading());

                kilowattHoursThatMonth = previousReading.getKilowattHours();
                costThatMonth =
                        costThatMonth.add(
                                costThatHour.multiply(previousReading.getKilowattHours()));

                while (consumptionIterator.hasNext()) {
                    final var reading = consumptionIterator.next();

                    if (isNextConsumptionPeriod(reading, previousReading)) {
                        consumptionCosts.add(
                                createConsumptionCost(
                                        meteringPoint,
                                        kilowattHoursThatMonth,
                                        costThatMonth,
                                        previousReading.getTimeOfReading()));

                        costThatMonth = BigDecimal.ZERO;
                        kilowattHoursThatMonth = BigDecimal.ZERO;
                    }

                    costThatHour = costPort.getPricePerKilowattWithVAT(reading.getTimeOfReading());
                    kilowattHoursThatMonth = kilowattHoursThatMonth.add(reading.getKilowattHours());
                    costThatMonth =
                            costThatMonth.add(costThatHour.multiply(reading.getKilowattHours()));
                    previousReading = reading;

                    if (!consumptionIterator.hasNext()) {
                        consumptionCosts.add(
                                createConsumptionCost(
                                        meteringPoint,
                                        kilowattHoursThatMonth,
                                        costThatMonth,
                                        previousReading.getTimeOfReading()));
                    }
                }

                consumptionReports.add(consumptionReport.costs(consumptionCosts).build());
            }
        }

        return consumptionReports;
    }

    private ConsumptionCost createConsumptionCost(
            final MeteringPoint meteringPoint,
            BigDecimal kilowattHoursThatMonth,
            BigDecimal costThatMonth,
            OffsetDateTime tallingTime) {
        return ConsumptionCost.builder()
                .timestamp(tallingTime)
                .cost(costThatMonth.divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_EVEN))
                .kilowattHoursConsumed(kilowattHoursThatMonth)
                .build();
    }

    private boolean isNextConsumptionPeriod(
            final Consumption readingA, final Consumption readingB) {
        return readingA.getTimeOfReading().getMonth() != readingB.getTimeOfReading().getMonth();
    }
}
