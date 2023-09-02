package com.stt.dash.backend.repositories;

import com.stt.dash.backend.data.entity.ODashConf;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface OdashConfRepository extends JpaRepository<ODashConf, Long> {
    Optional<ODashConf> findBySyncId(@Param("syncId") Integer syncId);
}