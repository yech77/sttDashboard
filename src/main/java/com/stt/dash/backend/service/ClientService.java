package com.stt.dash.backend.service;

import com.stt.dash.backend.data.entity.Client;
import com.stt.dash.backend.data.entity.ODashConf;
import com.stt.dash.backend.repositories.ClientRepository;
import com.stt.dash.backend.repositories.OdashConfRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class ClientService {
    private final static Logger log = LoggerFactory.getLogger(ClientService.class);
    private final ClientRepository client_repo;
    private final OdashConfService conf_repo;

    public ClientService(@Autowired ClientRepository client_repo,
                         @Autowired OdashConfService conf_repo) {
        this.client_repo = client_repo;
        this.conf_repo = conf_repo;
    }

    public List<Client> findByClientCod(String clientCod) {
        return client_repo.findByClientCod(clientCod);

    }

    public Page<Client> findAll(Pageable pageable) {
        return client_repo.findAll(pageable);
    }

    /**
     * Solo devueve los primeros 100.
     *
     * @return
     */
    public Page<Client> findAll() {
        return client_repo.findAll(PageRequest.of(0, 100));
    }

    public Optional<Client> findById(Long id) {
        return client_repo.findById(id);
    }

    public Client save(Client client) {
        Client c = client_repo.save(client);
        log.info("[SYNC] [CLIENT] [{}] [{}]", c.getClientCod(), c.getId());
        return c;
    }

    @Transactional
    public Client sync(Client client, Map<String, String> map, Long id) {
        Client c = client_repo.save(client);
        ODashConf oDashConf = conf_repo.save(OdashConfService.ODASH_CONF_TYPE.SYNC, map, id);
        log.info("[SYNC] [CLIENT] [{}] [{}]", c.getClientCod(), c.getId());
        return c;
    }

}
