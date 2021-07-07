package com.stt.dash.ui.smsview;

import com.stt.dash.app.OMessageType;
import com.stt.dash.backend.data.OUserSession;
import com.stt.dash.backend.data.bean.OPageable;
import com.stt.dash.backend.data.entity.Carrier;
import com.stt.dash.backend.data.entity.SystemId;
import com.stt.dash.backend.data.entity.sms.AbstractSMS;
import com.stt.dash.backend.service.AbstractSmsService;
import com.stt.dash.ui.MainView;
import com.stt.dash.ui.utils.BakeryConst;
import com.vaadin.flow.component.Tag;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.dependency.JsModule;
import com.vaadin.flow.component.littemplate.LitTemplate;
import com.vaadin.flow.component.template.Id;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.vaadin.gatanaso.MultiselectComboBox;

import java.time.LocalDate;

@Tag("sms-view")
@JsModule("./src/views/smsview/sms-view.ts")
@Route(value = BakeryConst.PAGE_SMS_VIEW, layout = MainView.class)
@PageTitle(BakeryConst.TITLE_SMS_VIEW)
public class SmsView extends LitTemplate {
    @Id("dateOne")
    private DatePicker dateOne;
    @Id("dateTwo")
    private DatePicker dateTwo;
    @Id("comboCarrier")
    MultiselectComboBox<Carrier> comboCarrier;
    @Id("textPhoneNumer")
    TextField textPhoneNumer;
    @Id("multi_systemIds")
    MultiselectComboBox<SystemId> multi_systemIds;
    @Id("multi_messagetype")
    MultiselectComboBox<OMessageType> multi_messagetype;
    @Id("searchButton")
    Button searchButton;
    /**/
    private final OPageable opage = new OPageable();
    private static int maxSelect = 3;
    private int onPage = 0;
    private int currentPageSize = 0;
    private int currentElements = 0;
    private int currentPageCount = 0;
    /**/
    private final AbstractSmsService sms_serv;
    private final OUserSession ouser_session;

    public SmsView(@Autowired AbstractSmsService sms_serv,
                   @Autowired OUserSession ouser_session){
        this.sms_serv=sms_serv;
        this.ouser_session=ouser_session;
    }

    private Page<? extends AbstractSMS> getSmsPage(LocalDate dateOne, LocalDate dateTwo) {
//        Page<? extends AbstractSMS> l = null;
//        switch (getFindype()) {
////            case 0:
////                l = sms_serv.getAllMessages(dateOne, dateTwo, ouser_session.getStringSystemid(), onPage);
////                break;
//            case 1:
//                l = sms_serv.findByPhoneNumer(dateOne, dateTwo,
//                        ouser_session.getStringSystemid(),
//                        textPhoneNumer.getValue().trim(),
//                        opage.getCurrentPage());
//                break;
//            case 2:
//                System.out.println(opage);
//                l = sms_serv.findBySystemIdIn(dateOne,
//                        dateTwo,
//                        getSystemIdString(multi_systemIds.getSelectedItems()),
//                        opage.getCurrentPage());
//                break;
//            case 3:
//                l = sms_serv.findByPhoneNumber(dateOne,
//                        dateTwo,
//                        getSystemIdString(multi_systemIds.getSelectedItems()),
//                        textPhoneNumer.getValue().trim(),
//                        opage.getCurrentPage());
//                break;
//            case 4:
//                l = sms_serv.findByCarrier(dateOne,
//                        dateTwo,
//                        ouser_session.getStringSystemid(),
//                        comboCarrier.getValue().getCarrierCharcode().trim(),
//                        opage.getCurrentPage());
//                break;
//            case 5:
//                l = sms_serv.findByPhoneNumber(dateOne,
//                        dateTwo,
//                        getSystemIdString(multi_systemIds.getSelectedItems()),
//                        textPhoneNumer.getValue().trim(),
//                        opage.getCurrentPage());
//                break;
//            case 6:
//                l = sms_serv.findByCarrier(dateOne,
//                        dateTwo,
//                        getSystemIdString(multi_systemIds.getSelectedItems()),
//                        comboCarrier.getValue().getCarrierCharcode().trim(),
//                        opage.getCurrentPage());
//                break;
//            case 8:
//                l = sms_serv.findByMessageType(dateOne,
//                        dateTwo,
//                        ouser_session.getStringSystemid(),
//                        valueOfMessageType(multi_messagetype.getSelectedItems()),
//                        opage.getCurrentPage());
//                break;
//            case 9:
//                l = sms_serv.findByPhoneNumber(dateOne,
//                        dateTwo,
//                        ouser_session.getStringSystemid(),
//                        textPhoneNumer.getValue().trim(),
//                        valueOfMessageType(multi_messagetype.getSelectedItems()),
//                        opage.getCurrentPage());
//                break;
//            case 10:
//                l = sms_serv.findByMessageType(dateOne,
//                        dateTwo,
//                        getSystemIdString(multi_systemIds.getSelectedItems()),
//                        valueOfMessageType(multi_messagetype.getSelectedItems()),
//                        opage.getCurrentPage());
//                break;
//            case 11:
//                l = sms_serv.findByPhoneNumber(dateOne,
//                        dateTwo,
//                        getSystemIdString(multi_systemIds.getSelectedItems()),
//                        textPhoneNumer.getValue().trim(),
//                        opage.getCurrentPage());
//                break;
//
//            case 12:
//                l = sms_serv.findByCarrierAndMessageType(dateOne,
//                        dateTwo,
//                        ouser_session.getStringSystemid(),
//                        comboCarrier.getValue().getCarrierCharcode().trim(),
//                        valueOfMessageType(multi_messagetype.getSelectedItems()),
//                        opage.getCurrentPage());
//                break;
//            case 13:
//                l = sms_serv.findByPhoneNumber(dateOne,
//                        dateTwo,
//                        ouser_session.getStringSystemid(),
//                        textPhoneNumer.getValue().trim(),
//                        valueOfMessageType(multi_messagetype.getSelectedItems()),
//                        comboCarrier.getValue().getCarrierCharcode().trim(),
//                        opage.getCurrentPage());
//                break;
//            case 14:
//                l = sms_serv.findByCarrierAndMessageType(dateOne,
//                        dateTwo,
//                        getSystemIdString(multi_systemIds.getSelectedItems()),
//                        comboCarrier.getValue().getCarrierCharcode().trim(),
//                        valueOfMessageType(multi_messagetype.getSelectedItems()),
//                        opage.getCurrentPage());
//                break;
//            case 15:
//                l = sms_serv.findByPhoneNumber(dateOne,
//                        dateTwo,
//                        getSystemIdString(multi_systemIds.getSelectedItems()),
//                        textPhoneNumer.getValue().trim(),
//                        valueOfMessageType(multi_messagetype.getSelectedItems()),
//                        comboCarrier.getValue().getCarrierCharcode().trim(),
//                        opage.getCurrentPage());
//                break;
//        }
//        if (l == null) {
//            return null;
//        }
//        /* PAGEABLE */
//        opage.setTotalPage(l.getTotalPages());
//        opage.setCurrentPage(l.getPageable().getPageNumber());
//        opage.setTotalData(l.getTotalElements());
//        opage.setTotalDataPage(l.getNumberOfElements());
//        currentPageSize = l.getContent().size();
//        currentElements = (int) l.getTotalElements();
//        currentPageCount = l.getTotalPages();
//
//        System.out.println("PAGING - getSize: " + l.getSize());
//        System.out.println("PAGING - getNumber: " + l.getNumber());
//        System.out.println("PAGING - getNumberOfElements: " + l.getNumberOfElements());
//        System.out.println("PAGING - getTotalElements: " + l.getTotalElements());
//        System.out.println("PAGING - getPageable().getPageNumber() " + l.getPageable().getPageNumber());
//        System.out.println("PAGING - l.getPageable(): " + l.getPageable());
//        System.out.println("AFTER CALL: " + opage);
//        updateDownloadButton(obtainAbstractOf(l));
//        return l;
        return null;
    }

    private int getFindype() {
        int n = 0;
        if (!"".equals(textPhoneNumer.getValue().trim())) {
            n += 1;
        }
        if (multi_systemIds.getSelectedItems().size() > 0) {
            n += 2;
        }
//        if (comboCarrier.getValue() != null && !"".equals(comboCarrier.getValue().getCarrierName().trim())) {
//            n += 4;
//        }
        if (multi_messagetype.getValue().size() > 0) {
            n += 8;
        }
        System.out.println("FindType: " + n);
        return n;
    }
}
