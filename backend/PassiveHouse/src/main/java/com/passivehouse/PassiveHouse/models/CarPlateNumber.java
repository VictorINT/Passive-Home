package com.passivehouse.PassiveHouse.models;

import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;

public class CarPlateNumber {
    @Id
    @GeneratedValue
    private Long Id;

    private String CarPlateNumber;

    public CarPlateNumber(){}

    public Long getId() {
        return Id;
    }

    public void setId(Long id) {
        this.Id = id;
    }

    public String getCarPlateNumber() {
        return CarPlateNumber;
    }

    public void setCarPlateNumber(String CarPlateNumber) {
        this.CarPlateNumber = CarPlateNumber;
    }
}
