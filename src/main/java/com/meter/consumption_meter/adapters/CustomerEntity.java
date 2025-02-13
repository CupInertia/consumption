package com.meter.consumption_meter.adapters;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity(name = "Customer")
@Table(name = "customer")
@Getter
@Setter
@NoArgsConstructor
public class CustomerEntity {

    @Id private String ID;

    private String firstName;
    private String lastName;
    private String username;
    private String password;

    @JoinColumn(name = "customer_id")
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    private List<MeteringPointEntity> meteringPoints = new ArrayList<>();

    public void addMeteringPoint(final MeteringPointEntity meteringPoint) {
        meteringPoints.add(meteringPoint);
        meteringPoint.setCustomerId(ID);
    }
}
