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
public class ODashAuditEvent extends AbstractEntitySequence {


    public static enum OEVENT_TYPE {
        LOGIN(0, "LOGIN"),
        LOGOUT(1, "LOGIN"),
        CREATE_AGENDA(2, "agenda creada"),
        CREATE_RECADO(3, "masivo programada"),
        CREATE_USER(4, "usuario creado"),
        UPDATE_AGENDA(5, "agenda actualizada"),
        UPDATE_RECADO(6, "masivo actualizado"),
        UPDATE_USER(7, "usuario actualizado"),
        DELETE_AGENDA(8, "agenda borrada"),
        DELETE_RECADO(9, "masivo borrado"),
        DELETE_USER(10, "usuario borrado"),
        PASSWORD_CHANGED(11, "clave cambiada"),
        DOWNLOAD_FILE_AGENDA(12, "descarga de agenda"),
        DOWNLOAD_FILE_TRAFFIC_SMS(13, "descarga de tráfico"),
        DOWNLOAD_FILE_SEARCH_SMS(14, "descarga de busqueda sms"),
        DOWNLOAD_FILE_AUDITEVENT(15, "descarga de archivo de auditoria"),
        ACCEPTED_SMS(16, "Aceptar envio de mensajes"),
        BLOCKED(17, "Usuario bloqueado");

        private int eventId;
        private String eventName;

        OEVENT_TYPE(int eventId, String eventName) {
            this.eventId = eventId;
            this.eventName = eventName;
        }
    }

    @GridColumn(order = 2, columnName = "Fecha")
    private Date eventDate;

    @Size(min = 3, max = 100)
    @Column(length = 100)
    @GridColumn(order = 0, columnName = "Usuario")
    private String principal;

    @GridColumn(order = 1, columnName = "Tipo")
    @Enumerated(EnumType.ORDINAL)
    private OEVENT_TYPE eventType;

    @GridColumn(order = 3, columnName = "Descripcion")
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
