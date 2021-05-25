package com.stt.dash.backend.repositories;

import com.stt.dash.backend.data.entity.Carrier;
import com.stt.dash.backend.data.entity.Client;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CarrierRepository extends JpaRepository<Carrier, Long> {

    @Override
    Page<Carrier> findAll(Pageable pageable);
}
