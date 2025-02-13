package com.meter.consumption_meter.usecases;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.when;

import com.meter.consumption_meter.domain.Consumption;
import com.meter.consumption_meter.domain.Customer;
import com.meter.consumption_meter.domain.MeteringPoint;
import com.meter.consumption_meter.domain.ports.out.ConsumptionPort;
import com.meter.consumption_meter.domain.ports.out.CostPort;
import com.meter.consumption_meter.domain.ports.out.CustomerPort;
import java.math.BigInteger;
import java.util.Calendar;
import java.util.List;
import java.util.UUID;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class GetConsumptionCostsImplTest {

    @Mock private CustomerPort customerPort;

    @Mock private ConsumptionPort consumptionPort;

    @Mock private CostPort costPort;

    @InjectMocks private GetConsumptionCostsImpl getConsumptionCosts;

    @Test
    public void calculatesCostBasedOnConsumptionAndPrice() {
        // given
        final var customerId = UUID.randomUUID();

        final var calendar = Calendar.getInstance();
        calendar.set(2025, 1, 1);
        final var consumptionForJanuaryA =
                Consumption.builder()
                        .timeOfReading(calendar.getTime())
                        .numberOfKiloWattHours(BigInteger.ONE)
                        .build();

        calendar.add(Calendar.DAY_OF_MONTH, 1);
        final var consumptionForJanuaryB =
                Consumption.builder()
                        .timeOfReading(calendar.getTime())
                        .numberOfKiloWattHours(
                                consumptionForJanuaryA
                                        .getNumberOfKiloWattHours()
                                        .add(BigInteger.ONE))
                        .build();

        calendar.add(Calendar.MONTH, 1);
        final var consumptionForFebruary =
                Consumption.builder()
                        .timeOfReading(calendar.getTime())
                        .numberOfKiloWattHours(BigInteger.TEN)
                        .build();

        final var meteringPoint =
                MeteringPoint.builder()
                        .consumption(
                                List.of(
                                        consumptionForJanuaryA,
                                        consumptionForJanuaryB,
                                        consumptionForFebruary))
                        .build();

        final var customer =
                Customer.builder().ID(customerId).meteringPoints(List.of(meteringPoint)).build();

        when(customerPort.getCustomer(customerId)).thenReturn(customer);
        when(costPort.getCostPerKiloWattHour(consumptionForJanuaryB.getTimeOfReading()))
                .thenReturn(BigInteger.valueOf(3));
        when(costPort.getCostPerKiloWattHour(consumptionForFebruary.getTimeOfReading()))
                .thenReturn(BigInteger.TWO);

        // when
        final var costs = getConsumptionCosts.get(customerId);

        // then
        assertThat(costs, hasSize(2));

        final var firstCost = costs.get(0);
        assertThat(firstCost.getMeteringPoint(), is(meteringPoint));
        assertThat(firstCost.getCost(), is(BigInteger.valueOf(6)));
        assertThat(
                firstCost.getNumberOfKiloWattHours(),
                is(consumptionForJanuaryB.getNumberOfKiloWattHours()));
        assertThat(firstCost.getTimestamp(), is(consumptionForJanuaryB.getTimeOfReading()));

        final var secondCost = costs.get(1);
        assertThat(secondCost.getMeteringPoint(), is(meteringPoint));
        assertThat(secondCost.getCost(), is(BigInteger.valueOf(20)));
        assertThat(
                secondCost.getNumberOfKiloWattHours(),
                is(consumptionForFebruary.getNumberOfKiloWattHours()));
        assertThat(secondCost.getTimestamp(), is(consumptionForFebruary.getTimeOfReading()));
    }
}
