package com.stt.dash.backend.data.entity;

import com.stt.dash.backend.data.Status;

import java.util.Date;

public interface FileToSendSummary {
    Long getId();

    Status getStatus();

    Date getDateToSend();

    int getNumGenerated();

    int getNumSent();

    int getSmsCount();

    String getOrderName();

    String getOrderDescription();

    String getSystemId();

    String getFileName();

    String getFileId();

    boolean isReadyToSend();

    boolean isBeingProcessed();

    String getFilePath();

    User getUserCreator();
}
