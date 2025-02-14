package com.meter.consumption_meter.adapters.out;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
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

    private Instant timestamp;

    private int timestampOffset;

    public void setTimestamp(final OffsetDateTime timestamp) {
        this.timestamp = timestamp.toInstant();
        this.timestampOffset = timestamp.getOffset().getTotalSeconds();
    }

    public OffsetDateTime getTimestampWithOffset() {
        return this.timestamp.atOffset(ZoneOffset.ofTotalSeconds(timestampOffset));
    }
}
