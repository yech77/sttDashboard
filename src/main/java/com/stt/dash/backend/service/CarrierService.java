package com.stt.dash.backend.service;

import com.stt.dash.backend.data.entity.Carrier;
import com.stt.dash.backend.repositories.CarrierRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class CarrierService{
    private CarrierRepository carrierRepository;

    public CarrierService(CarrierRepository carrierRepository) {
        this.carrierRepository = carrierRepository;
    }

    public Page<Carrier> findAll(Pageable pageable) {
        return carrierRepository.findAll(pageable);
    }

    /**
     * Solo devueve los primeros 100.
     *
     * @return
     */
    public Page<Carrier> findAll() {
        return carrierRepository.findAll(PageRequest.of(0, 100));
    }

}
