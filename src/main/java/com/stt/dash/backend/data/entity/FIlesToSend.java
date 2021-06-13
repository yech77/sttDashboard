package com.stt.dash.backend.data.entity;

import com.stt.dash.backend.data.Status;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ManyToOne;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;
import java.util.Date;

@Entity
public class FIlesToSend extends AbstractEntitySequence implements FileToSendSummary{

//    public enum Status {
//        VALIDATING,
//        GENERATING_MESSAGES,
//        PREPARING_SMS,
//        WAITING_TO_SEND,
//        SENDING,
//        COMPLETED,
//        INVALID
//    }

    @ManyToOne(fetch = FetchType.LAZY)
    private User userCreator;
    private int numGenerated;
    private int numSent;
    private int smsCount;
    private Status status;

    @NotNull
    @NotEmpty
    @Size(max = 100)
    @Column(length = 100, unique = true)
    private String orderName;

    @Size(max = 255)
    private String orderDescription;

    @Size(max = 255)
    private String fileName;

    @NotNull
    private Date dateToSend;

    @Size(max = 255)
    private String fileId;

    @NotEmpty
    @NotNull
    @Size(min = 3, max = 20)
    @Column(length = 20)
    private String systemId;
    private boolean readyToSend;
    private boolean beingProcessed;
    @Size(max = 255)
    private String filePath;

    public FIlesToSend() {
        initDefaultValues();
    }

    public FIlesToSend(String orderName, String orderDescription, String fileName, Date dateToSend, String systemId) {
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

    public String getGeneratedProgress(){
        return getPercentGenerated() + " (" + getNumGenerated() + "/" + getSmsCount() + ")";
    }

    public String getSentProgress(){
        return getPercentSent() + " (" + getNumSent() + "/" + getSmsCount() + ")";
    }

    public String getPercentGenerated() {
        int temp = (int)(1000*getRatioGenerated());
        String str = temp/10 + "." + temp%10 + "%";
        return str;
    }

    public String getPercentSent() {
        int temp = (int)(1000*getRatioSent());
        String str = temp/10 + "." + temp%10 + "%";
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

    public String getStatusText(){
        switch(getStatus()){
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

    public User getUserCreator() {
        return userCreator;
    }

    public void setUserCreator(User userCreator) {
        this.userCreator = userCreator;
    }


}
