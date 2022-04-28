package com.stt.dash.ui.popup;

import com.stt.dash.app.OMessageType;
import com.stt.dash.backend.data.SmsByYearMonth;
import com.stt.dash.backend.data.SmsByYearMonthDay;
import com.stt.dash.backend.data.entity.Carrier;
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
     * MainDashboard
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
     * MainDashboard
     *
     * @param smsHourService
     * @param yearSms
     * @param monthSms
     * @param systemidStringList
     * @param view
     */
//    public MonthlySmsPopupPresenter(SmsHourService smsHourService, int yearSms, int monthSms, int DaySms, List<String> systemidStringList, Viewnable<SmsByYearMonth> view) {
//        this.view = view;
//        this.smsHourService = smsHourService;
//        this.systemidStringList = systemidStringList;
//        List<SmsByYearMonthDay> monthToShowDataList = smsHourService.groupSmsSyTyByYeMoWhYeMoSyInTyIn(yearSms, monthSms, systemidStringList);
//        updateDataProvider(monthToShowDataList);
//        updateInView(dataProvider);
//    }
//
//    /**
//     * Client
//     *
//     * @param smsHourService
//     * @param yearSms
//     * @param monthSms
//     * @param systemidStringList
//     * @param messageType
//     * @param view
//     */
//    public MonthlySmsPopupPresenter(SmsHourService smsHourService, int yearSms, int monthSms, List<String> systemidStringList, List<String> messageType, Viewnable<SmsByYearMonth> view) {
//        this.view = view;
//        this.smsHourService = smsHourService;
//        this.systemidStringList = systemidStringList;
//        List<SmsByYearMonthDay> monthToShowDataList = smsHourService.groupSmsSyTyByYeMoWhYeMoSyInTyIn(yearSms, monthSms, messageType, systemidStringList);
//        updateDataProvider(monthToShowDataList);
//        updateInView(dataProvider);
//    }
//
//    /**
//     * Client
//     *
//     * @param smsHourService
//     * @param yearSms
//     * @param monthSms
//     * @param systemidStringList
//     * @param messageType
//     * @param view
//     */
//    public MonthlySmsPopupPresenter(SmsHourService smsHourService, int yearSms, List<Integer> monthSms, List<String> systemidStringList, List<String> messageType, Viewnable<SmsByYearMonth> view) {
//        this.view = view;
//        this.smsHourService = smsHourService;
//        this.systemidStringList = systemidStringList;
//        List<SmsByYearMonthDay> monthToShowDataList = smsHourService.groupSmsSyTyByYeMoWhYeMoInSyInTyIn(yearSms, monthSms, messageType, systemidStringList);
//        updateDataProvider(monthToShowDataList);
//        updateInView(dataProvider);
//    }
//
//    /**
//     * Carrier
//     *
//     * @param smsHourService
//     * @param actualYear
//     * @param monthToShowList
//     * @param systemidList
//     * @param messageTypeSet
//     * @param carrierSet
//     * @param view
//     */
//    public MonthlySmsPopupPresenter(SmsHourService smsHourService, int actualYear, List<Integer> monthToShowList, List<String> systemidList, Set<OMessageType> messageTypeSet, Set<Carrier> carrierSet, Viewnable<SmsByYearMonth> view) {
//        this.view = view;
//        this.smsHourService = smsHourService;
////        this.systemidStringList = systemidStringList;
//        List<SmsByYearMonthDay> monthToShowDataList = smsHourService.groupSmsCarrierAndMessageTypeByYeMoWhYeMoSyIn_CarrierInTyIn(
//                actualYear,
//                monthToShowList,
//                carrierSet,
//                messageTypeSet,
//                systemidList);
//        updateDataProvider(monthToShowDataList);
//        updateInView(dataProvider);
//    }
//
//    public MonthlySmsPopupPresenter(SmsHourService smsHourService, int actualYear, int actualMonth, List<String> systemidList, Set<OMessageType> messageTypeSet, Set<Carrier> carrierSet, Viewnable<SmsByYearMonth> view) {
//        this.view = view;
//        this.smsHourService = smsHourService;
////        this.systemidStringList = systemidStringList;
//        List<SmsByYearMonthDay> monthToShowDataList = smsHourService.groupSmsCarrierAndMessageTypeByYeMoWhYeMoSyIn_CarrierInTyIn(
//                actualYear,
//                actualMonth,
//                carrierSet,
//                messageTypeSet,
//                systemidList);
//        updateDataProvider(monthToShowDataList);
//        updateInView(dataProvider);
//    }

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
//        view.updateDownloadButton(dataProvider.getItems());
    }
}
