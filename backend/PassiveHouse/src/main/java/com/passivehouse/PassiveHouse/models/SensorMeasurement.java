package com.passivehouse.PassiveHouse.models;

import jakarta.persistence.*;
import java.util.Date;

@Entity
@Table(name = "sensor_measurements")
public class SensorMeasurement {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(nullable = false, updatable = false)
    private Date timestamp;

    @Column(nullable = false)
    private double temperature;

    @Column(nullable = false)
    private double humidity;

    @Column(nullable = false)
    private double light1;

    @Column(nullable = false)
    private double light2;

    @Column(nullable = false)
    private double light3;

    @Column(nullable = false)
    private double voltage1;

    @Column(nullable = false)
    private double voltage2;

    @Column(nullable = false)
    private double current1;

    @Column(nullable = false)
    private double current2;

    public SensorMeasurement() {
        this.timestamp = new Date();
    }

    public SensorMeasurement(double temperature, double humidity, double light1, double light2, double light3,
                             double voltage1, double voltage2, double current1, double current2) {
        this.timestamp = new Date();
        this.temperature = temperature;
        this.humidity = humidity;
        this.light1 = light1;
        this.light2 = light2;
        this.light3 = light3;
        this.voltage1 = voltage1;
        this.voltage2 = voltage2;
        this.current1 = current1;
        this.current2 = current2;
    }

    public Long getId() {
        return id;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public double getTemperature() {
        return temperature;
    }

    public void setTemperature(double temperature) {
        this.temperature = temperature;
    }

    public double getHumidity() {
        return humidity;
    }

    public void setHumidity(double humidity) {
        this.humidity = humidity;
    }

    public double getLight1() {
        return light1;
    }

    public void setLight1(double light1) {
        this.light1 = light1;
    }

    public double getLight2() {
        return light2;
    }

    public void setLight2(double light2) {
        this.light2 = light2;
    }

    public double getLight3() {
        return light3;
    }

    public void setLight3(double light3) {
        this.light3 = light3;
    }

    public double getVoltage1() {
        return voltage1;
    }

    public void setVoltage1(double voltage1) {
        this.voltage1 = voltage1;
    }

    public double getVoltage2() {
        return voltage2;
    }

    public void setVoltage2(double voltage2) {
        this.voltage2 = voltage2;
    }

    public double getCurrent1() {
        return current1;
    }

    public void setCurrent1(double current1) {
        this.current1 = current1;
    }

    public double getCurrent2() {
        return current2;
    }

    public void setCurrent2(double current2) {
        this.current2 = current2;
    }
}
