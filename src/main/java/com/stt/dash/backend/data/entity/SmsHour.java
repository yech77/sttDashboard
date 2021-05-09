package com.stt.dash.backend.data.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Entity
public class SmsHour extends AbstractEntitySequence{


    private int total;
    private int year;
    private int month;
    private int day;
    private int hour;

    @Size(min = 2, max = 3)
    @Column(length = 3)
    private String messageType;

    @NotEmpty
    @NotNull
    @Size(min = 3, max = 20)
    @Column(length = 20)
    private String carrierCharCode;

    @NotEmpty
    @NotNull
    @Size(min = 3, max = 20)
    @Column(length = 20)
    private String systemId;

    @NotEmpty
    @NotNull
    @Size(min = 3, max = 20)
    @Column(length = 20)
    private String clientCod;

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public int getMonth() {
        return month;
    }

    public void setMonth(int month) {
        this.month = month;
    }

    public int getDay() {
        return day;
    }

    public void setDay(int day) {
        this.day = day;
    }

    public String getMessageType() {
        return messageType;
    }

    public void setMessageType(String messageType) {
        this.messageType = messageType;
    }

    public String getCarrierCharCode() {
        return carrierCharCode;
    }

    public void setCarrierCharCode(String carrierCharCode) {
        this.carrierCharCode = carrierCharCode;
    }

    public String getSystemId() {
        return systemId;
    }

    public void setSystemId(String systemId) {
        this.systemId = systemId;
    }

    @Override
    public String toString() {
        return "SmsHour{" + "total=" + total + ", year=" + year + ", month=" + month + ", day=" + day + ", hour=" + hour + ", messageType=" + messageType + ", carrierCharCode=" + carrierCharCode + ", systemId=" + systemId + ", clientCod=" + clientCod + '}';
    }

    public int getHour() {
        return hour;
    }

    public void setHour(int hour) {
        this.hour = hour;
    }

    public String getClientCod() {
        return clientCod;
    }

    public void setClientCod(String clientCod) {
        this.clientCod = clientCod;
    }

}
