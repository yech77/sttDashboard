package com.stt.dash.backend.repositories;

import com.stt.dash.backend.data.entity.OAuthority;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OAuthorityRepository extends JpaRepository<OAuthority, Long> {

    List<OAuthority> findByAuthName(String authName);
}