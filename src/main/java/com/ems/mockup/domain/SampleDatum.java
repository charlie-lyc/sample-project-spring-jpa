package com.ems.mockup.domain;

import lombok.*;
import org.hibernate.annotations.DynamicUpdate;
import javax.persistence.*;
import java.time.LocalDateTime;

@DynamicUpdate
@AllArgsConstructor
@NoArgsConstructor
@Data
@Entity
@Table(name = "sampleData")
public class SampleDatum {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(name = "hour")
    private int hour;

    @Column
    private double pvUsage;

    @Column
    private double essUsage;

    @Column
    private double equipLight;

    @Column
    private double equipSmart;

    @Column
    private double equipAqua;

    @Column
    private double equipAir;

    @Column
    private double chargeMinutes;

    @Column
    private double chargeVolt;

    @Column
    private double chargeCurrent;

    @Column
    private double dischargeMinutes;

    @Column
    private double dischargeVolt;

    @Column
    private double dischargeCurrent;

    @Column
    private double solarSupplyRate;

    @Column
    private double electricDemandRate;

    @Column
    private double batterySupplyRate;

    @Column
    private double batteryOpRate;

    @Column
    private double solarPower;

    @Column
    private double solarOpRate;

    @Column
    private double solarDailyGen;

    @Column
    private double electricDemand;

    @Column
    private double electricConsume;

    @Column
    private double batteryDayCharge;

    @Column
    private double batteryDayDischarge;

    @Column
    private double batteryDayChargeHours;

    @Column
    private double batteryDayDischargeHours;

    @Column
    private double batteryAccCharge;

    @Column
    private double batteryAccChargeHours;

    @Column
    private double batteryAccDischarge;

    @Column
    private double batteryAccDischargeHours;

    @Column
    private double essCharge;

    /**
     * Default: (updatable = true)
     */
    @Column(updatable = false)
    private LocalDateTime createdAt;

    @Column
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

}
