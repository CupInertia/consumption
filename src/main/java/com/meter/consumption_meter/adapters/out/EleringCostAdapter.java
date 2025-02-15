package com.meter.consumption_meter.adapters.out;

import com.meter.consumption_meter.domain.ports.out.CostPort;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.Arrays;
import java.util.Optional;
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

        return priceCache
                .get(prepareForCache(requestDate))
                .orElseGet(
                        () -> {
                            final var cacheEndDate = requestDate.plusMonths(3);

                            final var prices =
                                    restTemplate.getForObject(
                                            "https://estfeed.elering.ee/api/public/v1/energy-price/electricity"
                                                + "?startDateTime={startDateTime}&endDateTime={endDate}&resolution=one_hour",
                                            EleringCost[].class,
                                            requestDate.toInstant(),
                                            cacheEndDate.toInstant());

                            var cacheStartDate = OffsetDateTime.from(requestDate);
                            while (cacheStartDate.isBefore(cacheEndDate)) {
                                final var priceToCache = findCost(prices, cacheStartDate);

                                if (priceToCache.isPresent()) {
                                    priceCache.cache(
                                            prepareForCache(cacheStartDate),
                                            priceToCache.get().centsPerKwhWithVat);
                                }

                                cacheStartDate = cacheStartDate.plusHours(1);
                            }

                            return priceCache
                                    .get(prepareForCache(requestDate))
                                    .orElse(BigDecimal.ZERO);
                        });
    }

    private boolean compare(ZonedDateTime a, ZonedDateTime b) {
        return a.getYear() == b.getYear()
                && a.getMonth() == b.getMonth()
                && a.getDayOfMonth() == b.getDayOfMonth()
                && a.getHour() == b.getHour();
    }

    private Instant prepareForCache(OffsetDateTime offsetDateTime) {
        return offsetDateTime.withMinute(0).withSecond(0).withNano(0).toInstant();
    }

    private Optional<EleringCost> findCost(
            final EleringCost[] prices, final OffsetDateTime requestDate) {
        final var requestDateUTC = requestDate.atZoneSameInstant(ZoneOffset.UTC);
        return Arrays.stream(prices)
                .filter(
                        e -> {
                            final var responseDateUTC =
                                    e.toDateTime().atZoneSameInstant(ZoneId.of("UTC"));

                            return compare(requestDateUTC, responseDateUTC);
                        })
                .findFirst();
    }
}
