package com.passivehouse.PassiveHouse.service;

import com.passivehouse.PassiveHouse.model.SensorMeasurement;
import com.passivehouse.PassiveHouse.repository.SensorMeasurementsRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SensorMeasurementsService {

    private final SensorMeasurementsRepository repo;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    public SensorMeasurementsService(SensorMeasurementsRepository repo) {
        this.repo = repo;
    }

    public void saveSensorData(SensorMeasurement sensorMeasurement) {
        String sql = "INSERT INTO sensor_measurements (timestamp, temperature, humidity, light1, light2, light3, voltage1, voltage2, current1, current2) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        jdbcTemplate.update(sql,
                sensorMeasurement.getTimestamp(),
                sensorMeasurement.getTemperature(),
                sensorMeasurement.getHumidity(),
                sensorMeasurement.getLight1(),
                sensorMeasurement.getLight2(),
                sensorMeasurement.getLight3(),
                sensorMeasurement.getVoltage1(),
                sensorMeasurement.getVoltage2(),
                sensorMeasurement.getCurrent1(),
                sensorMeasurement.getCurrent2());
    }

    @Transactional
    public SensorMeasurement uploadSensorMeasurement(SensorMeasurement sm) {
        return repo.save(sm);
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
