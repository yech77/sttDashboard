package com.stt.dash.backend.service;

import com.stt.dash.backend.data.entity.ORole;
import com.stt.dash.backend.repositories.ORoleRepository;
import com.vaadin.flow.server.VaadinSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ORoleService {

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
}