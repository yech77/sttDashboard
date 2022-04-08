package com.stt.dash.ui;

import com.stt.dash.app.session.ListGenericBean;
import com.stt.dash.backend.data.AbstractSmsByYearMonth;
import com.stt.dash.backend.data.SmsByYearMonth;
import com.stt.dash.backend.service.SmsHourService;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.Optional;

public class MonthlySmsShowGridPresenter extends SmsShowGridPresenter<SmsByYearMonth> {
    private List<String> messageType = new ArrayList<>();

    public MonthlySmsShowGridPresenter(SmsHourService smsHourService, List<Integer> monthToShowList, List<String> stringListGenericBean, Viewnable<SmsByYearMonth> view) {
        super(smsHourService, monthToShowList, stringListGenericBean, view);
        List<SmsByYearMonth>
                monthToShowDataList = getGroupSmsBy(stringListGenericBean, monthToShowList);
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

    public MonthlySmsShowGridPresenter(SmsHourService smsHourService, Integer monthToShow, List<String> stringListGenericBean, List<String> messageType, Viewnable<SmsByYearMonth> view) {
        super(smsHourService, view);
        this.messageType = messageType;
        List<SmsByYearMonth>
                monthToShowDataList = getGroupSmsBy(stringListGenericBean, monthToShow);
        /* Completar SystemID con 0 */
        messageType.forEach(smstype -> {
            stringListGenericBean.forEach(systemid -> {
                        /* Recorro la lista por cada tipo de mensaje */
                        Optional<SmsByYearMonth> first = monthToShowDataList
                                .stream()
                                .filter(sms -> {
                                    return sms.getSomeCode().equalsIgnoreCase(systemid) &&
                                            sms.getMessageType().equalsIgnoreCase(smstype);
                                })
                                .findFirst();
                        /* si la combinacion systemid y tipo de mensaje no existe se crea con 0 */
                        if (!first.isPresent()) {
                            monthToShowDataList.add(new SmsByYearMonth(0, 2022, monthToShow, systemid, smstype));
                        }
                    }
            );
        });
        updateDataProvider(monthToShowDataList);
        updateInView(dataProvider);
    }

    @Override
    public List<SmsByYearMonth> getGroupSmsBy(List<String> systemidList, List<Integer> monthList) {
        return smsHourService.getGroupSmsByYearMonthMessageTypeWhMo(2022, monthList, systemidList);
    }

    @Override
    public List<SmsByYearMonth> getGroupSmsBy(List<String> stringList, Integer month) {
        return smsHourService.groupYeMoSyTyWhYeMoInSyInTyIn(2022, month, messageType, stringList);
    }
}
