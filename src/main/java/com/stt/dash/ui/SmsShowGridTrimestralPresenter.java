package com.stt.dash.ui;

import com.stt.dash.app.session.ListGenericBean;
import com.stt.dash.backend.data.AbstractSmsByYearMonth;
import com.stt.dash.backend.data.SmsByYearMonth;
import com.stt.dash.backend.service.SmsHourService;
import com.vaadin.flow.data.provider.ListDataProvider;
import liquibase.pro.packaged.T;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class SmsShowGridTrimestralPresenter {
    private final Viewnable view;
    private ListDataProvider<AbstractSmsByYearMonth> dataProvider = new ListDataProvider<>(new ArrayList<>());
    private final SmsHourService smsHourService;

    public SmsShowGridTrimestralPresenter(SmsHourService smsHourService, List<Integer> monthToShowList, ListGenericBean<String> stringListGenericBean, Viewnable<AbstractSmsByYearMonth> view) {
        this.view = view;
        this.smsHourService = smsHourService;
        List<SmsByYearMonth>
                monthToShowDataList = smsHourService.getGroupSmsByYearMonthMessageTypeWhMo(2022, monthToShowList, stringListGenericBean.getList());
        Calendar c = Calendar.getInstance();
        /* Completar MONTH faltante con 0 */
        for (int monthRunner = 1; monthRunner <= 3; monthRunner++) {
            boolean thisHasIt = false;
            for (AbstractSmsByYearMonth smsByYearMonth : monthToShowDataList) {
                /* Si tengo el Month me salgo del ciclo. */
                if (smsByYearMonth.getGroupBy() == monthRunner) {
                    thisHasIt = true;
                    break;
                }
            }
            /* Agregar el MONTH faltante a la respuesta. */
            if (!thisHasIt) {
                /* TODO Descablear year*/
                SmsByYearMonth de = new SmsByYearMonth(0, 2022, monthRunner, "MT");
                monthToShowDataList.add(de);
                de = new SmsByYearMonth(0, 2022, monthRunner, "MO");
                monthToShowDataList.add(de);
            }
        }
        updateDataProvider(monthToShowDataList);
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
