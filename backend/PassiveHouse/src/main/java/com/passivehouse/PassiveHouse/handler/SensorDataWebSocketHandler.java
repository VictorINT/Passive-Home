package com.passivehouse.PassiveHouse.handler;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.passivehouse.PassiveHouse.SSE.PirSSEController;
import com.passivehouse.PassiveHouse.model.RFID;
import com.passivehouse.PassiveHouse.model.SensorMeasurement;
import com.passivehouse.PassiveHouse.repository.RFIDRepository;
import com.passivehouse.PassiveHouse.service.AlarmService;
import com.passivehouse.PassiveHouse.service.SensorMeasurementsService;
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

    private boolean yolo_flag = false;

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
        logger.info("Client connected: {}", session.getId());
        sessions.add(session);
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) {
        String payload = message.getPayload();
        logger.info("Received: {}", payload);
        try {
            if (payload.contains("PIR")) {
                if (alarmService.isAlarmActive()) {
                    logger.info("Alarm actitve: triggering SSE");
                    pirSSEController.sendAlarmEvent();
                } else {
                    logger.info("Alarm inactive: Ignoring PIR");
                }
            }
            else if (payload.contains("RFID")) {
                JsonNode node = objectMapper.readTree(payload);
                String rfidTag = node.get("RFID").asText();
                logger.info(rfidTag);
                List<RFID> rfids = rfidRepository.findAll();

                for (RFID value : rfids) {
                    if (value.getRfid_tag().equals(rfidTag) && yolo_flag) {
                        // access granted
                        logger.info("access granted for RFID: {}", value.getRfid_tag());
                        broadcast("[SPRING] access granted for RFID: " + value.getRfid_tag());
                    }
                    else{
                        // access denied
                        logger.info("access denied for RFID: {}", value.getRfid_tag());
                        broadcast("[SPRING] access denied for RFID: " + value.getRfid_tag());
                    }
                }
            }
            else if(payload.contains("YOLO")){
                // todo: check if the plate number is on the white list
                // todo: if yes, set flag
                // todo: otherwise don't
                // yolo_flag
            }
            else {
                SensorMeasurement sensorMeasurement = objectMapper.readValue(payload, SensorMeasurement.class);
                sensorMeasurementsService.uploadSensorMeasurement(sensorMeasurement);
            }
        } catch (Exception e) {
            logger.error("Invalid SensorMeasurement JSON, treating as raw message.");
        }
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) {
        logger.info("Client disconnected: {}", session.getId());
        sessions.remove(session);
    }

    public void broadcast(String message) {
        for (WebSocketSession session : sessions) {
            if (session.isOpen()) {
                try {
                    session.sendMessage(new TextMessage(message));
                } catch (IOException e) {
                    logger.error("Error sending WebSocket message: {}", e.getMessage());
                }
            }
        }
    }
}
