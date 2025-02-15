package com.meter.consumption_meter.adapters.out;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.Optional;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.client.RestClientTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.rules.SpringClassRule;
import org.springframework.test.context.junit4.rules.SpringMethodRule;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestTemplate;

@RunWith(MockitoJUnitRunner.class)
@RestClientTest
@Import(EleringConfig.class)
public class EleringCostAdapterTest {

    @ClassRule public static final SpringClassRule springClassRule = new SpringClassRule();

    @Rule public final SpringMethodRule springMethodRule = new SpringMethodRule();

    @Mock private PriceCache priceCache;

    private EleringCostAdapter eleringCostAdapter;

    private MockRestServiceServer server;

    @Autowired private RestTemplate restTemplate;

    @Before
    public void setUp() {
        server = MockRestServiceServer.bindTo(restTemplate).build();

        eleringCostAdapter = new EleringCostAdapter(restTemplate, priceCache);
    }

    @Test
    public void returnsPrices() {
        // given
        final var requestDate = OffsetDateTime.of(2024, 2, 12, 0, 0, 0, 0, ZoneOffset.ofHours(2));

        final var response =
                """
                [{
                  "centsPerKwh": 6.401,
                  "centsPerKwhWithVat": 7.80922,
                  "eurPerMwh": 64.01,
                  "eurPerMwhWithVat": 78.0922,
                  "fromDateTime": "2024-02-12T00:00:00+02:00",
                  "toDateTime": "2024-02-12T00:59:59.999999999+02:00"
                },
                {
                  "centsPerKwh": 6.5,
                  "centsPerKwhWithVat": 7.93,
                  "eurPerMwh": 65,
                  "eurPerMwhWithVat": 79.3,
                  "fromDateTime": "2024-03-12T01:00:00+02:00",
                  "toDateTime": "2024-03-12T01:59:59.999999999+02:00"
                }]
                """;

        final var expectedURI =
                "https://estfeed.elering.ee/api/public/v1/energy-price/electricity"
                    + "?startDateTime=2024-02-11T22:00:00Z&endDateTime=2024-05-11T22:00:00Z&resolution=one_hour";

        server.expect(requestTo(expectedURI))
                .andRespond(withSuccess(response, MediaType.APPLICATION_JSON));

        final var expectedPrice = BigDecimal.valueOf(7.80922);

        when(priceCache.get(requestDate.toInstant()))
                .thenReturn(Optional.empty())
                .thenReturn(Optional.of(expectedPrice));

        // when
        final var price = eleringCostAdapter.getPricePerKiloWattWithVAT(requestDate);

        // then
        verify(priceCache).cache(requestDate.toInstant(), expectedPrice);
        verify(priceCache)
                .cache(
                        OffsetDateTime.of(2024, 3, 12, 1, 0, 0, 0, ZoneOffset.ofHours(2))
                                .toInstant(),
                        BigDecimal.valueOf(7.93));

        assertThat(price, is(expectedPrice));
    }
}
