package com.stt.dash.backend.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import com.stt.dash.backend.data.entity.Customer;

public interface CustomerRepository extends JpaRepository<Customer, Long> {
}
