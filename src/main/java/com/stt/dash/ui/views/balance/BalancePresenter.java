package com.stt.dash.ui.views.balance;

import com.stt.dash.app.session.ListGenericBean;
import com.stt.dash.backend.service.SystemIdBalanceWebClientService;
import com.stt.dash.backend.util.ws.SystemIdBalanceOResponse;
import com.vaadin.flow.data.provider.ListDataProvider;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class BalancePresenter {
    private final SystemIdBalanceWebClientService balanceWebClientService;
    private final ListGenericBean<String> stringListGenericBean;
    private final BalanceView view;
    private ListDataProvider<SystemIdBalanceOResponse> dataProvider = new ListDataProvider<>(new ArrayList<>());

    public BalancePresenter(SystemIdBalanceWebClientService balanceWebClientService, ListGenericBean<String> stringListGenericBean, BalanceView view) {
        this.balanceWebClientService = balanceWebClientService;
        this.stringListGenericBean = stringListGenericBean;
        this.view = view;
        updateDataProvider();
        view.setDataProvider(dataProvider);
    }

    public void updateDataProvider() {
        List<SystemIdBalanceOResponse> systemIdBalance1 = balanceWebClientService.findSystemIdBalance(stringListGenericBean.getList());
        addData(systemIdBalance1);
    }

    public void addData(List<SystemIdBalanceOResponse> systemidList) {
        dataProvider.getItems().clear();
        dataProvider.getItems().addAll(systemidList);
        dataProvider.refreshAll();
    }

}
