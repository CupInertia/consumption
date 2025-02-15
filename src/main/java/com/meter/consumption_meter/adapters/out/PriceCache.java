package com.meter.consumption_meter.adapters.out;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Optional;

public interface PriceCache {
    public void cache(Instant date, BigDecimal price);

    public Optional<BigDecimal> get(Instant date);
}
