/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.stt.smstransfertoqueue;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

/**
 * @since
 * @author yech77
 */
@Component
@PropertySource("file:${stt.sttq.propertyfile}/attributes.properties")
public class OProperties {
    @Value("${mq.imqaddresslist}")
    private String imqaddresslist;
    
    @Value("${mq.origen}")
    private String origen;
    
    @Value("${mq.target}")
    private String target;
    /**/
    @Value("${agenda.filepath.upload}")
    private String agendaFilePathUpload;
    
    @Value("${agenda.filepath.base}")
    private String agendafilePathBase;

    public String getImqaddresslist() {
        return imqaddresslist;
    }

    public String getOrigen() {
        return origen;
    }

    public String getTarget() {
        return target;
    }

    public String getAgendaFilePathUpload() {
        return agendaFilePathUpload;
    }

    public String getAgendafilePathBase() {
        return agendafilePathBase;
    }
    
}
