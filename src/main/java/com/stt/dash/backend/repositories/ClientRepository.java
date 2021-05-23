package com.stt.dash.backend.repositories;

import com.stt.dash.backend.data.entity.Client;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ClientRepository extends JpaRepository<Client, Long> {

    List<Client> findByClientCod(String clientCod);
    @Override
    Page<Client> findAll(Pageable pageable);
}
