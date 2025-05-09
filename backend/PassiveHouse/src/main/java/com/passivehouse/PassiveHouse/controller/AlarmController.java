package com.passivehouse.PassiveHouse.controller;

import com.passivehouse.PassiveHouse.model.AlarmState;
import com.passivehouse.PassiveHouse.repository.AlarmStateRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/alarm")
//@CrossOrigin(origins = "*")
public class AlarmController {

    @Autowired
    private AlarmStateRepository alarmStateRepository;

    @GetMapping("/state")
    public boolean getAlarmState() {
        return alarmStateRepository.findById(1L)
                .map(AlarmState::getActive)
                .orElse(false);
    }

    @PostMapping("/state")
    public void setAlarmState(@RequestBody AlarmState newState) {
        AlarmState alarmState = alarmStateRepository.findById(1L)
                .orElse(new AlarmState(1L, false));

        alarmState.setActive(newState.getActive());
        alarmStateRepository.save(alarmState);
    }
}
