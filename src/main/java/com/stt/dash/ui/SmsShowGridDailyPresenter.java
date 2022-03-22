package com.stt.dash.ui;

import com.stt.dash.app.session.ListGenericBean;
import com.stt.dash.backend.data.AbstractSmsByYearMonth;
import com.stt.dash.backend.data.SmsByYearMonthDay;
import com.stt.dash.backend.service.SmsHourService;
import com.vaadin.flow.data.provider.ListDataProvider;

import java.util.ArrayList;
import java.util.List;

public class SmsShowGridDailyPresenter {
    private final Viewnable view;
    private ListDataProvider<AbstractSmsByYearMonth> dataProvider = new ListDataProvider<>(new ArrayList<>());
    private final SmsHourService smsHourService;

    public SmsShowGridDailyPresenter(SmsHourService smsHourService, int actualYear, int actualMonth, ListGenericBean<String> stringListGenericBean, Viewnable<AbstractSmsByYearMonth> view) {
        this.view = view;
        this.smsHourService = smsHourService;
        List<SmsByYearMonthDay> smsHourList = smsHourService.getGroupSmsByYearMonthDayMessageType(actualYear, actualMonth, stringListGenericBean.getList());
        updateDataProvider(smsHourList);
        updateInView(dataProvider);
    }

    public void updateDataProvider(List<? extends AbstractSmsByYearMonth> smsByYearMonthList) {
        dataProvider.getItems().clear();
        dataProvider.getItems().addAll(smsByYearMonthList);
        dataProvider.refreshAll();
    }

    private void updateInView(ListDataProvider<AbstractSmsByYearMonth> dataProvider) {
        view.setGridDataProvider(dataProvider);
        view.updateDownloadButton(dataProvider.getItems());
    }
}
