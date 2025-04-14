package com.passivehouse.PassiveHouse.controllers;

import com.passivehouse.PassiveHouse.models.SensorMeasurement;
import com.passivehouse.PassiveHouse.services.SensorMeasurementsService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/sensors")
public class SensorMeasurementsController {

    private final Logger logger = LoggerFactory.getLogger(SensorMeasurementsController.class);

    private final SensorMeasurementsService sensorMeasurementsService;

    @Autowired
    public SensorMeasurementsController(SensorMeasurementsService sensorMeasurementsService){
        this.sensorMeasurementsService = sensorMeasurementsService;
    }

    @GetMapping("/all")
    @PreAuthorize("isAuthenticated()")
    public List<SensorMeasurement> getAllSensorMeasurements(){
        return sensorMeasurementsService.getAllSensorMeasurements();
    }

    @GetMapping("/last")
    @PreAuthorize("isAuthenticated()")
    public SensorMeasurement getLastSensorMeasurement(){
        return sensorMeasurementsService.getLastSensorMeasurement();
    }

    /**
     * ! For testing only;
     * todo: delete it before deployment or comment it(in services also).
     */
    @PostMapping("/upload")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> uploadSensorMeasurement(@RequestBody SensorMeasurement sensorMeasurement){
//        System.out.println(sensorMeasurement.getId() + ";" + sensorMeasurement.getTimestamp() + ";" + sensorMeasurement.getTemperature() + ";" + sensorMeasurement.getHumidity() + ";" + sensorMeasurement.getLight1()+ ";" + sensorMeasurement.getLight2() + ";" + sensorMeasurement.getLight3()+ ";" + sensorMeasurement.getVoltage1() + ";" + sensorMeasurement.getVoltage2() + ";" + sensorMeasurement.getCurrent1() + ";" + sensorMeasurement.getCurrent2());
        logger.info("{};{};{};{};{};{};{};{};{};{};{}", sensorMeasurement.getId(), sensorMeasurement.getTimestamp(), sensorMeasurement.getTemperature(), sensorMeasurement.getHumidity(), sensorMeasurement.getLight1(), sensorMeasurement.getLight2(), sensorMeasurement.getLight3(), sensorMeasurement.getVoltage1(), sensorMeasurement.getVoltage2(), sensorMeasurement.getCurrent1(), sensorMeasurement.getCurrent2());
        SensorMeasurement uploadedSM = sensorMeasurementsService.uploadSensorMeasurement(sensorMeasurement);
        return ResponseEntity.status(HttpStatus.CREATED).body(uploadedSM);
    }
}
