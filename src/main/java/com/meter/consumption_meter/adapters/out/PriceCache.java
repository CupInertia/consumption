package com.meter.consumption_meter.adapters.out;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

public interface PriceCache {
    public BigDecimal cache(OffsetDateTime date, BigDecimal price);
}
