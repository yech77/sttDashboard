/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.msv.orinocosms.bean;

import java.io.Serializable;
import java.util.Date;

/**
 * Clase que representa la tabla en la cual se guardan los mensajes.
 *
 * @since
 * @author yech77
 */
public class AbstractSMSDTO implements Serializable{
    private static final long serialVersionUID = 1L;
    
    private Long id;
    private String system_id;
    private String messagesText;
    private String messageType;
    private Date date;
    private String iso2;
    private String carrier_char_code;
    private String source;
    private String destination;
    private int datacoding;
    private String msgSended;
    private String msgReceived;

    public Long getId() {
        return id;
}

    public void setId(Long id) {
        this.id = id;
    }

    public String getSystem_id() {
        return system_id;
    }

    public void setSystem_id(String systemid) {
        this.system_id = systemid;
    }

    public String getMessagesText() {
        return messagesText;
    }

    public void setMessagesText(String messagesText) {
        this.messagesText = messagesText;
    }

    public String getMessageType() {
        return messageType;
    }

    public void setMessageType(String messageType) {
        this.messageType = messageType;
    }

    public String getIso2() {
        return iso2;
    }

    public void setIso2(String iso2) {
        this.iso2 = iso2;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getDestination() {
        return destination;
    }

    public void setDestination(String destination) {
        this.destination = destination;
    }

    public int getDatacoding() {
        return datacoding;
    }

    public void setDatacoding(int datacoding) {
        this.datacoding = datacoding;
    }

    public String getMsgSended() {
        return msgSended;
    }

    public void setMsgSended(String msgSended) {
        this.msgSended = msgSended;
    }

    public String getMsgReceived() {
        return msgReceived;
    }

    public void setMsgReceived(String msgReceived) {
        this.msgReceived = msgReceived;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getCarrier_char_code() {
        return carrier_char_code;
    }

    public void setCarrier_char_code(String carrier_char_code) {
        this.carrier_char_code = carrier_char_code;
    }

}
