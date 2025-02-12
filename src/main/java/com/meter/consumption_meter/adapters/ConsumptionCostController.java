package com.meter.consumption_meter.adapters;

import com.meter.consumption_meter.usecases.ConsumptionCost;
import com.meter.consumption_meter.usecases.GetConsumptionCosts;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class ConsumptionCostController {

    private final GetConsumptionCosts getConsumptionCosts;

    @GetMapping("/customer/{customerId}/consumption")
    public List<ConsumptionCost> getConsumptionCost(
            @AuthenticationPrincipal UserDetails user,
            @PathVariable("customerId") String customerId,
            @PathVariable("meterId") String meterId) {
        return getConsumptionCosts.get(UUID.fromString(customerId));
    }
}
