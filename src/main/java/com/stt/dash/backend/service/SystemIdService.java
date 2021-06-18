package com.stt.dash.backend.service;

import com.stt.dash.backend.data.entity.SystemId;
import com.stt.dash.backend.repositories.SystemIdRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class SystemIdService {
    private final SystemIdRepository systemIdRepository;

    public SystemIdService(SystemIdRepository systemIdRepository) {
        this.systemIdRepository = systemIdRepository;
    }

    public Optional<SystemId> findBySystemId(String systemId) {
        return systemIdRepository.findBySystemId(systemId);
    }
}
