package com.stt.dash.backend.service;

import com.stt.dash.backend.data.entity.Client;
import com.stt.dash.backend.repositories.ClientRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ClientService{
    private final ClientRepository client_repo;

    public ClientService(@Autowired ClientRepository client_repo) {
        this.client_repo = client_repo;
    }
    public List<Client> findByClientCod(String clientCod){
        return client_repo.findByClientCod(clientCod);
    }
}
