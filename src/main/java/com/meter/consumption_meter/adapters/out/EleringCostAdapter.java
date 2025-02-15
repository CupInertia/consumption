package com.meter.consumption_meter.adapters.out;

import com.meter.consumption_meter.domain.ports.out.CostPort;
import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.util.Arrays;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
@RequiredArgsConstructor
public class EleringCostAdapter implements CostPort {

    private final RestTemplate restTemplate;

    private final PriceCache priceCache;

    public static record EleringCost(
            OffsetDateTime fromDateTime,
            OffsetDateTime toDateTime,
            BigDecimal centsPerKwhWithVat) {}

    @Override
    public BigDecimal getPricePerKiloWattWithVAT(final OffsetDateTime requestDate) {
        final var requestDateUTC = requestDate.atZoneSameInstant(ZoneId.of("UTC"));

        final var prices =
                restTemplate.getForObject(
                        "https://estfeed.elering.ee/api/public/v1/energy-price/electricity"
                            + "?startDateTime={startDateTime}&endDateTime={endDate}&resolution=one_hour",
                        EleringCost[].class,
                        requestDateUTC.toInstant(),
                        requestDateUTC.plusMonths(1).toInstant());

        final var price =
                Arrays.stream(prices)
                        .filter(
                                e -> {
                                    final var responseDateUTC =
                                            e.toDateTime().atZoneSameInstant(ZoneId.of("UTC"));

                                    return responseDateUTC.getYear() == requestDateUTC.getYear()
                                            && responseDateUTC.getMonth()
                                                    == requestDateUTC.getMonth()
                                            && responseDateUTC.getDayOfMonth()
                                                    == requestDateUTC.getDayOfMonth()
                                            && responseDateUTC.getHour()
                                                    == requestDateUTC.getHour();
                                })
                        .findFirst()
                        .orElseThrow();

        return priceCache.cache(requestDate, price.centsPerKwhWithVat());
    }
}
