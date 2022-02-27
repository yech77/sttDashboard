package com.stt.dash.backend.data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

public class SystemIdCopycatDTO {
    private Long id;

    private Long sytemidId;

    private String paymentType;

    private String systemId;

    private Long clientId;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getSytemidId() {
        return sytemidId;
    }

    public void setSytemidId(Long sytemidId) {
        this.sytemidId = sytemidId;
    }

    public String getPaymentType() {
        return paymentType;
    }

    public void setPaymentType(String paymentType) {
        this.paymentType = paymentType;
    }

    public String getSystemId() {
        return systemId;
    }

    public void setSystemId(String systemId) {
        this.systemId = systemId;
    }

    public Long getClientId() {
        return clientId;
    }

    public void setClientId(Long clientId) {
        this.clientId = clientId;
    }

    @Override
    public String toString() {
        return "SystemIdCopycatDTO{" +
                "id=" + id +
                ", sytemidId=" + sytemidId +
                ", paymentType='" + paymentType + '\'' +
                ", systemId='" + systemId + '\'' +
                ", clientId=" + clientId +
                '}';
    }
}