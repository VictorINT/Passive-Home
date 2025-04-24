package com.passivehouse.PassiveHouse.repository;

import com.passivehouse.PassiveHouse.model.RFID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RFIDRepository extends JpaRepository<RFID, Long> {}
