/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.stt.smstransfertoqueue.util;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

/**
 * @since @author yech77
 */
public class ODateUitls {

    public final static SimpleDateFormat dd_MM_yyyy_HH_mm_SS = new SimpleDateFormat("dd/MM/yyyy HH:mm:SS");
    public final static SimpleDateFormat dd_MM_yyyy = new SimpleDateFormat("dd/MM/yyyy");

    public static synchronized LocalDateTime valueOf(Date dateToConvert) {
        return dateToConvert.toInstant()
                .atZone(ZoneId.systemDefault())
                .toLocalDateTime();
    }

    public static synchronized Date valueOf(LocalDate localDateToConvert) {
        return Date.from(localDateToConvert.atStartOfDay().atZone(ZoneId.systemDefault()).toInstant());
    }

    public static synchronized Date localDateTimeToDate(LocalDateTime dateToConvert) {
        return java.sql.Timestamp.valueOf(dateToConvert);
    }
}
