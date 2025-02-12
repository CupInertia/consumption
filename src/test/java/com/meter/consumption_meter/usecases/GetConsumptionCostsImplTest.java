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
        final var consumptionForJanuary =
                Consumption.builder()
                        .timeOfReading(calendar.getTime())
                        .numberOfKiloWattHours(1)
                        .build();
        calendar.add(Calendar.MONDAY, 1);
        final var consumptionForFebruary =
                Consumption.builder()
                        .timeOfReading(calendar.getTime())
                        .numberOfKiloWattHours(3)
                        .build();
        final var meteringPoint =
                MeteringPoint.builder()
                        .consumption(List.of(consumptionForJanuary, consumptionForFebruary))
                        .build();
        final var customer =
                Customer.builder().ID(customerId).meteringPoints(List.of(meteringPoint)).build();

        when(customerPort.getCustomer(customerId)).thenReturn(customer);
        when(costPort.getCostPerKiloWattHour(consumptionForJanuary.getTimeOfReading()))
                .thenReturn(3f);
        when(costPort.getCostPerKiloWattHour(consumptionForFebruary.getTimeOfReading()))
                .thenReturn(2f);

        // when
        final var costs = getConsumptionCosts.get(customerId);

        // then
        assertThat(costs, hasSize(1));

        final var firstCost = costs.get(0);
        assertThat(firstCost.getMeteringPoint(), is(meteringPoint));
        assertThat(firstCost.getCost(), is(3));
        assertThat(firstCost.getNumberOfKiloWattHours(), is(1));
        assertThat(firstCost.getTimestamp(), is(consumptionForJanuary.getTimeOfReading()));

        final var secondCost = costs.get(1);
        assertThat(secondCost.getMeteringPoint(), is(meteringPoint));
        assertThat(secondCost.getCost(), is(6));
        assertThat(secondCost.getNumberOfKiloWattHours(), is(2));
        assertThat(secondCost.getTimestamp(), is(consumptionForFebruary.getTimeOfReading()));
    }
}
