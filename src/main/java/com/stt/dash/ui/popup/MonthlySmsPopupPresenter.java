package com.stt.dash.ui.popup;

import com.stt.dash.app.OMessageType;
import com.stt.dash.backend.data.SmsByYearMonthDay;
import com.stt.dash.backend.service.SmsHourService;
import com.stt.dash.ui.Viewnable;
import com.vaadin.flow.data.provider.ListDataProvider;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;

public class MonthlySmsPopupPresenter {
    final Viewnable view;
    ListDataProvider<SmsByYearMonthDay> dataProvider = new ListDataProvider<>(new ArrayList<>());
    final SmsHourService smsHourService;
    List<Integer> monthToShowList;
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
    public MonthlySmsPopupPresenter(SmsHourService smsHourService, int yearSms, int monthToShow, List<String> systemidStringList, Viewnable<SmsByYearMonthDay> view) {
        this.view = view;
        this.smsHourService = smsHourService;
//        this.monthToShowList = monthToShowList;
        this.systemidStringList = systemidStringList;
        List<SmsByYearMonthDay> monthToShowDataList = smsHourService.groupSmsByYeMoDaTyWhYeMoSyIn(yearSms, monthToShow, systemidStringList);
        updateDataProvider(monthToShowDataList);
        updateInView(dataProvider);
    }

    /**
     * MainDashboard: Point
     *
     * @param smsHourService
     * @param yearSms
     * @param monthToShow
     * @param selectedDay
     * @param systemidStringList
     * @param view
     */
    public MonthlySmsPopupPresenter(SmsHourService smsHourService, int yearSms, int monthToShow, int selectedDay, List<String> systemidStringList, Viewnable<SmsByYearMonthDay> view) {
        this.view = view;
        this.smsHourService = smsHourService;
        this.systemidStringList = systemidStringList;
        List<SmsByYearMonthDay> monthToShowDataList = smsHourService.groupSmsMessageTypeByYeMoDaWhYeMoDaSyIn(yearSms, monthToShow, selectedDay, systemidStringList);
        updateDataProvider(monthToShowDataList);
        updateInView(dataProvider);
    }

    /**
     * Client: Chart
     *
     * @param smsHourService
     * @param yearSms
     * @param monthToShow
     * @param systemidStringList
     * @param view
     */
    public MonthlySmsPopupPresenter(SmsHourService smsHourService, int yearSms, int monthToShow, List<String> systemidStringList, Set<OMessageType> messageTypeSet, Viewnable<SmsByYearMonthDay> view) {
        this.view = view;
        this.smsHourService = smsHourService;
        this.systemidStringList = systemidStringList;
        List<SmsByYearMonthDay> monthToShowDataList = smsHourService.groupSmsByYeMoDaSyWhYeMoSyIn_TyIn(yearSms, monthToShow, messageTypeSet, systemidStringList);
        updateDataProvider(monthToShowDataList);
        updateInView(dataProvider);
    }

    /**
     * Client: Point
     *
     * @param smsHourService
     * @param yearSms
     * @param monthToShow
     * @param systemidStringList
     * @param view
     */
    public MonthlySmsPopupPresenter(SmsHourService smsHourService, int yearSms, int monthToShow, int dayToShow, List<String> systemidStringList, Set<OMessageType> messageTypeSet, Viewnable<SmsByYearMonthDay> view) {
        this.view = view;
        this.smsHourService = smsHourService;
        this.systemidStringList = systemidStringList;
        List<SmsByYearMonthDay> monthToShowDataList = smsHourService.groupSmsByYeMoDaSyWhYeMoSyIn_TyIn(yearSms, monthToShow, dayToShow, messageTypeSet, systemidStringList);
        updateDataProvider(monthToShowDataList);
        updateInView(dataProvider);
    }

    /**
     * Client: Point
     *
     * @param smsHourService
     * @param yearSms
     * @param monthToShow
     * @param systemidStringList
     * @param view
     */
    public MonthlySmsPopupPresenter(SmsHourService smsHourService, int yearSms, int monthToShow, List<String> carrierStringList, List<String> messageTypeStringList, List<String> systemidStringList, Viewnable<SmsByYearMonthDay> view) {
        this.view = view;
        this.smsHourService = smsHourService;
        this.systemidStringList = systemidStringList;
        List<SmsByYearMonthDay> smsByCarrierAndTypeList = smsHourService.groupSmsCarrierMessageTypeByYeMoDaWhYeMoSyIn_CarrierTyIn(yearSms,
                monthToShow,
                carrierStringList,
                messageTypeStringList,
                systemidStringList);
        updateDataProvider(smsByCarrierAndTypeList);
        updateInView(dataProvider);
    }

    /**
     * Actualizan la data en el dataProvider
     *
     * @param smsByYearMonthList
     */
    public void updateDataProvider(List<SmsByYearMonthDay> smsByYearMonthList) {
        dataProvider.getItems().clear();
        dataProvider.getItems().addAll(smsByYearMonthList);
        dataProvider.refreshAll();
    }

    public Collection<SmsByYearMonthDay> getdataFromProvider() {
        return dataProvider.getItems();
    }

    /**
     * @param dataProvider
     */
    void updateInView(ListDataProvider<SmsByYearMonthDay> dataProvider) {
        view.setGridDataProvider(dataProvider);
    }
}
