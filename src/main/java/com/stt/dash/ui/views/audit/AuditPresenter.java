package com.stt.dash.ui.views.audit;

import com.stt.dash.backend.data.entity.ODashAuditEvent;
import com.stt.dash.backend.data.entity.SystemId;
import com.stt.dash.backend.data.entity.sms.AbstractSMS;
import com.stt.dash.backend.service.ODashAuditEventService;
import com.stt.dash.ui.utils.ODateUitls;
import com.vaadin.flow.data.provider.ListDataProvider;
import org.springframework.data.domain.Page;

import java.text.ParseException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class AuditPresenter {
    private final ODashAuditEventService service;
    private final AuditViewV2 view;
    private ListDataProvider<ODashAuditEvent> dataProvider = new ListDataProvider<>(new ArrayList<>());
    private int totPages;

    public AuditPresenter(ODashAuditEventService service,
                          AuditViewV2 view) {
        this.service = service;
        this.view = view;
        view.setGridDataProvider(dataProvider);
    }

    public void updateDataProvider(LocalDate firsDate, LocalDate secondDate,
                                   int actualpage,
                                   int itemsPerPage) {
        if (secondDate.isBefore(firsDate)) {
            /* TODO: mensaje de error de fechas */
            return;
        }
        try {
            Date one = ODateUitls.parseToYearMonthDay(firsDate);
            Date two = ODateUitls.parseToYearMonthDay(secondDate.plusDays(1));
            Page<ODashAuditEvent> eventPage = service.findAll(one, two, actualpage, itemsPerPage);
            List<ODashAuditEvent> objects = eventPage.getContent() != null ? eventPage.getContent() : new ArrayList<>();
            Page<ODashAuditEvent> limitedList = service.findAll(one, two, 0, 20000);
            addData(objects, limitedList.getContent(), eventPage);
        } catch (ParseException e) {
            /*TODO: mensaje de error de fechas */
            return;
        }
    }

    public void updateDataProvider(LocalDate firsDate, LocalDate secondDate, List<String> userChildren, ODashAuditEvent.OEVENT_TYPE event,
                                   int actualpage,
                                   int itemsPerPage) {
        if (secondDate.isBefore(firsDate)) {
            /* TODO: mensaje de error de fechas */
            return;
        }
        try {
            Date one = ODateUitls.parseToYearMonthDay(firsDate);
            Date two = ODateUitls.parseToYearMonthDay(secondDate.plusDays(1));
            Page<ODashAuditEvent> eventPage = service.findAll(userChildren, event, one, two, actualpage, itemsPerPage);
            List<ODashAuditEvent> objects = eventPage.getContent() != null ? eventPage.getContent() : new ArrayList<>();
            Page<ODashAuditEvent> limitedList = service.findAll(userChildren, event, one, two, 0, 20000);
            addData(objects, limitedList.getContent(), eventPage);
        } catch (ParseException e) {
            /*TODO: mensaje de error de fechas */
            return;
        }
    }

    public void updateDataProvider(LocalDate firsDate, LocalDate secondDate, String email,
                                   int actualpage,
                                   int itemsPerPage) {
        if (secondDate.isBefore(firsDate)) {
            /* TODO: mensaje de error de fechas */
            return;
        }
        try {
            Date one = ODateUitls.parseToYearMonthDay(firsDate);
            Date two = ODateUitls.parseToYearMonthDay(secondDate.plusDays(1));
            Page<ODashAuditEvent> eventPage = service.findAll(email, one, two, actualpage, itemsPerPage);
            List<ODashAuditEvent> objects = eventPage.getContent() != null ? eventPage.getContent() : new ArrayList<>();
            Page<ODashAuditEvent> limitedList = service.findAll(email, one, two, 0, 20000);
            addData(objects, limitedList.getContent(), eventPage);
        } catch (ParseException e) {
            /*TODO: mensaje de error de fechas */
            return;
        }
    }

    public void updateDataProvider(LocalDate firsDate, LocalDate secondDate, String email, ODashAuditEvent.OEVENT_TYPE event,
                                   int actualpage,
                                   int itemsPerPage) {
        if (secondDate.isBefore(firsDate)) {
            /* TODO: mensaje de error de fechas */
            return;
        }
        try {
            Date one = ODateUitls.parseToYearMonthDay(firsDate);
            Date two = ODateUitls.parseToYearMonthDay(secondDate.plusDays(1));
            Page<ODashAuditEvent> eventPage = service.findAll(email, event, one, two, actualpage, itemsPerPage);
            List<ODashAuditEvent> objects = eventPage.getContent() != null ? eventPage.getContent() : new ArrayList<>();
            Page<ODashAuditEvent> limitedList = service.findAll(email, one, two, 0, 20000);
            addData(objects, limitedList.getContent(), eventPage);
        } catch (ParseException e) {
            /*TODO: mensaje de error de fechas */
            return;
        }
    }

    private void addData(List<ODashAuditEvent> list, List<ODashAuditEvent> limitedList, Page page) {
        dataProvider.getItems().clear();
        dataProvider.getItems().addAll(list);
        dataProvider.refreshAll();
        totPages = page.getTotalPages();
        view.updateDownloadButton(limitedList);
    }

    public void updateDataProviderPagin(LocalDate firsDate, LocalDate secondDate,
                                        int actualpage,
                                        int itemsPerPage) {
        updateDataProvider(firsDate, secondDate, actualpage, itemsPerPage);
        view.updateTotalPage(totPages);
    }

    public void updateDataProviderPagin(LocalDate firsDate, LocalDate secondDate, List<String> userChildren, ODashAuditEvent.OEVENT_TYPE event,
                                        int actualpage,
                                        int itemsPerPage) {
        updateDataProvider(firsDate, secondDate, userChildren, event, actualpage, itemsPerPage);
        view.updateTotalPage(totPages);
    }

    public void updateDataProviderPagin(LocalDate firsDate, LocalDate secondDate, String email,
                                        int actualpage,
                                        int itemsPerPage) {
        updateDataProvider(firsDate, secondDate, email, actualpage, itemsPerPage);
        view.updateTotalPage(totPages);
    }

    public void updateDataProviderPagin(LocalDate firsDate, LocalDate secondDate, String email, ODashAuditEvent.OEVENT_TYPE event,
                                        int actualpage,
                                        int itemsPerPage) {
        updateDataProvider(firsDate, secondDate, email, event, actualpage, itemsPerPage);
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
