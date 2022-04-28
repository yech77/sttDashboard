package com.stt.dash.ui.popup;

import com.stt.dash.backend.data.SmsByYearMonthDay;
import com.stt.dash.backend.data.SmsByYearMonthDayHour;
import com.stt.dash.backend.service.SmsHourService;
import com.stt.dash.ui.Viewnable;
import com.vaadin.flow.data.provider.ListDataProvider;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

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
