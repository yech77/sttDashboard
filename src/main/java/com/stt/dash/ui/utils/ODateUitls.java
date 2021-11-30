package com.stt.dash.ui.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

public class ODateUitls {

    public final static SimpleDateFormat dd_MM_yyyy_HH_mm_SS = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
    public final static SimpleDateFormat dd_MM_yyyy = new SimpleDateFormat("dd/MM/yyyy");

    public static synchronized LocalDateTime valueOf(Date dateToConvert) {
        return dateToConvert.toInstant()
                .atZone(ZoneId.systemDefault())
                .toLocalDateTime();
    }

    public static synchronized LocalDate valueOf2(Date dateToConvert) {
        return dateToConvert.toInstant()
                .atZone(ZoneId.systemDefault())
                .toLocalDate();
    }

    public static synchronized Date valueOf(LocalDate localDateToConvert) {
        return Date.from(localDateToConvert.atStartOfDay().atZone(ZoneId.systemDefault()).toInstant());
    }

    public static synchronized Date localDateTimeToDate(LocalDateTime dateToConvert) {
        return java.sql.Timestamp.valueOf(dateToConvert);
    }

    /**
     * devuelve una fecha con year, month, day, hour, minute y con los
     * segundos siempre en cero.
     *
     * @param year
     * @param month
     * @param day
     * @return
     * @throws ParseException
     */
    public static synchronized Date parseYearMonthDayHourToDate(int year, int month, int day, int hour, int min) throws ParseException {
        return dd_MM_yyyy_HH_mm_SS.parse(day + "/" + month + "/" + year + " " + hour + ":" + min + ":00");
    }

    /**
     * devuelve una fecha con year, month, day con los hour, minute y los
     * segundos siempre en cero.
     *
     * @param year
     * @param month
     * @param day
     * @return
     * @throws ParseException
     */
    public static synchronized Date parseYearMonthDayToDate(int year, int month, int day) throws ParseException {
        return dd_MM_yyyy_HH_mm_SS.parse(day + "/" + month + "/" + year + " " + "00:00:00");
    }

    /**
     * devuelve una fecha con year, month, day con los hour, minute y los
     * segundos siempre en cero.
     *
     * @return
     * @throws ParseException
     */
    public static synchronized Date parseToYearMonthDay(LocalDate localDate) throws ParseException {
        return parseToYearMonthDay(localDate, "00:00:00");
    }

    /**
     * devuelve una fecha con year, month, day con los hour, minute y los
     * segundos segun hh_mm_ss.
     *
     * @return
     * @throws ParseException
     */
    public static synchronized Date parseToYearMonthDay(LocalDate localDate, String hh_mm_ss) throws ParseException {
        int year = localDate.getYear();
        int month = localDate.getMonthValue();
        int day = localDate.getDayOfMonth();
        return dd_MM_yyyy_HH_mm_SS.parse(day + "/" + month + "/" + year + " " + hh_mm_ss);
    }
}
