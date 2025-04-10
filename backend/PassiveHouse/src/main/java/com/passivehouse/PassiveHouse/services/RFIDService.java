package com.passivehouse.PassiveHouse.services;

import com.passivehouse.PassiveHouse.models.RFID;
import com.passivehouse.PassiveHouse.repositories.RFIDRepository;
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
