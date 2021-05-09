package com.stt.dash.backend.data;

public class SmsByYearMonthDay extends SmsByYearMonth{

    private int daySms;

    public SmsByYearMonthDay(long total, int yearSms, int monthSms, int daySms) {
        super(total, yearSms, monthSms);
        this.daySms = daySms;
    }

    public SmsByYearMonthDay(long total, int yearSms, int monthSms, int daySms, String someCode) {
        super(total, yearSms, monthSms, someCode);
        this.daySms = daySms;
    }

    @Override
    public int getGroupBy() {
        return getDaySms();
    }

    public int getDaySms() {
        return daySms;
    }

    public void setDaySms(int daySms) {
        this.daySms = daySms;
    }

    /**
     * Usado para colocar como key en el Map previo a realizar el orden.
     *
     * @return
     */
    @Override
    public String forKey() {
        return getGroupBy() + getMonthSms() + getYearSms() + getSomeCode();
    }

    @Override
    public String toString() {
        return super.toString() + " SmsByYearMonthDay{" + "daySms=" + daySms + "}";
    }

}
