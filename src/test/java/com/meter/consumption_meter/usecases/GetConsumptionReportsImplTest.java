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
public class GetConsumptionReportsImplTest {

    @Mock private CustomerPort customerPort;

    @Mock private CostPort costPort;

    @InjectMocks private GetConsumptionReportsImpl getConsumptionReport;

    @Test
    public void compilesConsumptionCostsIntoMonthlyReports() {
        // given
        final var customerId = UUID.randomUUID();

        final var calendar = OffsetDateTime.of(2025, 1, 1, 1, 1, 59, 0, ZoneOffset.ofHours(2));
        final var consumptionForJanuaryA =
                Consumption.builder().timeOfReading(calendar).wattHours(1000).build();

        final var consumptionForJanuaryB =
                Consumption.builder().timeOfReading(calendar.plusHours(1)).wattHours(2000).build();

        final var consumptionForFebruary =
                Consumption.builder()
                        .timeOfReading(calendar.plusMonths(1))
                        .wattHours(10000)
                        .build();

        final var meteringPoint =
                MeteringPoint.builder()
                        .address("An address")
                        .id(UUID.randomUUID())
                        .consumption(
                                List.of(
                                        consumptionForJanuaryA,
                                        consumptionForJanuaryB,
                                        consumptionForFebruary))
                        .build();

        final var customer =
                Customer.builder().ID(customerId).meteringPoints(List.of(meteringPoint)).build();

        when(customerPort.getCustomer(customerId)).thenReturn(customer);
        when(costPort.getPricePerKilowattWithVAT(consumptionForJanuaryA.getTimeOfReading()))
                .thenReturn(BigDecimal.valueOf(300));
        when(costPort.getPricePerKilowattWithVAT(consumptionForJanuaryB.getTimeOfReading()))
                .thenReturn(BigDecimal.valueOf(400));
        when(costPort.getPricePerKilowattWithVAT(consumptionForFebruary.getTimeOfReading()))
                .thenReturn(BigDecimal.valueOf(200));

        // when
        final var reports = getConsumptionReport.get(customerId);

        // then
        assertThat(reports, hasSize(1));

        final var firstReport = reports.get(0);
        final var costs = firstReport.getCosts();
        assertThat(costs, hasSize(2));

        final var januaryCosts = costs.get(0);
        assertThat(firstReport.getMeterID(), is(meteringPoint.getId()));
        assertThat(firstReport.getMeterAddress(), is(meteringPoint.getAddress()));

        assertThat(januaryCosts.getCost(), is(BigDecimal.valueOf(11).setScale(2)));
        assertThat(januaryCosts.getKilowattHoursConsumed(), is(BigDecimal.valueOf(3)));
        assertThat(januaryCosts.getTimestamp(), is(consumptionForJanuaryB.getTimeOfReading()));

        final var februaryCosts = costs.get(1);
        assertThat(februaryCosts.getCost(), is(BigDecimal.valueOf(20).setScale(2)));
        assertThat(februaryCosts.getKilowattHoursConsumed(), is(BigDecimal.valueOf(10)));
        assertThat(februaryCosts.getTimestamp(), is(consumptionForFebruary.getTimeOfReading()));
    }
}
