package com.stt.dash.ui;

import com.stt.dash.app.session.ListGenericBean;
import com.stt.dash.backend.data.SmsByYearMonthDay;
import com.stt.dash.backend.service.SmsHourService;

import java.util.List;

public class DailySmsShowGridPresenter extends SmsShowGridPresenter<SmsByYearMonthDay> {

    public DailySmsShowGridPresenter(SmsHourService smsHourService, List<Integer> integerList, ListGenericBean<String> stringListGenericBean, Viewnable<SmsByYearMonthDay> view) {
        super(smsHourService, integerList, stringListGenericBean, view);
        List<SmsByYearMonthDay> smsHourList = getGroupSmsBy(stringListGenericBean.getList(), integerList);
        updateDataProvider(smsHourList);
        updateInView(dataProvider);
    }

    @Override
    public List<SmsByYearMonthDay> getGroupSmsBy(List<String> stringList, List<Integer> integerList) {
        return smsHourService.getGroupSmsByYearMonthDayMessageType(
                integerList.get(0),
                integerList.get(1),
                stringList);
    }
}
