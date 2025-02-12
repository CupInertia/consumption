package com.meter.consumption_meter.domain;

import java.util.List;
import java.util.UUID;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

@Getter
@Builder
@ToString
@EqualsAndHashCode
public class MeteringPoint {
    private UUID id;
    private String address;

    private List<Consumption> consumption;
}
