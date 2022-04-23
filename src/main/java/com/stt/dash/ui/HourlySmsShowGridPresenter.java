package com.stt.dash.ui;

import com.stt.dash.app.session.ListGenericBean;
import com.stt.dash.backend.data.SmsByYearMonthDayHour;
import com.stt.dash.backend.service.SmsHourService;

import java.util.Arrays;
import java.util.List;

public class HourlySmsShowGridPresenter extends SmsShowGridPresenter<SmsByYearMonthDayHour> {


    public HourlySmsShowGridPresenter(SmsHourService smsHourService, int actualYear, int actualMonth, int actualDay, ListGenericBean<String> stringListGenericBean, Viewnable<SmsByYearMonthDayHour> view) {
        this(smsHourService, actualYear, actualMonth, actualDay, stringListGenericBean.getList(), view);
    }

    public HourlySmsShowGridPresenter(SmsHourService smsHourService, int actualYear, int actualMonth, int actualDay, List<String> systemidStringList, Viewnable<SmsByYearMonthDayHour> view) {
        super(smsHourService, Arrays.asList(actualYear, actualMonth, actualDay), systemidStringList, view);
        List<SmsByYearMonthDayHour> smsHourList = getGroupSmsBy(systemidStringList, Arrays.asList(actualYear, actualMonth, actualDay));
        updateDataProvider(smsHourList);
        updateInView(dataProvider);
    }

    @Override
    public List<SmsByYearMonthDayHour> getGroupSmsBy(List<String> stringList, List<Integer> integerList) {
        return smsHourService.groupSmsYeMoDaHoTyWhYeMoDaSyIn(integerList.get(0), integerList.get(1), integerList.get(2), stringList);
    }
}
