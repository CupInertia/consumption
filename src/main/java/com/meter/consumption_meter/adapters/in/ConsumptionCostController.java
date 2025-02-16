package com.meter.consumption_meter.adapters.in;

import com.meter.consumption_meter.domain.ports.out.CustomerPort;
import com.meter.consumption_meter.usecases.ConsumptionReport;
import com.meter.consumption_meter.usecases.GetConsumptionReports;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class ConsumptionCostController {

    private final GetConsumptionReports getConsumptionCosts;
    private final CustomerPort customerPort;

    @GetMapping("/customer/consumption")
    public List<ConsumptionReport> getConsumptionCost(@AuthenticationPrincipal UserDetails user) {
        final var customer = customerPort.getCustomer(user.getUsername());
        return getConsumptionCosts.get(customer.getID());
    }
}
