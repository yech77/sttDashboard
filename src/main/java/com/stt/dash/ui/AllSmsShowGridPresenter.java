package com.stt.dash.ui;

import com.stt.dash.app.session.ListGenericBean;
import com.stt.dash.backend.data.SmsByYearMonthDayHour;
import com.stt.dash.backend.data.entity.sms.AbstractSMS;
import com.stt.dash.backend.service.AbstractSmsService;
import com.stt.dash.backend.service.SmsHourService;
import org.springframework.data.domain.Page;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

public class AllSmsShowGridPresenter extends SmsShowGridPresenter<AbstractSMS> {

    private final AbstractSmsService abstractSmsService;

    public AllSmsShowGridPresenter(AbstractSmsService abstractSmsService, SmsHourService smsHourService, int actualHour, List<String> stringListGenericBean, Viewnable<AbstractSMS> view) {
        super(smsHourService, Arrays.asList(actualHour), stringListGenericBean, view);
        this.abstractSmsService = abstractSmsService;
        List<AbstractSMS> smsHourList = getGroupSmsBy(stringListGenericBean, Arrays.asList(actualHour));
        updateDataProvider(smsHourList);
        updateInView(dataProvider);
    }

    @Override
    public List<AbstractSMS> getGroupSmsBy(List<String> stringList, List<Integer> integerList) {
        return getGroupSmsBy(stringList, integerList, 0, 1000);
    }

    @Override
    public List<AbstractSMS> getGroupSmsBy(List<String> stringList, List<Integer> integerList, int page, int pageSize) {
        Page<AbstractSMS> allMessages = abstractSmsService.getAllMessages(stringList, LocalDate.now(), integerList.get(0).intValue(), page, pageSize);
        return allMessages.getContent();
    }
}
