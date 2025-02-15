package com.meter.consumption_meter.usecases;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.when;

import com.meter.consumption_meter.domain.Consumption;
import com.meter.consumption_meter.domain.Customer;
import com.meter.consumption_meter.domain.MeteringPoint;
import com.meter.consumption_meter.domain.ports.out.CostPort;
import com.meter.consumption_meter.domain.ports.out.CustomerPort;
import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
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

    @Mock private CostPort costPort;

    @InjectMocks private GetConsumptionCostsImpl getConsumptionCosts;

    @Test
    public void calculatesCostBasedOnConsumptionAndPrice() {
        // given
        final var customerId = UUID.randomUUID();

        final var calendar = OffsetDateTime.of(2025, 1, 1, 1, 1, 59, 0, ZoneOffset.ofHours(2));
        final var consumptionForJanuaryA =
                Consumption.builder().timeOfReading(calendar).wattHours(1).build();

        final var consumptionForJanuaryB =
                Consumption.builder().timeOfReading(calendar.plusHours(1)).wattHours(2).build();

        final var consumptionForFebruary =
                Consumption.builder().timeOfReading(calendar.plusMonths(1)).wattHours(10).build();

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
        when(costPort.getPricePerKiloWattWithVAT(consumptionForJanuaryA.getTimeOfReading()))
                .thenReturn(BigDecimal.valueOf(300));
        when(costPort.getPricePerKiloWattWithVAT(consumptionForJanuaryB.getTimeOfReading()))
                .thenReturn(BigDecimal.valueOf(400));
        when(costPort.getPricePerKiloWattWithVAT(consumptionForFebruary.getTimeOfReading()))
                .thenReturn(BigDecimal.valueOf(200));

        // when
        final var costs = getConsumptionCosts.get(customerId);

        // then
        assertThat(costs, hasSize(2));

        final var januaryCosts = costs.get(0);
        assertThat(januaryCosts.getMeteringPoint(), is(meteringPoint));
        assertThat(januaryCosts.getCost(), is(BigDecimal.valueOf(11)));
        assertThat(januaryCosts.getKiloWattHoursConsumed(), is(BigDecimal.valueOf(0.003)));
        assertThat(januaryCosts.getTimestamp(), is(consumptionForJanuaryB.getTimeOfReading()));

        final var februaryCosts = costs.get(1);
        assertThat(februaryCosts.getMeteringPoint(), is(meteringPoint));
        assertThat(februaryCosts.getCost(), is(BigDecimal.valueOf(20)));
        assertThat(februaryCosts.getKiloWattHoursConsumed(), is(BigDecimal.valueOf(0.01)));
        assertThat(februaryCosts.getTimestamp(), is(consumptionForFebruary.getTimeOfReading()));
    }
}
