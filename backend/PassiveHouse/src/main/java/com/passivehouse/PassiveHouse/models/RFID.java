package com.passivehouse.PassiveHouse.models;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

@Entity
public class RFID {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String rfid_tag;

    public RFID() {}

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getRfid_tag() {
        return rfid_tag;
    }

    public void setRfid_tag(String rfid_tag) {
        this.rfid_tag = rfid_tag;
    }
}
