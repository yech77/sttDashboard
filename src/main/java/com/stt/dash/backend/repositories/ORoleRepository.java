package com.stt.dash.backend.repositories;

import com.stt.dash.backend.data.entity.ORole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ORoleRepository extends JpaRepository<ORole, Long> {

    List<ORole> findByRolName(String rol_name);

    @Query("select r from ORole r " +
            "WHERE lower(r.rolName) like lower(concat('%', :filterText, '%'))")
    List<ORole> searchAll(String filterText);
}
