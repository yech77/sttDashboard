package com.stt.dash.backend.service;

import com.stt.dash.backend.data.entity.ODashAuditEvent;
import com.stt.dash.backend.repositories.ODashAuditEventRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
public class ODashAuditEventService {

    /**/
    private ODashAuditEventRepository audit_repo;

    private static String UI_CODE = "SERV_DAES";
    private static final Logger log = LoggerFactory.getLogger(ODashAuditEventService.class.getName());

    public ODashAuditEventService(@Autowired ODashAuditEventRepository audit_repo) {
        this.audit_repo = audit_repo;
    }

    public void save(ODashAuditEvent dashEvent) {
        if (dashEvent == null) {
            log.warn("{} ODashAuditEvent is null", getStringLog());
            return;
        }
        try {
            Long id = dashEvent.getId();
            audit_repo.save(dashEvent);
        } catch (Exception d) {
            log.error("{} Error on Save: {}", getStringLog(), dashEvent);
            log.error("", d);
        }
    }

    public List<ODashAuditEvent> findAll() {
        return audit_repo.findAll();
    }

    public List<ODashAuditEvent> findAll(String principal, Date one,Date two) {
        if (principal==null || principal==""){
            return new ArrayList<>();
        }
        return audit_repo.findAllByPrincipalAndEventDateBetweenOrderByEventDateDesc(principal, one, two).orElse(new ArrayList<>());
    }

    public List<ODashAuditEvent> findAll(List<String> principals, ODashAuditEvent.OEVENT_TYPE event, Date one, Date two) {
        if (principals==null || principals.isEmpty()){
            return new ArrayList<>();
        }
        return audit_repo.findAllByPrincipalInAndEventTypeAndEventDateBetweenOrderByEventDateDesc(
                principals, event, one, two).orElse(new ArrayList<>());
    }

    public List<ODashAuditEvent> findAll(Date one, Date two) {
        return audit_repo.findAllByEventDateBetweenOrderByEventDateDesc(one, two).orElse(new ArrayList<>());
    }

    public List<ODashAuditEvent> findAll(String principal, ODashAuditEvent.OEVENT_TYPE event, Date one,Date two) {
        if (principal==null || principal==""){
            return new ArrayList<>();
        }
        return audit_repo.findAllByPrincipalAndEventTypeAndEventDateBetweenOrderByEventDateDesc(principal, event,  one, two).orElse(new ArrayList<>());
    }

    private String getStringLog() {
        String id = SecurityContextHolder.getContext().getAuthentication().getName();
        StringBuilder sb = new StringBuilder();
        sb.append('[').append(id).append("] [").append(UI_CODE).append("]");
        return sb.toString();
    }
}