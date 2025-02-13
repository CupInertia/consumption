package com.meter.consumption_meter.adapters.out;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.sql.Timestamp;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity(name = "Consumption")
@Table(name = "consumption")
@Getter
@Setter
@NoArgsConstructor
public class ConsumptionEntity {

    @Id private String id;

    @Column(name = "metering_point_id")
    private String meteringPointId;

    private long wattHours;

    private Timestamp timestamp;
}
