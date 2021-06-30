package com.stt.dash.backend.data.entity;

import com.stt.dash.ui.crud.GridColumn;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.validation.constraints.Size;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;

@Entity
public class ODashAuditEvent extends AbstractEntitySequence{


    public static enum OEVENT_TYPE {
        CREATE_AGENDA,
        CREATE_RECADO,
        UPDATE_AGENDA,
        UPDATE_RECADO,
        DELETE_AGENDA,
        DELETE_RECADO,
        LOGIN_IN,
        LOGOUT,
        PASSWORD_CHANGED,
        CREATE_USER,
        DOWNLOAD_FILE_AGENDA, DOWNLOAD_FILE_TRAFFIC_SMS, DOWNLOAD_FILE_SEARCH_SMS, UPDATE_USER,
        DOWNLOAD_FILE_AUDITEVENT
    }

    @GridColumn(order = 2, columnName = "Date")
    private Date eventDate;

    @Size(min = 3, max = 100)
    @Column(length = 100)
    @GridColumn(order = 0, columnName = "USUARIO")
    private String principal;

    @GridColumn(order = 1, columnName = "TIPO")
    @Enumerated(EnumType.ORDINAL)
    private OEVENT_TYPE eventType;

    @GridColumn(order = 3, columnName = "DESSCRIPCION")
    @Size(min = 3)
    private String eventDesc;

    public Date getEventDate() {
        return eventDate;
    }

    public void setEventDate(Date eventDate) {
        this.eventDate = eventDate;
    }

    public String getPrincipal() {
        return principal;
    }

    public void setPrincipal(String principal) {
        this.principal = principal;
    }

    public OEVENT_TYPE getEventType() {
        return eventType;
    }

    public void setEventType(OEVENT_TYPE eventType) {
        this.eventType = eventType;
    }

    public String getEventDesc() {
        return eventDesc;
    }

    public void setEventDesc(String eventDesc) {
        this.eventDesc = eventDesc;
    }

    @Override
    public String toString() {
        return "ODashAuditEvent{" + "eventDate=" + eventDate + ", principal=" + principal + ", eventType=" + eventType + ", eventDesc=" + eventDesc + '}';
    }

}
