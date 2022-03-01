package com.stt.dash.backend.service;

import com.stt.dash.backend.data.entity.ODashConf;
import com.stt.dash.backend.data.entity.SystemId;
import com.stt.dash.backend.repositories.SystemIdRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;
import java.util.Optional;

@Service
public class SystemIdService {
    private final static Logger log = LoggerFactory.getLogger(SystemIdService.class);
    private final SystemIdRepository systemIdRepository;
    private final OdashConfService conf_repo;

    public SystemIdService(@Autowired SystemIdRepository systemIdRepository,
                           @Autowired OdashConfService conf_repo) {
        this.systemIdRepository = systemIdRepository;
        this.conf_repo = conf_repo;
    }

    public Optional<SystemId> findBySystemId(String systemId) {
        return systemIdRepository.findBySystemId(systemId);
    }

    @Transactional
    public SystemId sync(SystemId systemId, Map<String, String> confMap, Long id) {
        SystemId s = systemIdRepository.save(systemId);
        ODashConf oDashConf = conf_repo.save(OdashConfService.ODASH_CONF_TYPE.SYNC, confMap, id);
        log.info("[SYNC] [SYSTEMID] [{}] [{}]", s.getSystemId(), s.getId());
        return s;
    }
}
