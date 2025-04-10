package com.passivehouse.PassiveHouse.handlers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.passivehouse.PassiveHouse.SSE.PirSSEController;
import com.passivehouse.PassiveHouse.models.SensorMeasurement;
import com.passivehouse.PassiveHouse.services.AlarmService;
import com.passivehouse.PassiveHouse.services.SensorMeasurementsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.*;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.IOException;
import java.util.concurrent.CopyOnWriteArrayList;

@Component
public class SensorDataWebSocketHandler extends TextWebSocketHandler {

    private final SensorMeasurementsService sensorMeasurementsService;
    private final ObjectMapper objectMapper;
    private final AlarmService alarmService;
    private final PirSSEController pirSSEController;

    private final CopyOnWriteArrayList<WebSocketSession> sessions = new CopyOnWriteArrayList<>();

    @Autowired
    public SensorDataWebSocketHandler(
            SensorMeasurementsService sensorMeasurementsService,
            ObjectMapper objectMapper,
            AlarmService alarmService,
            PirSSEController pirSSEController
    ) {
        this.sensorMeasurementsService = sensorMeasurementsService;
        this.objectMapper = objectMapper;
        this.alarmService = alarmService;
        this.pirSSEController = pirSSEController;
    }

    @Override
    public void afterConnectionEstablished(WebSocketSession session) {
        System.out.println("Client connected: " + session.getId());
        sessions.add(session);
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) {
        String payload = message.getPayload();
        System.out.println("Received: " + payload);

        try {
            if (payload.contains("PIR")) {
                if (alarmService.isAlarmActive()) {
                    System.out.println("ALARM ACTIVE: triggering SSE");
                    pirSSEController.sendAlarmEvent();
                } else {
                    System.out.println("Alarm is off. Ignoring PIR.");
                }
                return;
            }

            if (payload.contains("RFID")) {
                // TODO: handle RFID logic here
                return;
            }

            // Try to parse as SensorMeasurement
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
