package com.stt.dash.ui;

import com.stt.dash.backend.data.OUserSession;
import com.stt.dash.backend.data.entity.SystemId;
import com.stt.dash.backend.data.entity.sms.AbstractSMS;
import com.stt.dash.backend.service.AbstractSmsService;
import com.vaadin.flow.data.provider.ListDataProvider;
import org.springframework.data.domain.Page;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class SmsShowPresenter {
    private final AbstractSmsService service;
    private final List<String> ouser_session;
    private final SmsShowView view;
    private ListDataProvider<AbstractSMS> dataProvider = new ListDataProvider<>(new ArrayList<>());
    private int totPages;

    public SmsShowPresenter(AbstractSmsService service,
                            List<String> ouser_session,
                            SmsShowView view) {
        this.service = service;
        this.ouser_session = ouser_session;
        this.view = view;
        view.setGridDataProvider(dataProvider);
    }

    public void updateDataProvider(LocalDate dateOne,
                                   LocalDate dateTwo,
                                   int actualpage,
                                   int itemsPerPage) {
        if (dateTwo.isBefore(dateOne)) {
            /* TODO: mensaje de error de fechas */
            return;
        }
        Page<AbstractSMS> smsPage = service.findBySystemIdIn(
                dateOne,
                dateTwo,
                ouser_session,
                actualpage,
                itemsPerPage);
        List<AbstractSMS> objects = smsPage.getContent() != null ? smsPage.getContent() : new ArrayList<>();
        dataProvider.getItems().clear();
        dataProvider.getItems().addAll(objects);
        dataProvider.refreshAll();
        totPages = smsPage.getTotalPages();
        /* Upload Button */
        smsPage = service.findBySystemIdIn(
                dateOne,
                dateTwo,
                ouser_session,
                0,
                5000000);
        view.updateDownloadButton(smsPage.getContent());
    }

    public void updateDataProviderPagin(LocalDate dateOne,
                                        LocalDate dateTwo,
                                        int actualpage,
                                        int itemsPerPage) {
        updateDataProvider(dateOne, dateTwo, actualpage, itemsPerPage);
        view.updateTotalPage(totPages);
    }

    private List<AbstractSMS> obtainAbstractOf(Page<? extends AbstractSMS> l) {
        return l == null ? new ArrayList<>() : new ArrayList<>(l.getContent());
    }

    private List<String> getSystemIdString(List<SystemId> s) {
        List<String> l = new ArrayList<>(s.size());
        for (SystemId oSystemIdSession : s) {
            l.add(oSystemIdSession.getSystemId());
        }
        return l;
    }
}
