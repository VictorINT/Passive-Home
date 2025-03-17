package com.passivehouse.PassiveHouse.services;

import com.passivehouse.PassiveHouse.models.SensorMeasurement;
import com.passivehouse.PassiveHouse.repositories.SensorMeasurementsRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SensorMeasurementsService {

    private final SensorMeasurementsRepository repo;

    public SensorMeasurementsService(SensorMeasurementsRepository repo) {
        this.repo = repo;
    }

    public List<SensorMeasurement> getAllSensorMeasurements() {
        return repo.findAll();
    }
}
