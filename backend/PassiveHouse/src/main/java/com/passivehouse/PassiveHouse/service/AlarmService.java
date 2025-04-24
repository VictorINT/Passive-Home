package com.passivehouse.PassiveHouse.service;

import com.passivehouse.PassiveHouse.model.AlarmState;
import com.passivehouse.PassiveHouse.repository.AlarmStateRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AlarmService {
    @Autowired
    private AlarmStateRepository alarmStateRepository;

    public boolean isAlarmActive() {
        return alarmStateRepository.findById(1L)
                .map(AlarmState::getActive)
                .orElse(false);
    }
}
