package com.meter.consumption_meter.adapters.out;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import org.springframework.stereotype.Component;

@Component
public class InMemoryPriceCache implements PriceCache {

    private Map<Instant, BigDecimal> cache = new HashMap<>();

    @Override
    public void cache(Instant date, BigDecimal price) {
        cache.put(date, price);
    }

    @Override
    public Optional<BigDecimal> get(Instant date) {
        return cache.containsKey(date) ? Optional.of(cache.get(date)) : Optional.empty();
    }
}
