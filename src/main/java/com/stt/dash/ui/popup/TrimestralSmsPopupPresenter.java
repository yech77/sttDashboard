package com.stt.dash.ui.popup;

import com.stt.dash.backend.data.AbstractSmsByYearMonth;
import com.stt.dash.backend.data.SmsByYearMonth;
import com.stt.dash.backend.service.SmsHourService;
import com.stt.dash.ui.Viewnable;
import com.vaadin.flow.data.provider.ListDataProvider;
import liquibase.pro.packaged.T;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

public class TrimestralSmsPopupPresenter {
    final Viewnable view;
    ListDataProvider<SmsByYearMonth> dataProvider = new ListDataProvider<>(new ArrayList<>());
    final SmsHourService smsHourService;
    List<Integer> monthToShowList;
    final List<String> systemidStringList;

    /**
     * MainDashboard
     *
     * @param smsHourService
     * @param yearSms
     * @param monthToShowList
     * @param systemidStringList
     * @param view
     */
    public TrimestralSmsPopupPresenter(SmsHourService smsHourService, int yearSms, List<Integer> monthToShowList, List<String> systemidStringList, Viewnable<SmsByYearMonth> view) {
        this.view = view;
        this.smsHourService = smsHourService;
        this.monthToShowList = monthToShowList;
        this.systemidStringList = systemidStringList;
        List<SmsByYearMonth> monthToShowDataList = smsHourService.groupSmsMessageTypeByYeMoWhYeMoInSyIn(yearSms, monthToShowList, systemidStringList);
        updateDataProvider(monthToShowDataList);
        updateInView(dataProvider);
    }

    /**
     * MainDashboard
     *
     * @param smsHourService
     * @param yearSms
     * @param monthSms
     * @param systemidStringList
     * @param view
     */
    public TrimestralSmsPopupPresenter(SmsHourService smsHourService, int yearSms, int monthSms, List<String> systemidStringList, Viewnable<SmsByYearMonth> view) {
        this.view = view;
        this.smsHourService = smsHourService;
        this.systemidStringList = systemidStringList;
        List<SmsByYearMonth> monthToShowDataList = smsHourService.groupSmsSyTyByYeMoWhYeMoSyInTyIn(yearSms, monthSms, systemidStringList);
        updateDataProvider(monthToShowDataList);
        updateInView(dataProvider);
    }

    public TrimestralSmsPopupPresenter(SmsHourService smsHourService, int yearSms, int monthSms, List<String> systemidStringList, List<String> messageType, Viewnable<SmsByYearMonth> view) {
        this.view = view;
        this.smsHourService = smsHourService;
        this.systemidStringList = systemidStringList;
        List<SmsByYearMonth> monthToShowDataList = smsHourService.groupSmsSyTyByYeMoWhYeMoSyInTyIn(yearSms, monthSms, messageType, systemidStringList);
        updateDataProvider(monthToShowDataList);
        updateInView(dataProvider);
    }

    public TrimestralSmsPopupPresenter(SmsHourService smsHourService, int yearSms, List<Integer> monthSms, List<String> systemidStringList, List<String> messageType, Viewnable<SmsByYearMonth> view) {
        this.view = view;
        this.smsHourService = smsHourService;
        this.systemidStringList = systemidStringList;
        List<SmsByYearMonth> monthToShowDataList = smsHourService.groupSmsSyTyByYeMoWhYeMoInSyInTyIn(yearSms, monthSms, messageType, systemidStringList);
        updateDataProvider(monthToShowDataList);
        updateInView(dataProvider);
    }

    /**
     * Actualizan la data en el dataProvider
     *
     * @param smsByYearMonthList
     */
    public void updateDataProvider(List<SmsByYearMonth> smsByYearMonthList) {
        dataProvider.getItems().clear();
        dataProvider.getItems().addAll(smsByYearMonthList);
        dataProvider.refreshAll();
    }

    public Collection<SmsByYearMonth> getdataFromProvider() {
        return dataProvider.getItems();
    }

    /**
     * @param dataProvider
     */
    void updateInView(ListDataProvider<SmsByYearMonth> dataProvider) {
        view.setGridDataProvider(dataProvider);
//        view.updateDownloadButton(dataProvider.getItems());
    }
}
