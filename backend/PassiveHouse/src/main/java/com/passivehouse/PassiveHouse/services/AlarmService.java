package com.passivehouse.PassiveHouse.services;

import com.passivehouse.PassiveHouse.models.AlarmState;
import com.passivehouse.PassiveHouse.repositories.AlarmStateRepository;
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
