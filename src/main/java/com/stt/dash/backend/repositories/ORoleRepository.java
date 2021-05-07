package com.stt.dash.backend.repositories;

import com.stt.dash.backend.data.entity.ORole;
import com.stt.dash.backend.data.entity.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface ORoleRepository extends JpaRepository<ORole, Long> {

    List<ORole> findByRolName(String rol_name);

    @Query("select r from ORole r " +
            "WHERE lower(r.rolName) like lower(concat('%', :filterText, '%'))")
    List<ORole> searchAll(String filterText);


    Page<ORole> findByRolNameLikeIgnoreCase(String name, Pageable page);

    Page<ORole> findBy(Pageable page);

    @Override
    Optional<ORole> findById(Long aLong);

    int countByRolNameLikeIgnoreCase(String name);
}
