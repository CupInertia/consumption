package com.meter.consumption_meter.adapters.out;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity(name = "MeteringPoint")
@Table(name = "metering_point")
@Getter
@Setter
@NoArgsConstructor
public class MeteringPointEntity {

    @Id private String id;

    @NotNull @Column(name = "customer_id")
    private String customerId;

    @NotNull private String address;

    @JoinColumn(name = "metering_point_id")
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ConsumptionEntity> consumption = new ArrayList<>();

    public void addConsumption(final ConsumptionEntity consumptionReading) {
        consumption.add(consumptionReading);
        consumptionReading.setMeteringPointId(id);
    }
}
