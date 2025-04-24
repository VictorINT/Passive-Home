package com.passivehouse.PassiveHouse.controller;

import com.passivehouse.PassiveHouse.model.SensorMeasurement;
import com.passivehouse.PassiveHouse.service.SensorMeasurementsService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
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
}
