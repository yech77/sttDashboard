package com.stt.dash.backend.data.entity;

import com.stt.dash.app.security.CurrentUser;
import com.stt.dash.backend.service.ODashAuditEventService;
import com.vaadin.flow.spring.annotation.SpringComponent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.actuate.audit.AuditEvent;
import org.springframework.boot.actuate.audit.AuditEventRepository;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.*;
@Component
public class MyAuditEventComponent implements AuditEventRepository {

    Logger log = LoggerFactory.getLogger(MyAuditEventComponent.class);
    /**/
    private final ODashAuditEventService audit_serv;
    List<AuditEvent> l = new ArrayList<>();

    public MyAuditEventComponent(ODashAuditEventService audit_serv) {
        this.audit_serv = audit_serv;
    }

    @Override
    public void add(AuditEvent event) {
        System.out.println("ADDING EVENT OVERRIDE: " + event);
        l.add(event);
        /* Lista de eventos del servicio maximo de 500. */
        if (l.size()>500){
            l.removeAll(l.subList(0, 50));
        }
        if (event.getType().equalsIgnoreCase("AUTHENTICATION_SUCCESS")){
            add(ODashAuditEvent.OEVENT_TYPE.LOGIN_IN, event.getPrincipal());
        }
    }

    @Override
    public List<AuditEvent> find(String principal, Instant after, String type) {
        return l;
    }

    public void add(String type, int year, int month, List<String> list) {
        Map<String, Object> map = new HashMap<>();
        map.put("year", year);
        map.put("month", month);
        map.put("list_sid", list);
        /**/
        AuditEvent e = new AuditEvent(SecurityContextHolder.getContext().getAuthentication().getName(), type, map);
        add(e);
    }

    public void add(ODashAuditEvent.OEVENT_TYPE type, String eventDesc) {
        Map<String, Object> map = new HashMap<>();
        map.put("eventDesc", eventDesc);
//        String desc = "Nombre: '" + SecurityContextHolder.getContext().getAuthentication().getName() + "'. Descripcion: " + type.name();
//        log.info("xxxxxxxxxxxxxxxxxxxxxxxxx " + type.name() +" - " + desc);
        /**/

        try {
            if (type== ODashAuditEvent.OEVENT_TYPE.LOGIN_IN){
                AuditEvent e = new AuditEvent(eventDesc, type.name(), map);
                add(e);
            }else{
                AuditEvent e = new AuditEvent(SecurityContextHolder.getContext().getAuthentication().getName(), type.name(), map);
                add(e);
            }
            /**/
            audit_serv.save(valueOf(type, eventDesc));
        } catch (Exception e) {
            e.printStackTrace();
            log.warn("{} EVENT DISCARTED BY NULL", e);
        }
    }

    public void add(ODashAuditEvent.OEVENT_TYPE type, String eventDesc, String user) {
        Map<String, Object> map = new HashMap<>();
        map.put("eventDesc", eventDesc);
//        String desc = "Nombre: '" + SecurityContextHolder.getContext().getAuthentication().getName() + "'. Descripcion: " + type.name();
//        log.info("xxxxxxxxxxxxxxxxxxxxxxxxx " + type.name() +" - " + desc);
        /**/
        AuditEvent e = new AuditEvent(user, type.name(), map);
        add(e);
        /**/
        audit_serv.save(valueOf(type, eventDesc, user));
    }

    public void add(ODashAuditEvent.OEVENT_TYPE type, Agenda agenda) {
        String desc = "Nombre: " + agenda.getName() + ". Descripcion: " + agenda.getDescription();
        log.info("************************ " + type.name() + " - " + desc);
        add(type, desc);
    }

    public void add(ODashAuditEvent.OEVENT_TYPE type, User user) {
        if (type == ODashAuditEvent.OEVENT_TYPE.CREATE_USER) {
            StringJoiner sjRol = new StringJoiner(",", "[", "]");
            user.getRoles().forEach(role -> {
                sjRol.add(role.getRolName());
            });
            StringJoiner sjCliJoiner = new StringJoiner(",", "[", "]");
            String data = "";
            if (user.getUserType() != User.OUSER_TYPE.BY) {
                user.getClients().forEach(client -> {
                    sjCliJoiner.add(client.getClientName());
                });
                data = "CLIENTES: ";
            } else {
                user.getSystemids().forEach(sid -> {
                    sjCliJoiner.add(sid.getSystemId());
                });
                data = "CREDENCIALES: ";
            }
            data += sjCliJoiner.toString();
            String desc = user.getEmail()
                    + ", " + user.getFirstName()
                    + ", " + user.getLastName()
                    + ", " + user.getUserTypeOrd().name()
//                    + ", " + user.getStatus().name()
                    + ", Roles: " + sjRol.toString()
                    + ", " + data;
            add(type, desc);
        } else {
            String desc = user.getFirstName() + " " + user.getLastName();
            log.info("************************ " + type.name() + " - " + desc);
            add(type, desc);
        }
    }

    public void add(ODashAuditEvent.OEVENT_TYPE type, User user, String changes) {
        if (type != ODashAuditEvent.OEVENT_TYPE.LOGIN_IN) {
            String desc = user.getEmail() + " - " + user.getFirstName() + " " + user.getLastName();
            log.info("************************ " + type.name() + " - " + desc + " CAMBIOS: " + changes);
            add(type, desc + " CAMBIOS: " + changes);
        } else {
            String desc = user.getFirstName() + " " + user.getLastName();
            log.info("************************ " + type.name() + " - " + desc + " CAMBIOS: " + changes);
            add(type, desc + " CAMBIOS: " + changes);
        }
    }

    public void add(ODashAuditEvent.OEVENT_TYPE type, FIlesToSend fileToSend) {
        String desc = "Nombre: " + fileToSend.getOrderName() + ". Descripcion" + fileToSend.getOrderDescription();
        add(type, desc);
    }

    public void add(ODashAuditEvent.OEVENT_TYPE type, FIlesToSend fileToSend, String user) {
        String desc = "Nombre: " + fileToSend.getOrderName() + ". Descripcion" + fileToSend.getOrderDescription();
        add(type, desc, user);
    }

    public ODashAuditEvent valueOf(ODashAuditEvent.OEVENT_TYPE type, String eventDesc) {
        ODashAuditEvent d = new ODashAuditEvent();
        d.setEventDate(new Date());
        d.setEventType(type);
        if (type== ODashAuditEvent.OEVENT_TYPE.LOGIN_IN){
            d.setEventDesc("Ingreso al sistema.");
            d.setPrincipal(eventDesc);
        }else{
            d.setEventDesc(eventDesc);
            d.setPrincipal(SecurityContextHolder.getContext().getAuthentication().getName());
        }
        return d;
    }

    public ODashAuditEvent valueOf(ODashAuditEvent.OEVENT_TYPE type, String eventDesc, String user) {
        ODashAuditEvent d = new ODashAuditEvent();
        d.setEventDate(new Date());
        d.setEventType(type);
        d.setEventDesc(eventDesc);
        d.setPrincipal(user);
        return d;
    }
}
