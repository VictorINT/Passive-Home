package com.passivehouse.PassiveHouse.repositories;

import com.passivehouse.PassiveHouse.models.SensorMeasurement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SensorMeasurementsRepository extends JpaRepository<SensorMeasurement, Long> {

}
