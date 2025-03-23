package com.passivehouse.PassiveHouse.handlers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.passivehouse.PassiveHouse.models.SensorMeasurement;
import com.passivehouse.PassiveHouse.repositories.SensorMeasurementsRepository;
import com.passivehouse.PassiveHouse.services.SensorMeasurementsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.*;
import org.springframework.web.socket.handler.TextWebSocketHandler;

@Component
public class SensorDataWebSocketHandler extends TextWebSocketHandler {

    @Autowired
    private ObjectMapper objectMapper = new ObjectMapper();

    @Autowired
    private final SensorMeasurementsService sensorMeasurementsService;

    @Autowired
    public SensorDataWebSocketHandler(SensorMeasurementsService sensorMeasurementsService){
        this.sensorMeasurementsService = sensorMeasurementsService;
    }

//    @Autowired
//    private SensorMeasurementsRepository sensorMeasurementsRepository;

    @Override
    public void afterConnectionEstablished(WebSocketSession session) {
        System.out.println("Client connected: " + session.getId());
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        String payload = message.getPayload();
        System.out.println("Received: " + payload);
        SensorMeasurement sensorMeasurement = objectMapper.readValue(payload, SensorMeasurement.class);
//        sensorMeasurementsRepository.save(sensorMeasurement);
        System.out.println(sensorMeasurement.getId() + ";" + sensorMeasurement.getTimestamp() + ";" + sensorMeasurement.getTemperature() + ";" + sensorMeasurement.getHumidity() + ";" + sensorMeasurement.getLight1()+ ";" + sensorMeasurement.getLight2() + ";" + sensorMeasurement.getLight3()+ ";" + sensorMeasurement.getVoltage1() + ";" + sensorMeasurement.getVoltage2() + ";" + sensorMeasurement.getCurrent1() + ";" + sensorMeasurement.getCurrent2());
        sensorMeasurementsService.uploadSensorMeasurement(sensorMeasurement);
//        sensorMeasurementsService.saveSensorData(sensorMeasurement);
        // Trimite un mesaj de confirmare înapoi la client
        session.sendMessage(new TextMessage("Hello from Spring Boot! Data saved to DB."));
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) {
        System.out.println("Client disconnected: " + session.getId());
    }
}

/** !!
 * uri = "ws://192.168.0.25:8080/ws/sensors"
 */