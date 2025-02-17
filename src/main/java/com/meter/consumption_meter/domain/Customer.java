package com.meter.consumption_meter.domain;

import java.util.ArrayList;
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
    // username and password are probably not needed in domain
    private String username;
    private String password;

    @Builder.Default private List<MeteringPoint> meteringPoints = new ArrayList<>();
}
