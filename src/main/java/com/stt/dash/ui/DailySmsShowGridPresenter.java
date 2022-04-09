package com.stt.dash.ui;

import com.stt.dash.app.session.ListGenericBean;
import com.stt.dash.backend.data.SmsByYearMonthDay;
import com.stt.dash.backend.service.SmsHourService;

import java.util.List;

/**
 * Presenta la data por dia, del mes seleccionado.
 */
public class DailySmsShowGridPresenter extends SmsShowGridPresenter<SmsByYearMonthDay> {

    public DailySmsShowGridPresenter(SmsHourService smsHourService, List<Integer> integerList, ListGenericBean<String> stringListGenericBean, Viewnable<SmsByYearMonthDay> view) {
        this(smsHourService, integerList, stringListGenericBean.getList(), view);
    }

    /**
     * Cuando se desea que la agrupacion se Day-messageType
     *
     * @param smsHourService
     * @param integerList
     * @param systemidStringList
     * @param view
     */
    public DailySmsShowGridPresenter(SmsHourService smsHourService, List<Integer> integerList, List<String> systemidStringList, Viewnable<SmsByYearMonthDay> view) {
        super(smsHourService, integerList, systemidStringList, view);
        List<SmsByYearMonthDay> smsHourList = getGroupSmsBy(systemidStringList, integerList);
        updateDataProvider(smsHourList);
        updateInView(dataProvider);
    }

    /**
     * Cuando se desea que la agrupacion se Day-systemid-messageType
     *
     * @param smsHourService
     * @param integerList
     * @param systemidStringList
     * @param messageType
     * @param view
     */
    public DailySmsShowGridPresenter(SmsHourService smsHourService, List<Integer> integerList, List<String> systemidStringList, List<String> messageType, Viewnable<SmsByYearMonthDay> view) {
        super(smsHourService, view);
        List<SmsByYearMonthDay> smsHourList = getGroupSmsBy(integerList, messageType, systemidStringList);
        updateDataProvider(smsHourList);
        updateInView(dataProvider);
    }

    @Override
    public List<SmsByYearMonthDay> getGroupSmsBy(List<String> stringList, List<Integer> integerList) {
        return smsHourService.groupByYearMonthDayMessageTypeWhereYearAndMonth(
                integerList.get(0),
                integerList.get(1),
                stringList);
    }

    public List<SmsByYearMonthDay> getGroupSmsBy(List<Integer> integerList, List<String> messageTypeList, List<String> systemidStringList) {
        return smsHourService.groupYeMoSyTyWhYeMoInSyInTyIn(
                integerList.get(0),
                integerList.get(1),
                integerList.get(2),
                messageTypeList,
                systemidStringList);
    }
}
