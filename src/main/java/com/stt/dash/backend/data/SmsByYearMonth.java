package com.stt.dash.backend.data;

import java.util.StringJoiner;

/**
 * Representa la agrupacionde anio y mes.
 *
 * @since V1.0
 * @author yech77
 */
public class SmsByYearMonth {

    private long total;
    private int yearSms;
    private int monthSms;
    private String someCode;
    private String messageType;

    public SmsByYearMonth(long total, int yearSms, int monthSms) {
        this.total = total;
        this.yearSms = yearSms;
        this.monthSms = monthSms;
        this.someCode = null;
        this.messageType="";
    }

    public SmsByYearMonth(long total, int yearSms, int monthSms, String someCode) {
        this(total, yearSms, monthSms);
        this.someCode = someCode;
    }
    public SmsByYearMonth(long total, int yearSms, int monthSms, String someCode, String messageType) {
        this(total, yearSms, monthSms, someCode);
        this.messageType=messageType;
    }

    public int getYearSms() {
        return yearSms;
    }

    public void setYearSms(int yearSms) {
        this.yearSms = yearSms;
    }

    public int getMonthSms() {
        return monthSms;
    }

    public void setMonthSms(int monthSms) {
        this.monthSms = monthSms;
    }

    public long getTotal() {
        return total;
    }

    public void setTotal(long total) {
        this.total = total;
    }

    public String getSomeCode() {
        return someCode;
    }

    public String getMessageType() {
        return messageType;
    }

    public void setMessageType(String messageType) {
        this.messageType = messageType;
    }

    public void setSomeCode(String someCode) {
        this.someCode = someCode;
    }

    /**
     * Devuelve el valor de lo que se esta agrupando. En ese caso el Mes
     *
     * @return
     */
    public int getGroupBy() {
        return getMonthSms();
    }

    /**
     * Usado para colocar como key en el Map previo a realizar el orden.
     * @return
     */
    public String forKey() {
        return yearSms + "" + monthSms + "" + someCode;
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", SmsByYearMonth.class.getSimpleName() + "[", "]")
                .add("total=" + total)
                .add("yearSms=" + yearSms)
                .add("monthSms=" + monthSms)
                .add("someCode='" + someCode + "'")
                .add("messageType='" + messageType + "'")
                .toString();
    }
}
