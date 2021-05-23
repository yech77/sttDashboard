package com.stt.dash.app;

import java.util.HashMap;
import java.util.Map;

public enum OMonths {
    ENERO(1, "Enero"),
    FEBRERO(2, "Febrero"),
    MARZO(3, "Marzo"),
    ABRIL(4, "Abril"),
    MAYO(5, "Mayo"),
    JUNIO(6, "Junio"),
    JULIO(7, "Julio"),
    AGOSTO(8, "Agosto"),
    SEPTIEMBRE(9, "Septiembre"),
    OCTUBRE(10, "Octubre"),
    NOVIEMBRE(11, "Noviembre"),
    DICIEMBRE(12, "Diciembre");

    private final String monthName;
    private final int monthPos;
    private final static Map<Integer, OMonths> map = new HashMap<>(12);

    OMonths(int monthPos, String monthName) {
        this.monthName = monthName;
        this.monthPos = monthPos;
    }

    static {
        for (OMonths months : OMonths.values()) {
            map.put(months.monthPos, months);
        }
    }

    public int getValue() {
        return monthPos;
    }

    public static OMonths valueOf(int monthPod) {
        if(monthPod < 1) {
            monthPod +=12;
        } else if (monthPod > 12) {
            monthPod -=12;
        }
        return map.get(monthPod);
    }

    public String getMonthNameShort() {
        return monthName.substring(0, 3).toUpperCase();
    }

    public String getMonthName() {
        return monthName;
    }

}
