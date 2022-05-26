/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.stt.smspreparationmanager.entity;

import javax.persistence.Entity;
import java.util.Date;

/**
 * @author Enrique
 */
@Entity
public class FilesToSend extends AbstractEntityAuto {

    public enum Status {
        VALIDATING("Validando"),
        GENERATING_MESSAGES("Generando"),
        PREPARING_SMS("Preparando"),
        WAITING_TO_SEND("Esperando"),
        SENDING("Enviando"),
        COMPLETED("Enviados"),
        INVALID("Invalido");

        private String text;

        Status(String text) {
            this.text = text;
        }

        public String getText() {
            return text;
        }
    }

    private int numGenerated;
    private int numSent;
    private int smsCount;
    private Status status;
    private String orderName;
    private String orderDescription;

    private String fileName;
    private Date dateToSend;
    private String fileId;
    private String systemId;
    private boolean readyToSend;
    private boolean beingProcessed;
    private String filePath;
    private Integer totalSmsToSend;

    public FilesToSend() {
        initDefaultValues();
    }

    public FilesToSend(String orderName, String orderDescription, String fileName, Date dateToSend, String systemId) {
        this.orderName = orderName;
        this.orderDescription = orderDescription;
        this.fileName = fileName;
        this.dateToSend = dateToSend;
        this.systemId = systemId;
        initDefaultValues();
    }

    public void initDefaultValues() {
        readyToSend = false;
        beingProcessed = false;
        smsCount = 0;
        numGenerated = 0;
        numSent = 0;
        status = Status.GENERATING_MESSAGES;
    }

    public String getGeneratedProgress() {
        return getPercentGenerated() + " (" + getNumGenerated() + "/" + getSmsCount() + ")";
    }

    public String getSentProgress() {
        return getPercentSent() + " (" + getNumSent() + "/" + getSmsCount() + ")";
    }

    public String getPercentGenerated() {
        int temp = (int) (1000 * getRatioGenerated());
        String str = temp / 10 + "." + temp % 10 + "%";
        return str;
    }

    public String getPercentSent() {
        int temp = (int) (1000 * getRatioSent());
        String str = temp / 10 + "." + temp % 10 + "%";
        return str;
    }

    public double getRatioGenerated() {
        if (getSmsCount() == 0) {
            return 0;
        }
        return ((double) getNumGenerated()) / getSmsCount();
    }

    public double getRatioSent() {
        if (getSmsCount() == 0) {
            return 0;
        }
        return ((double) getNumSent()) / getSmsCount();
    }

    public String getStatusText() {
        switch (getStatus()) {
            case COMPLETED:
                return "Completado";
            case GENERATING_MESSAGES:
                return "Generando Mensajes";
            case PREPARING_SMS:
                return "Creando SMS";
            case INVALID:
                return "Inválido";
            case SENDING:
                return "Mandando Mensajes";
            case VALIDATING:
                return "Validando";
            case WAITING_TO_SEND:
                return "Esperando para Mandar";
        }
        return "Error";
    }

    public int getNumGenerated() {
        return numGenerated;
    }

    public void setNumGenerated(int numGenerated) {
        this.numGenerated = numGenerated;
    }

    public int getNumSent() {
        return numSent;
    }

    public void setNumSent(int numSent) {
        this.numSent = numSent;
    }

    public int getSmsCount() {
        return smsCount;
    }

    public void setSmsCount(int smsCount) {
        this.smsCount = smsCount;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public String getOrderName() {
        return orderName;
    }

    public void setOrderName(String orderName) {
        this.orderName = orderName;
    }

    public String getOrderDescription() {
        return orderDescription;
    }

    public void setOrderDescription(String orderDescription) {
        this.orderDescription = orderDescription;
    }

    public String getSystemId() {
        return systemId;
    }

    public void setSystemId(String systemId) {
        this.systemId = systemId;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public Date getDateToSend() {
        return dateToSend;
    }

    public void setDateToSend(Date dateToSend) {
        this.dateToSend = dateToSend;
    }

    public String getFileId() {
        return fileId;
    }

    public void setFileId(String fileId) {
        this.fileId = fileId;
    }

    public boolean isReadyToSend() {
        return readyToSend;
    }

    public void setReadyToSend(boolean readyToSend) {
        this.readyToSend = readyToSend;
    }

    public boolean isBeingProcessed() {
        return beingProcessed;
    }

    public void setBeingProcessed(boolean beingProcessed) {
        this.beingProcessed = beingProcessed;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public Integer getTotalSmsToSend() {
        return totalSmsToSend;
    }

    public void setTotalSmsToSend(Integer totalSmsToSend) {
        this.totalSmsToSend = totalSmsToSend;
    }
}
