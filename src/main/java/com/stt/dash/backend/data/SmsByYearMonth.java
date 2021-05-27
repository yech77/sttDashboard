package com.stt.dash.backend.data;

import java.util.StringJoiner;

/**
 * Representa la agrupacionde anio y mes.
 *
 * @since V1.0
 * @author yech77
 */
public class SmsByYearMonth extends AbstractSmsByYearMonth{
    public SmsByYearMonth(long total, int yearSms, int monthSms) {
        super(total, yearSms, monthSms);
    }

    public SmsByYearMonth(long total, int yearSms, int monthSms, String someCode) {
        super(total, yearSms, monthSms, someCode);
    }

    public SmsByYearMonth(long total, int yearSms, int monthSms, String someCode, String messageType) {
        super(total, yearSms, monthSms, someCode, messageType);
    }

    @Override
    public AbstractSmsByYearMonth getObject(int total, int year, int monthLoop, String someCode) {
        return new SmsByYearMonth(total, year, monthLoop, someCode);
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
        return getYearSms() + "" + getMonthSms() + "" + getSomeCode();
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", SmsByYearMonth.class.getSimpleName() + "[", "]")
                .add("total=" + getTotal())
                .add("yearSms=" + getYearSms())
                .add("monthSms=" + getMonthSms())
                .add("someCode='" + getSomeCode() + "'")
                .add("messageType='" + getMessageType() + "'")
                .toString();
    }
}
