package com.stt.dash.ui;

import com.stt.dash.app.session.ListGenericBean;
import com.stt.dash.backend.service.SmsHourService;
import com.vaadin.flow.data.provider.ListDataProvider;

import java.util.ArrayList;
import java.util.List;

public abstract class SmsShowGridPresenter<T> {
    final Viewnable view;
    ListDataProvider<T> dataProvider = new ListDataProvider<>(new ArrayList<>());
    final SmsHourService smsHourService;

    public SmsShowGridPresenter(SmsHourService smsHourService, List<Integer> integerList, List<String> stringListGenericBean, Viewnable<T> view) {
        this.view = view;
        this.smsHourService = smsHourService;
    }

    public SmsShowGridPresenter(SmsHourService smsHourService, Viewnable<T> view) {
        this.view = view;
        this.smsHourService = smsHourService;
    }

    /**
     * Actualizan la data en el dataProvider
     *
     * @param smsByYearMonthList
     */
    public void updateDataProvider(List<T> smsByYearMonthList) {
        dataProvider.getItems().clear();
        dataProvider.getItems().addAll(smsByYearMonthList);
        dataProvider.refreshAll();
    }

    /**
     * @param dataProvider
     */
    void updateInView(ListDataProvider<T> dataProvider) {
        view.setGridDataProvider(dataProvider);
        view.updateDownloadButton(dataProvider.getItems());
    }

    /**
     * Se realiza el llamado al servicio segun quien lo implemente.
     *
     * @param stringList
     * @param integerList
     * @return
     */
    public abstract List<T> getGroupSmsBy(List<String> stringList, List<Integer> integerList);

    /**
     * Se realiza el llamado al servicio segun quien lo implemente.
     *
     * @param stringList
     * @param month
     * @return
     */
    public List<T> getGroupSmsBy(List<String> stringList, Integer month) {
        return null;
    }

    ;

    /**
     * Se realiza el llamado al servicio, que implementa paginacion, segun quien lo implemente.
     *
     * @param stringList
     * @param integerList
     * @return
     */
    public List<T> getGroupSmsBy(List<String> stringList, List<Integer> integerList, int page, int pageSize) {
        return null;
    }

}
