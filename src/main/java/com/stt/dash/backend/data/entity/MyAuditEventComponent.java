package com.stt.dash.backend.data.entity;

import com.stt.dash.backend.service.ODashAuditEventService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.actuate.audit.AuditEvent;
import org.springframework.boot.actuate.audit.AuditEventRepository;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetails;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.*;

@Component
public class MyAuditEventComponent implements AuditEventRepository {

    public static final String EVENT_AUTHENTICATION_FAILURE = "AUTHENTICATION_FAILURE";
    public static final String EVENT_AUTHENTICATION_SUCCESS = "AUTHENTICATION_SUCCESS";
    Logger log = LoggerFactory.getLogger(MyAuditEventComponent.class);
    /**/
    private final ODashAuditEventService audit_serv;
    List<AuditEvent> l = new ArrayList<>();

    public MyAuditEventComponent(ODashAuditEventService audit_serv) {
        this.audit_serv = audit_serv;
    }

    @Override
    public void add(AuditEvent event) {
//        System.out.println("ADDING EVENT OVERRIDE: " + event);
        l.add(event);
        /* Lista de eventos del servicio maximo de 500. */
        if (l.size() > 500) {
            l.removeAll(l.subList(0, 50));
        }
        System.out.println("Principal " + event.getPrincipal()
                + " - " + event.getType());
        WebAuthenticationDetails details =
                (WebAuthenticationDetails) event.getData().get("details");
        if (details!=null) {
            System.out.println("Remote IP address: " + details.getRemoteAddress());
        }
        if (event.getType().equalsIgnoreCase(EVENT_AUTHENTICATION_FAILURE)
                && event.getPrincipal().equalsIgnoreCase("anonymousUser")) {
            add(ODashAuditEvent.OEVENT_TYPE.LOGOUT, event.getType() + " from " + details.getRemoteAddress(), event.getPrincipal());
        } else if (event.getType().equalsIgnoreCase(EVENT_AUTHENTICATION_SUCCESS)) {
            add(ODashAuditEvent.OEVENT_TYPE.LOGIN, event.getType() + " from " + details.getRemoteAddress(), event.getPrincipal());
        }
    }

    @Override
    public List<AuditEvent> find(String principal, Instant after, String type) {
        return l;
    }

    /**
     * Evento cuando se crea o se actualiza un usruaio.
     *
     * @param type
     * @param user
     */
    public void add(ODashAuditEvent.OEVENT_TYPE type, User user) {
        String desc;
        if (type == ODashAuditEvent.OEVENT_TYPE.CREATE_USER) {
            desc = addNewUserEventDescription(user);
        } else {
            desc = addUpdateUserEventDescription(user);
        }
        add(type, desc);
    }

    public void add(String type, int year, int month, List<String> list) {
        Map<String, Object> map = new HashMap<>();
        map.put("year", year);
        map.put("month", month);
        map.put("list_sid", list);
        /**/
        AuditEvent e = new AuditEvent(getAuthenticationName(), type, map);
        add(e);
    }

    public void add(ODashAuditEvent.OEVENT_TYPE type, String eventDesc) {
        Map<String, Object> map = new HashMap<>();
        map.put("eventDesc", eventDesc);
//        String desc = "Nombre: '" + getAuthenticationName() + "'. Descripcion: " + type.name();
//        log.info("xxxxxxxxxxxxxxxxxxxxxxxxx " + type.name() +" - " + desc);
        /**/

        try {
            if (type == ODashAuditEvent.OEVENT_TYPE.LOGIN) {
                AuditEvent e = new AuditEvent(eventDesc, type.name(), map);
                add(e);
            } else if (type == ODashAuditEvent.OEVENT_TYPE.LOGOUT) {
            } else {
                AuditEvent e = new AuditEvent(getAuthenticationName() , type.name(), map);
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
//        String desc = "Nombre: '" + getAuthenticationName() + "'. Descripcion: " + type.name();
//        log.info("xxxxxxxxxxxxxxxxxxxxxxxxx " + type.name() +" - " + desc);
        /**/
        AuditEvent e = new AuditEvent(user, type.name(), map);
        /* Ambos eventos son disparados por Springboot, asi que no llamo la metodo. */
        if (type != ODashAuditEvent.OEVENT_TYPE.LOGOUT && type != ODashAuditEvent.OEVENT_TYPE.LOGIN) {
            add(e);
        }
        /**/
        audit_serv.save(valueOf(type, eventDesc, user));
    }

    public void add(ODashAuditEvent.OEVENT_TYPE type, Agenda agenda) {
        String desc = "Nombre: " + agenda.getName() + ". Descripcion: " + agenda.getDescription();
//        log.info("************************ " + type.name() + " - " + desc);
        add(type, desc);
    }

    private String addUpdateUserEventDescription(User user) {
        return addNewUserEventDescription(user);
    }

    private String addNewUserEventDescription(User user) {
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
                + ", " + (user.isActive() ? "Activo" : "Desactivado")
                + ", Roles: " + sjRol.toString()
                + ", " + data;
        return desc;
    }

    public void add(ODashAuditEvent.OEVENT_TYPE type, User user, String changes) {
        if (type != ODashAuditEvent.OEVENT_TYPE.LOGIN) {
            String desc = user.getEmail() + " - " + user.getFirstName() + " " + user.getLastName();
//            log.info("************************ " + type.name() + " - " + desc + " CAMBIOS: " + changes);
            add(type, desc + " CAMBIOS: " + changes);
        } else {
            String desc = user.getFirstName() + " " + user.getLastName();
//            log.info("************************ " + type.name() + " - " + desc + " CAMBIOS: " + changes);
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
        if (type == ODashAuditEvent.OEVENT_TYPE.LOGIN) {
            d.setEventDesc("Ingreso al sistema.");
            d.setPrincipal(eventDesc);
        } else {
            d.setEventDesc(eventDesc);
            d.setPrincipal(getAuthenticationName());
        }
        return d;
    }
    private String getAuthenticationName(){
        String name="anonimous";
        try {
            name = SecurityContextHolder.getContext().getAuthentication().getName();
        } catch (Exception e) {
        }
        return name;
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
