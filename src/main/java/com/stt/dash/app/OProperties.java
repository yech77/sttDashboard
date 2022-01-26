package com.stt.dash.app;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

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

    @Value("${orinoco.host}")
    public static String ORINOCO_HOST;

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

    public static String getOrinocoHost() {
        return ORINOCO_HOST;
    }
}
