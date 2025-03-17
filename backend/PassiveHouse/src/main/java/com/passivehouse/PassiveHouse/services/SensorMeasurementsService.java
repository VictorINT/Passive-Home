package com.passivehouse.PassiveHouse.services;

import com.passivehouse.PassiveHouse.models.SensorMeasurement;
import com.passivehouse.PassiveHouse.repositories.SensorMeasurementsRepository;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.Query;
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

//    public SensorMeasurement getLastSensorMeasurement() {
//        List<SensorMeasurement> results = repo.findLatestSorted(Sort.by(Sort.Direction.DESC, "timestamp"));
//        return results.isEmpty() ? null : results.get(0);
//    }
    public SensorMeasurement getLastSensorMeasurement() {
        return repo.findTopByOrderByTimestampDesc();
    }

}
