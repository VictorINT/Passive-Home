package com.passivehouse.PassiveHouse.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.passivehouse.PassiveHouse.models.SensorMeasurement;
import com.passivehouse.PassiveHouse.repositories.SensorMeasurementsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.IOException;

@Controller
public class WebSocketController extends TextWebSocketHandler {

    @Autowired
    private SensorMeasurementsRepository sensorMeasurementRepository;

    @Override
    public void handleTextMessage(WebSocketSession session, org.springframework.web.socket.TextMessage message) throws IOException {
        String payload = message.getPayload();

        // Parsează mesajul JSON
        try {
            SensorMeasurement measurement = new ObjectMapper().readValue(payload, SensorMeasurement.class);
            // Salvează obiectul în baza de date (ID-ul va fi generat automat)
            sensorMeasurementRepository.save(measurement);
            System.out.println("Sensor data received and saved: " + measurement);

            // Trimite un răspuns (poate fi un mesaj de tip hello sau altceva)
            session.sendMessage(new org.springframework.web.socket.TextMessage("Data received successfully!"));
        } catch (Exception e) {
            session.sendMessage(new org.springframework.web.socket.TextMessage("Error parsing sensor data"));
        }
    }
}
