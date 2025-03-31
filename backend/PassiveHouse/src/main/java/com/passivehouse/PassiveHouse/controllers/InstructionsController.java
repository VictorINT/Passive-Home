package com.passivehouse.PassiveHouse.controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.passivehouse.PassiveHouse.handlers.SensorDataWebSocketHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/instructions")
public class InstructionsController {

    @Autowired
    private SensorDataWebSocketHandler webSocketHandler;

    @PostMapping
    public ResponseEntity<?> uploadInstruction(@RequestBody String jsonInstruction) {
        try {
            // Broadcast the received JSON directly to WebSocket clients
            webSocketHandler.broadcast(jsonInstruction);
            System.out.println(jsonInstruction);
            return ResponseEntity.status(HttpStatus.ACCEPTED).body(jsonInstruction);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error processing JSON");
        }
    }
}
