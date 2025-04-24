package com.passivehouse.PassiveHouse.controller;

import com.passivehouse.PassiveHouse.model.RFID;
import com.passivehouse.PassiveHouse.service.RFIDService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/rfid")
public class RFIDController {
    @Autowired
    private RFIDService rfidService;

    @GetMapping("/get")
    public List<RFID> get_all_rfids(){
        return rfidService.getRFID();
    }

    @PostMapping("/add")
    public ResponseEntity<?> add_Rfid(@RequestBody RFID rfid){
        return ResponseEntity.status(HttpStatus.CREATED).body(rfidService.uploadRFID(rfid));
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> add_Rfid(@PathVariable Long id){
        try{
            rfidService.deleteRFIDById(id);
            return ResponseEntity.ok("Deleted RFID with id: " + id);
        } catch(EmptyResultDataAccessException e){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("RFID not found, id: " + id);
        }
    }

}
