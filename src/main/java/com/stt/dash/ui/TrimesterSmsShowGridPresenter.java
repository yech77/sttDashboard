package com.stt.dash.ui;

import com.stt.dash.app.session.ListGenericBean;
import com.stt.dash.backend.data.AbstractSmsByYearMonth;
import com.stt.dash.backend.data.SmsByYearMonth;
import com.stt.dash.backend.service.SmsHourService;

import java.util.Calendar;
import java.util.List;

public class TrimesterSmsShowGridPresenter extends SmsShowGridPresenter<SmsByYearMonth> {

    public TrimesterSmsShowGridPresenter(SmsHourService smsHourService, List<Integer> monthToShowList, ListGenericBean<String> stringListGenericBean, Viewnable<SmsByYearMonth> view) {
        super(smsHourService, monthToShowList, stringListGenericBean, view);
        List<SmsByYearMonth>
                monthToShowDataList = getGroupSmsBy(stringListGenericBean.getList(), monthToShowList);
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

    @Override
    public List<SmsByYearMonth> getGroupSmsBy(List<String> stringList, List<Integer> integerList) {
        return smsHourService.getGroupSmsByYearMonthMessageTypeWhMo(2022, integerList, stringList);
    }
}
