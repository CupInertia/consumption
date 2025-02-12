package com.meter.consumption_meter.domain;

import java.util.List;
import java.util.UUID;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class Customer {
    private UUID ID;
    private String firstName;
    private String lastName;
    private String username;
    private String password;

    private List<MeteringPoint> meteringPoints;
}
