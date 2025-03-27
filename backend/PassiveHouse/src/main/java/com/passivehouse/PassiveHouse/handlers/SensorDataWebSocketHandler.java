package com.passivehouse.PassiveHouse.handlers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.passivehouse.PassiveHouse.models.SensorMeasurement;
import com.passivehouse.PassiveHouse.services.SensorMeasurementsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.*;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.IOException;
import java.util.concurrent.CopyOnWriteArrayList;

@Component
public class SensorDataWebSocketHandler extends TextWebSocketHandler {

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private final SensorMeasurementsService sensorMeasurementsService;

    private final CopyOnWriteArrayList<WebSocketSession> sessions = new CopyOnWriteArrayList<>();

    @Autowired
    public SensorDataWebSocketHandler(SensorMeasurementsService sensorMeasurementsService){
        this.sensorMeasurementsService = sensorMeasurementsService;
    }

    @Override
    public void afterConnectionEstablished(WebSocketSession session) {
        System.out.println("Client connected: " + session.getId());
        sessions.add(session);
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        String payload = message.getPayload();
        System.out.println("Received: " + payload);

        // Process as SensorMeasurement
        try {
            SensorMeasurement sensorMeasurement = objectMapper.readValue(payload, SensorMeasurement.class);
            sensorMeasurementsService.uploadSensorMeasurement(sensorMeasurement);
            System.out.println(sensorMeasurement.getId() + ";" + sensorMeasurement.getTimestamp() + ";" +
                    sensorMeasurement.getTemperature() + ";" + sensorMeasurement.getHumidity() + ";" +
                    sensorMeasurement.getLight1() + ";" + sensorMeasurement.getLight2() + ";" + sensorMeasurement.getLight3() + ";" +
                    sensorMeasurement.getVoltage1() + ";" + sensorMeasurement.getVoltage2() + ";" +
                    sensorMeasurement.getCurrent1() + ";" + sensorMeasurement.getCurrent2());
        } catch (Exception e) {
            System.err.println("Invalid SensorMeasurement JSON, treating as raw message.");
        }

        // Broadcast the received message to all WebSocket clients
        broadcast(payload);
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) {
        System.out.println("Client disconnected: " + session.getId());
        sessions.remove(session);
    }

    public void broadcast(String message) {
        for (WebSocketSession session : sessions) {
            if (session.isOpen()) {
                try {
                    session.sendMessage(new TextMessage(message));
                } catch (IOException e) {
                    System.err.println("Error sending WebSocket message: " + e.getMessage());
                }
            }
        }
    }
}
