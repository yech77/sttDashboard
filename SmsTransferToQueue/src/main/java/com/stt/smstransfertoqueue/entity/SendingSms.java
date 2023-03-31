/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.stt.smstransfertoqueue.entity;

import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

/**
 *
 * @author Enrique
 */
@Entity
public class SendingSms extends AbstractSMS{
    
    @ManyToOne
    @JoinColumn(name = "fileToSend_id")
    FilesToSend fileToSend;

    public FilesToSend getFileToSend() {
        return fileToSend;
    }

    public void setFileToSend(FilesToSend fileToSend) {
        this.fileToSend = fileToSend;
    }
}
