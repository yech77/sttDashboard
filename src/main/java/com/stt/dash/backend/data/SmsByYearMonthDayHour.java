package com.stt.dash.backend.data;

public class SmsByYearMonthDayHour extends SmsByYearMonthDay {
    private int hourSms;
    public SmsByYearMonthDayHour(long total, int yearSms, int monthSms, int daySms, int hourSms) {
        super(total, yearSms, monthSms, daySms);
        this.hourSms = hourSms;
    }

    public SmsByYearMonthDayHour(long total, int yearSms, int monthSms, int daySms, int hourSms, String someCode) {
        super(total, yearSms, monthSms, daySms, someCode);
        this.hourSms = hourSms;
    }

    public int getHourSms() {
        return hourSms;
    }

    public void setHourSms(int hourSms) {
        this.hourSms = hourSms;
    }
    /**
     * Usado para colocar como key en el Map previo a realizar el orden.
     *
     * @return
     */
    @Override
    public String forKey() {
        return getGroupBy() + getDaySms() + getMonthSms() + getYearSms() + getSomeCode();
    }

    @Override
    public int getGroupBy() {
        return getHourSms();
    }

    @Override
    public String toString() {
        return super.toString() + " SmsByYearMonthDayHour{" + "hourSms=" + hourSms + "}";
    }
}
