package com.stt.dash.ui;

import com.stt.dash.app.session.ListGenericBean;
import com.stt.dash.backend.data.AbstractSmsByYearMonth;
import com.stt.dash.backend.data.SmsByYearMonth;
import com.stt.dash.backend.data.SmsByYearMonthDay;
import com.stt.dash.backend.data.SmsByYearMonthDayHour;
import com.stt.dash.backend.data.entity.SystemId;
import com.stt.dash.backend.data.entity.sms.AbstractSMS;
import com.stt.dash.backend.service.SmsHourService;
import com.vaadin.flow.data.provider.ListDataProvider;
import org.springframework.data.domain.Page;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.stream.Collectors;

public class SmsShowGridPresenter {
    private final SmsShowGridView view;
    private ListDataProvider<AbstractSmsByYearMonth> dataProvider = new ListDataProvider<>(new ArrayList<>());
    private final SmsHourService smsHourService;

    public SmsShowGridPresenter(SmsHourService smsHourService, List<Integer> monthToShowList, ListGenericBean<String> stringListGenericBean, SmsShowGridView view) {
        this.view = view;
        this.smsHourService = smsHourService;
        List<SmsByYearMonth>
                monthToShowDataList = smsHourService.getGroupSmsByYearMonthMessageTypeWhMo(2022, monthToShowList, stringListGenericBean.getList());
        Calendar c = Calendar.getInstance();
        /* Completar HOUR faltantes con 0 */
        for (int monthRunner = 1; monthRunner <= 3; monthRunner++) {
            boolean thisHasIt = false;
            for (AbstractSmsByYearMonth smsByYearMonth : monthToShowDataList) {
                /* Si tengo el Month me salgo del ciclo. */
                if (smsByYearMonth.getGroupBy() == monthRunner) {
                    thisHasIt = true;
                    break;
                }
            }
            /* Agregar el HOUR faltante a la respuesta. */
            if (!thisHasIt) {
                /* TODO Descablear year*/
                SmsByYearMonth de = new SmsByYearMonth(0, 2022, monthRunner, "MT");
                monthToShowDataList.add(de);
                de = new SmsByYearMonth(0, 2022, monthRunner, "MO");
                monthToShowDataList.add(de);
            }
        }
        updateDataProvider(monthToShowDataList);
    }

    public void updateDataProvider(List<SmsByYearMonth> smsByYearMonthList) {
        dataProvider.getItems().clear();
        dataProvider.getItems().addAll(smsByYearMonthList);
        dataProvider.refreshAll();
        view.setGridDataProvider(dataProvider);
    }
}
