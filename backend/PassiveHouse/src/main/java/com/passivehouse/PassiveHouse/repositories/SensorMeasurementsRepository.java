package com.passivehouse.PassiveHouse.repositories;

import com.passivehouse.PassiveHouse.models.SensorMeasurement;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SensorMeasurementsRepository extends JpaRepository<SensorMeasurement, Long> {
//    @Query("SELECT s FROM SensorMeasurements ORDER BY s.timestamp DESC")
//    List<SensorMeasurement> findLatestSorted(Sort sort);
    SensorMeasurement findTopByOrderByTimestampDesc();
}
