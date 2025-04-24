package com.passivehouse.PassiveHouse.repository;

import com.passivehouse.PassiveHouse.model.SensorMeasurement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SensorMeasurementsRepository extends JpaRepository<SensorMeasurement, Long> {
//    @Query("SELECT s FROM SensorMeasurements ORDER BY s.timestamp DESC")
//    List<SensorMeasurement> findLatestSorted(Sort sort);
    SensorMeasurement findTopByOrderByTimestampDesc();
}
