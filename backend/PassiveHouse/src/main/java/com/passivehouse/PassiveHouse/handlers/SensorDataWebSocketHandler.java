package com.passivehouse.PassiveHouse.handlers;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.passivehouse.PassiveHouse.SSE.PirSSEController;
import com.passivehouse.PassiveHouse.models.RFID;
import com.passivehouse.PassiveHouse.models.SensorMeasurement;
import com.passivehouse.PassiveHouse.repositories.RFIDRepository;
import com.passivehouse.PassiveHouse.services.AlarmService;
import com.passivehouse.PassiveHouse.services.RFIDService;
import com.passivehouse.PassiveHouse.services.SensorMeasurementsService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.*;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

@Component
public class SensorDataWebSocketHandler extends TextWebSocketHandler {

    private final Logger logger = LoggerFactory.getLogger(SensorDataWebSocketHandler.class);

    private final SensorMeasurementsService sensorMeasurementsService;
    private final ObjectMapper objectMapper;
    private final AlarmService alarmService;
    private final PirSSEController pirSSEController;

    @Autowired
    private RFIDRepository rfidRepository;

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
//        System.out.println("Client connected: " + session.getId());
        logger.info("Client connected: {}", session.getId());
        sessions.add(session);
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) {
        String payload = message.getPayload();
//        System.out.println("Received: " + payload);
        logger.info("Received: {}", payload);
        try {
            if (payload.contains("PIR")) {
                if (alarmService.isAlarmActive()) {
//                    System.out.println("ALARM ACTIVE: triggering SSE");
                    logger.info("Alarm actitve: triggering SSE");
                    pirSSEController.sendAlarmEvent();
                } else {
//                    System.out.println("Alarm is off. Ignoring PIR.");
                    logger.info("Alarm inactive: Ignoring PIR");
                }
            }
            else if (payload.contains("RFID")) {
                JsonNode node = objectMapper.readTree(payload);
                String rfidTag = node.get("RFID").asText();
//                System.out.println(rfidTag);
                logger.info(rfidTag);
                List<RFID> rfids = rfidRepository.findAll();

                for (RFID value : rfids) {
                    if (value.getRfid_tag().equals(rfidTag)) {
                        // access granted
//                        System.out.println("access granted for RFID: " + value.getRfid_tag());
                        logger.info("access granted for RFID: {}", value.getRfid_tag());
                        broadcast("[SPRING] access granted for RFID: " + value.getRfid_tag());
                    }
                    else{
                        // access denied
//                        System.out.println("access denied for RFID: " + value.getRfid_tag());
                        logger.info("access denied for RFID: {}", value.getRfid_tag());
                        broadcast("[SPRING] access denied for RFID: " + value.getRfid_tag());
                    }
                }
            }
            else {
                // Try to parse as SensorMeasurement
                SensorMeasurement sensorMeasurement = objectMapper.readValue(payload, SensorMeasurement.class);
                sensorMeasurementsService.uploadSensorMeasurement(sensorMeasurement);
//                System.out.println(sensorMeasurement.getId() + ";" + sensorMeasurement.getTimestamp() + ";" +
//                        sensorMeasurement.getTemperature() + ";" + sensorMeasurement.getHumidity() + ";" +
//                        sensorMeasurement.getLight1() + ";" + sensorMeasurement.getLight2() + ";" + sensorMeasurement.getLight3() + ";" +
//                        sensorMeasurement.getVoltage1() + ";" + sensorMeasurement.getVoltage2() + ";" +
//                        sensorMeasurement.getCurrent1() + ";" + sensorMeasurement.getCurrent2());
            }
        } catch (Exception e) {
//            System.err.println("Invalid SensorMeasurement JSON, treating as raw message.");
            logger.error("Invalid SensorMeasurement JSON, treating as raw message.");
        }
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) {
//        System.out.println("Client disconnected: " + session.getId());
        logger.info("Client disconnected: {}", session.getId());
        sessions.remove(session);
    }

    public void broadcast(String message) {
        for (WebSocketSession session : sessions) {
            if (session.isOpen()) {
                try {
                    session.sendMessage(new TextMessage(message));
                } catch (IOException e) {
//                    System.err.println("Error sending WebSocket message: " + e.getMessage());
                    logger.error("Error sending WebSocket message: {}", e.getMessage());
                }
            }
        }
    }
}
