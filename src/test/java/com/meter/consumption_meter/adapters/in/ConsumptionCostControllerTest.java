package com.meter.consumption_meter.adapters.in;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.meter.consumption_meter.domain.Customer;
import com.meter.consumption_meter.domain.ports.out.CustomerPort;
import com.meter.consumption_meter.usecases.ConsumptionCost;
import com.meter.consumption_meter.usecases.ConsumptionReport;
import com.meter.consumption_meter.usecases.GetConsumptionReports;
import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

@RunWith(SpringRunner.class)
@WebMvcTest(ConsumptionCostController.class)
public class ConsumptionCostControllerTest {

    @Autowired private MockMvc mockMvc;

    @MockitoBean private GetConsumptionReports getConsumptionReports;

    @MockitoBean private CustomerPort customerPort;

    @Autowired private ObjectMapper objectMapper;

    private static final String USER = "bobby";

    @Test
    @WithMockUser(USER)
    public void returnsConsumtionReportsForCurrentUser() throws Exception {
        // given
        final var customerID = UUID.randomUUID();
        when(customerPort.getCustomer(USER)).thenReturn(Customer.builder().ID(customerID).build());

        final var consumptionReports =
                List.of(
                        ConsumptionReport.builder()
                                .costs(
                                        List.of(
                                                ConsumptionCost.builder()
                                                        .kilowattHoursConsumed(BigDecimal.TEN)
                                                        .timestamp(OffsetDateTime.now())
                                                        .cost(BigDecimal.ONE)
                                                        .build()))
                                .build());
        when(getConsumptionReports.get(customerID)).thenReturn(consumptionReports);

        // when
        final var mediaType = MediaType.parseMediaType("application/json;charset=UTF-8");
        mockMvc.perform(
                        get("/customer/consumption")
                                // then
                                .accept(mediaType))
                .andExpect(status().isOk())
                .andExpect(content().contentType(mediaType))
                .andExpect(content().json(objectMapper.writeValueAsString(consumptionReports)));
    }

    @Test
    public void forbidsAccessToUnauthorized() throws Exception {
        mockMvc.perform(get("/customer/consumption")).andExpect(status().is(401));
    }
}
