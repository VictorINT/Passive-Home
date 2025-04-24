package com.passivehouse.PassiveHouse.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;

@Entity
public class AlarmState {

    @Id
    private Long id;

    private boolean active;

    public AlarmState() {}

    public AlarmState(Long id, boolean active) {
        this.id = id;
        this.active = active;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Boolean getActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }

}