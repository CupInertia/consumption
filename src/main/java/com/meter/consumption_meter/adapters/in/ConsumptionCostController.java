package com.meter.consumption_meter.adapters.in;

import com.meter.consumption_meter.usecases.ConsumptionReport;
import com.meter.consumption_meter.usecases.GetConsumptionReports;
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

    private final GetConsumptionReports getConsumptionCosts;

    @GetMapping("/customer/{customerId}/consumption")
    public List<ConsumptionReport> getConsumptionCost(
            @AuthenticationPrincipal UserDetails user,
            @PathVariable("customerId") String customerId) {
        return getConsumptionCosts.get(UUID.fromString(customerId));
    }
}
