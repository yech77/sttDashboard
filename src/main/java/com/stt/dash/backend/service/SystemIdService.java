package com.stt.dash.backend.service;

import com.stt.dash.backend.repositories.SystemIdRepository;
import org.springframework.stereotype.Service;

@Service
public class SystemIdService {
    private final SystemIdRepository systemIdRepository;

    public SystemIdService(SystemIdRepository systemIdRepository) {
        this.systemIdRepository = systemIdRepository;
    }
}
