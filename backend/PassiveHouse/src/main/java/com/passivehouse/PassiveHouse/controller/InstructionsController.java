package com.passivehouse.PassiveHouse.controller;

import com.passivehouse.PassiveHouse.handler.SensorDataWebSocketHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RestController
@RequestMapping("/instructions")
public class InstructionsController {
    private final Logger logger = LoggerFactory.getLogger(InstructionsController.class);
    @Autowired
    private SensorDataWebSocketHandler webSocketHandler;

    @PostMapping
    public ResponseEntity<?> uploadInstruction(@RequestBody String jsonInstruction) {
        try {
            webSocketHandler.broadcast(jsonInstruction);
            logger.info(jsonInstruction);
            return ResponseEntity.status(HttpStatus.ACCEPTED).body(jsonInstruction);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error processing JSON");
        }
    }
}
