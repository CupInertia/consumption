package com.meter.consumption_meter;

import org.springframework.boot.SpringApplication;

public class TestConsumptionMeterApplication {

    public static void main(String[] args) {
        SpringApplication.from(ConsumptionMeterApplication::main)
                .with(TestcontainersConfiguration.class)
                .run(args);
    }
}
