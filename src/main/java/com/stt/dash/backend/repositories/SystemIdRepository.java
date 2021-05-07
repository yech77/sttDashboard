package com.stt.dash.backend.repositories;

import com.stt.dash.backend.data.entity.SystemId;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SystemIdRepository extends JpaRepository<SystemId, Long> {

    public List<SystemId> findBySystemId(String systemId);
}
