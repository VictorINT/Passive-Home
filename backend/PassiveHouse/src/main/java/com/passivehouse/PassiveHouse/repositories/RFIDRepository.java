package com.passivehouse.PassiveHouse.repositories;

import com.passivehouse.PassiveHouse.models.RFID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RFIDRepository extends JpaRepository<RFID, Long> {}
