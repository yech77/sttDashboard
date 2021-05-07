package com.stt.dash.backend.service;

import com.stt.dash.backend.data.entity.ORole;
import com.stt.dash.backend.data.entity.User;
import com.stt.dash.backend.repositories.ORoleRepository;
import com.vaadin.flow.server.VaadinSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ORoleService implements FilterableCrudService<ORole>{

    private static String UI_CODE = "SERV_ROL";
    private static final Logger log = LoggerFactory.getLogger(ORoleService.class.getName());
    private ORoleRepository role_repo;

    public ORoleService(ORoleRepository role_repo) {
        this.role_repo = role_repo;
    }

    private String getStringLog() {
        String id = VaadinSession.getCurrent().getSession().getId();
        StringBuilder sb = new StringBuilder();
        sb.append('[').append(id).append("] [").append(UI_CODE).append("]");
        return sb.toString();
    }

    public long count() {
        return role_repo.count();
    }

    public void delete(ORole role) {
        try {
            role_repo.delete(role);
            log.info("{} Deleted: [{}]", getStringLog(), role.getRolName());
        } catch (Exception d) {
            log.error("{} Error on Delete [{}]:", getStringLog(), role.getRolName());
            log.error("", d);
        }
    }

    public void save(ORole role) {
        if (role == null) {
            log.warn("{} ORole is null", getStringLog());
            return;
        }
        try {
            Long id = role.getId();
            role_repo.save(role);
            if (id == null) {
                log.info("{} Saved: ORole[{}]", getStringLog(), role.getRolName());
            } else {
                log.info("{} Updated: ORole[{}]", getStringLog(), role.getRolName());
            }
        } catch (Exception d) {
            log.error("{} Error on Save:", getStringLog());
            log.error("", d);
        }
    }

    public List<ORole> findAll(String filterText) {
        if (filterText.length() < 1) {
            return role_repo.findAll();
        }
        return role_repo.searchAll(filterText);
    }

    public List<ORole> findByRolName(String roleName) {
        return role_repo.findByRolName(roleName);
    }

















    @Override
    public long countAnyMatching(Optional<String> filter) {
        if (filter.isPresent()) {
            String repositoryFilter = "%" + filter.get() + "%";
            return role_repo.countByRolNameLikeIgnoreCase(repositoryFilter);
        } else {
            return count();
        }
    }

    @Override
    public Page<ORole> findAnyMatching(Optional<String> filter, Pageable pageable) {
        if (filter.isPresent()) {
            String repositoryFilter = "%" + filter.get() + "%";
            return role_repo.findByRolNameLikeIgnoreCase(repositoryFilter, pageable);
        } else {
            return find(pageable);
        }
    }

    public Page<ORole> find(Pageable pageable) {
        return role_repo.findBy(pageable);
    }


    @Override
    public JpaRepository<ORole, Long> getRepository() {
        return role_repo;
    }

    @Override
    public ORole createNew(User currentUser) {
        return new ORole();
    }

    @Override
    public ORole save(User currentUser, ORole entity) {
        try {
            return FilterableCrudService.super.save(currentUser, entity);
        } catch (DataIntegrityViolationException e) {
            throw new UserFriendlyDataException(
                    "There is already a product with that name. Please select a unique name for the product.");
        }

    }
}