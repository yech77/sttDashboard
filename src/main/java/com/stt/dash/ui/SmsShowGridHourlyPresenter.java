package com.stt.dash.ui;

import com.stt.dash.app.session.ListGenericBean;
import com.stt.dash.backend.data.AbstractSmsByYearMonth;
import com.stt.dash.backend.data.SmsByYearMonthDayHour;
import com.stt.dash.backend.service.SmsHourService;
import com.vaadin.flow.data.provider.ListDataProvider;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class SmsShowGridHourlyPresenter extends SmsShowGridAbstractPresenter<SmsByYearMonthDayHour> {


    public SmsShowGridHourlyPresenter(SmsHourService smsHourService, int actualYear, int actualMonth, int actualDay, ListGenericBean<String> stringListGenericBean, Viewnable<SmsByYearMonthDayHour> view) {
        super(smsHourService, Arrays.asList(actualYear, actualMonth, actualDay), stringListGenericBean, view);
        List<SmsByYearMonthDayHour> smsHourList = getGroupSmsBy(stringListGenericBean.getList(), Arrays.asList(actualYear, actualMonth, actualDay));
        updateDataProvider(smsHourList);
        updateInView(dataProvider);
    }

    @Override
    public List<SmsByYearMonthDayHour> getGroupSmsBy(List<String> stringList, List<Integer> integerList) {
        return smsHourService.getGroupSmsByYearMonthDayHourMessageType(integerList.get(0), integerList.get(1), integerList.get(2), stringList);
    }
}
