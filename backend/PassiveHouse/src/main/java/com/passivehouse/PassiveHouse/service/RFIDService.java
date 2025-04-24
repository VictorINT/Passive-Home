package com.passivehouse.PassiveHouse.service;

import com.passivehouse.PassiveHouse.model.RFID;
import com.passivehouse.PassiveHouse.repository.RFIDRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RFIDService {
    @Autowired
    private RFIDRepository rfidRepository;

    public List<RFID> getRFID(){
        return rfidRepository.findAll();
    }

    public RFID uploadRFID(RFID rfid){
        return rfidRepository.save(rfid);
    }

    public void deleteRFIDById(Long id) {
        rfidRepository.deleteById(id);
    }
}
