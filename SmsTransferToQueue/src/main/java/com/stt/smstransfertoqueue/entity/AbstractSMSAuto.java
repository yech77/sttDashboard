/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.stt.smstransfertoqueue.entity;

import javax.persistence.*;
import javax.validation.constraints.Size;
import java.util.Date;

/**
 *
 * @author Enrique
 */
@MappedSuperclass
public class AbstractSMSAuto {
    
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;

    @Size(min = 3, max = 20)
    @Column(length = 20)
    private String systemId;

    @Column(length = 170)
    private String messagesText;

    @Size(min = 2, max = 3)
    @Column(length = 3)
    private String messageType;
    private Date date;

    @Size(min = 2, max = 2)
    @Column(length = 2)
    private String iso2;

    @Size(min = 3, max = 20)
    @Column(length = 20)
    private String carrierCharCode;

    @Size(min = 3, max = 30)
    @Column(length = 30)
    private String source;

    @Size(min = 3, max = 30)
    @Column(length = 30)
    private String destination;
    private int datacoding;

    @Column(length = 50)
    private String msgSended;

    @Column(length = 50)
    private String msgReceived;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getSystemId() {
        return systemId;
    }

    public void setSystemId(String systemid) {
        this.systemId = systemid;
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

    public String getCarrierCharCode() {
        return carrierCharCode;
    }

    public void setCarrierCharCode(String carrierCharCode) {
        this.carrierCharCode = carrierCharCode;
    }

    @Override
    public int hashCode() {
        if (getId() != null) {
            return getId().hashCode();
        }
        return super.hashCode();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null) {
            return false;
        }
        if (getClass() != o.getClass()) {
            return false;
        }
        AbstractSMS other = (AbstractSMS) o;
        if (getId() == null || other.getId() == null) {
            return false;
        } else {
            return getId().equals(other.getId());
        }

    }

    @Override
    public String toString() {
        return "" + id + ",\"" + destination + "\"," + datacoding + ",\"" + date + "\",\"" + iso2 + "\",\"" + messageType + "\",\"" + messagesText + "\",\"" + msgReceived + "\",\"" + msgSended + "\",\"" + source + "\",\"" + systemId + "\",\"" + carrierCharCode + "\"";
    }
}
