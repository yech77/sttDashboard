package com.stt.dash.ui;

import com.stt.dash.app.session.ListGenericBean;
import com.stt.dash.backend.data.AbstractSmsByYearMonth;
import com.stt.dash.backend.data.SmsByYearMonthDayHour;
import com.stt.dash.backend.service.SmsHourService;
import com.vaadin.flow.data.provider.ListDataProvider;
import liquibase.pro.packaged.T;
import liquibase.pro.packaged.Y;

import java.util.ArrayList;
import java.util.List;

public abstract class SmsShowGridAbstractPresenter<T> {
    final Viewnable view;
    ListDataProvider<T> dataProvider = new ListDataProvider<>(new ArrayList<>());
    final SmsHourService smsHourService;

    public SmsShowGridAbstractPresenter(SmsHourService smsHourService, List<Integer> integerList, ListGenericBean<String> stringListGenericBean, Viewnable<T> view) {
        this.view = view;
        this.smsHourService = smsHourService;
    }

    public void updateDataProvider(List<T> smsByYearMonthList) {
        dataProvider.getItems().clear();
        dataProvider.getItems().addAll(smsByYearMonthList);
        dataProvider.refreshAll();
    }

    void updateInView(ListDataProvider<T> dataProvider) {
        view.setGridDataProvider(dataProvider);
        view.updateDownloadButton(dataProvider.getItems());
    }

    public abstract List<T> getGroupSmsBy(List<String> stringList, List<Integer> integerList);

}
