package com.meter.consumption_meter.adapters.out;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import org.springframework.stereotype.Component;

@Component
public class InMemoryPriceCache implements PriceCache {

    @Override
    public BigDecimal cache(OffsetDateTime date, BigDecimal price) {
        return price;
    }
}
