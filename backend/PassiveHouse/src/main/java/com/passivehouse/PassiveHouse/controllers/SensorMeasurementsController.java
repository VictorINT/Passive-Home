package com.passivehouse.PassiveHouse.controllers;

import com.passivehouse.PassiveHouse.models.SensorMeasurement;
import com.passivehouse.PassiveHouse.services.SensorMeasurementsService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/sensors")
public class SensorMeasurementsController {

    SensorMeasurementsService sensorMeasurementsService;

    public SensorMeasurementsController(SensorMeasurementsService sensorMeasurementsService){
        this.sensorMeasurementsService = sensorMeasurementsService;
    }

    @GetMapping("/all")
    public List<SensorMeasurement> getAllSensorMeasurements(){
        return sensorMeasurementsService.getAllSensorMeasurements();
    }
    @GetMapping("/last")
    public SensorMeasurement getLastSensorMeasurement(){
        return sensorMeasurementsService.getLastSensorMeasurement();
    }
}
