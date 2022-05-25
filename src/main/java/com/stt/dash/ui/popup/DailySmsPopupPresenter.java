package com.stt.dash.ui.popup;

import com.stt.dash.app.OMessageType;
import com.stt.dash.backend.data.SmsByYearMonthDay;
import com.stt.dash.backend.data.SmsByYearMonthDayHour;
import com.stt.dash.backend.data.entity.Carrier;
import com.stt.dash.backend.service.SmsHourService;
import com.stt.dash.ui.Viewnable;
import com.vaadin.flow.data.provider.ListDataProvider;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;

public class DailySmsPopupPresenter {
    final Viewnable view;
    ListDataProvider<SmsByYearMonthDayHour> dataProvider = new ListDataProvider<>(new ArrayList<>());
    final SmsHourService smsHourService;
    List<String> systemidStringList;

    /**
     * MainDashboard: Chart.
     *
     * @param smsHourService
     * @param yearSms
     * @param monthToShow
     * @param systemidStringList
     * @param view
     */
    public DailySmsPopupPresenter(SmsHourService smsHourService, int yearSms, int monthToShow, int dayToShow, List<String> systemidStringList, Viewnable<SmsByYearMonthDayHour> view) {
        this.view = view;
        this.smsHourService = smsHourService;
//        this.monthToShowList = monthToShowList;
        this.systemidStringList = systemidStringList;

        List<SmsByYearMonthDayHour> monthToShowDataList = smsHourService.groupSmsYeMoDaHoTyWhYeMoDaSyIn(yearSms, monthToShow, dayToShow, systemidStringList);
        updateDataProvider(monthToShowDataList);
        updateInView(dataProvider);
    }

    public DailySmsPopupPresenter(SmsHourService smsHourService, int yearSms, int monthToShow, int dayToShow, Viewnable<SmsByYearMonthDayHour> view, List<String> systemidStringList) {
        this.view = view;
        this.smsHourService = smsHourService;
        this.systemidStringList = systemidStringList;
        List<SmsByYearMonthDayHour> monthToShowDataList = smsHourService.groupSmsSystemidMessageTypeByYeMoDaHoWhYeMoDaSyIn(yearSms, monthToShow, dayToShow, systemidStringList);
        updateDataProvider(monthToShowDataList);
        updateInView(dataProvider);
    }

    /**
     * MainDashboard: Chart.
     *
     * @param smsHourService
     * @param yearSms
     * @param monthToShow
     * @param systemidStringList
     * @param view
     */

    public DailySmsPopupPresenter(SmsHourService smsHourService, int yearSms, int monthToShow, int dayToShow, int hourToShow, List<String> systemidStringList, Viewnable<SmsByYearMonthDayHour> view) {
        this.view = view;
        this.smsHourService = smsHourService;
//        this.monthToShowList = monthToShowList;
        this.systemidStringList = systemidStringList;

        List<SmsByYearMonthDayHour> monthToShowDataList = smsHourService.groupSmsYeMoDaHoTyWhYeMoDaSyIn(yearSms, monthToShow, dayToShow, hourToShow, systemidStringList);
        updateDataProvider(monthToShowDataList);
        updateInView(dataProvider);
    }

    /**
     * Cliente
     *
     * @param smsHourService
     * @param yearSms
     * @param monthToShow
     * @param dayToShow
     * @param hourToShow
     * @param view
     * @param systemidStringList
     */

    public DailySmsPopupPresenter(SmsHourService smsHourService, int yearSms, int monthToShow, int dayToShow, int hourToShow, Viewnable<SmsByYearMonthDayHour> view, List<String> systemidStringList) {
        this.view = view;
        this.smsHourService = smsHourService;
//        this.monthToShowList = monthToShowList;
        this.systemidStringList = systemidStringList;

        List<SmsByYearMonthDayHour> monthToShowDataList = smsHourService.groupSmsSystemidMessageTypeByYeMoDaHoWhYeMoDaHoSyIn(yearSms, monthToShow, dayToShow, hourToShow, systemidStringList);
        updateDataProvider(monthToShowDataList);
        updateInView(dataProvider);
    }

    /**
     * Operadora: Chart
     *
     * @param smsHourService
     * @param yearSms
     * @param monthToShow
     * @param dayToShow
     * @param systemidStringList
     * @param view
     */
    public DailySmsPopupPresenter(SmsHourService smsHourService, int yearSms, int monthToShow, int dayToShow, List<String> carrierList, List<String> messageTypeList, List<String> systemidStringList, Viewnable<SmsByYearMonthDayHour> view) {
        this.view = view;
        this.smsHourService = smsHourService;
        this.systemidStringList = systemidStringList;
        List<SmsByYearMonthDayHour> monthToShowDataList = smsHourService.groupSmsCarrierAndMessageTypeByYeMoDaWhYeMoDaSyIn_CarrierInTyIn(yearSms, monthToShow, dayToShow, carrierList, messageTypeList, systemidStringList);
        updateDataProvider(monthToShowDataList);
        updateInView(dataProvider);
    }

    /**
     * Operadora: Chart
     *
     * @param smsHourService
     * @param yearSms
     * @param monthToShow
     * @param dayToShow
     * @param systemidStringList
     * @param view
     */
    public DailySmsPopupPresenter(SmsHourService smsHourService, int yearSms, int monthToShow, int dayToShow, int hourToShow, List<String> carrierList, List<String> messageTypeList, List<String> systemidStringList, Viewnable<SmsByYearMonthDayHour> view) {
        this.view = view;
        this.smsHourService = smsHourService;
        this.systemidStringList = systemidStringList;
        List<SmsByYearMonthDayHour> monthToShowDataList = smsHourService.groupSmsCarrierAndMessageTypeByYeMoDaWhYeMoDaSyIn_CarrierInTyIn(yearSms, monthToShow, dayToShow, hourToShow, carrierList, messageTypeList, systemidStringList);
        updateDataProvider(monthToShowDataList);
        updateInView(dataProvider);
    }


    /**
     * Actualizan la data en el dataProvider
     *
     * @param smsByYearMonthList
     */
    public void updateDataProvider(List<SmsByYearMonthDayHour> smsByYearMonthList) {
        dataProvider.getItems().clear();
        dataProvider.getItems().addAll(smsByYearMonthList);
        dataProvider.refreshAll();
    }

    public Collection<SmsByYearMonthDayHour> getdataFromProvider() {
        return dataProvider.getItems();
    }

    /**
     * @param dataProvider
     */
    void updateInView(ListDataProvider<SmsByYearMonthDayHour> dataProvider) {
        view.setGridDataProvider(dataProvider);
//        view.updateDownloadButton(dataProvider.getItems());
    }
}