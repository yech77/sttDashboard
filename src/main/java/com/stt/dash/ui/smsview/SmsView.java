package com.stt.dash.ui.smsview;

import com.vaadin.flow.component.ClickEvent;
import com.vaadin.flow.component.Component;
import com.stt.dash.app.OMessageType;
import com.stt.dash.app.session.SetGenericBean;
import com.stt.dash.backend.data.OSystemIdSession;
import com.stt.dash.backend.data.OUserSession;
import com.stt.dash.backend.data.bean.OPageable;
import com.stt.dash.backend.data.entity.Carrier;
import com.stt.dash.backend.data.entity.ODashAuditEvent;
import com.stt.dash.backend.data.entity.SystemId;
import com.stt.dash.backend.data.entity.sms.AbstractSMS;
import com.stt.dash.backend.service.AbstractSmsService;
import com.stt.dash.backend.service.CarrierService;
import com.stt.dash.ui.MainView;
import com.stt.dash.ui.utils.BakeryConst;
import com.stt.dash.ui.utils.ODateUitls;
import com.vaadin.componentfactory.DateRange;
import com.vaadin.componentfactory.EnhancedDateRangePicker;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.Tag;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.dependency.JsModule;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.littemplate.LitTemplate;
import com.vaadin.flow.component.template.Id;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.provider.ListDataProvider;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.StreamResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.vaadin.gatanaso.MultiselectComboBox;
import org.vaadin.olli.FileDownloadWrapper;

import java.io.ByteArrayInputStream;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Tag("sms-view")
@JsModule("./src/views/smsview/sms-view.ts")
@Route(value = BakeryConst.PAGE_SMS_VIEW, layout = MainView.class)
@PageTitle(BakeryConst.TITLE_SMS_VIEW)
public class SmsView extends LitTemplate {
    @Id("firstline")
    Div firstline;
    @Id("secondline")
    Div secondline;
    @Id("footer")
    Div footer;
    @Id("smsGrid")
    Grid<AbstractSMS> smsGrid;
    /* Hora del servidor para establecer busquedas de YYYY-MM-DD*/
    public static LocalDateTime localDateTime = LocalDateTime.now();
    private EnhancedDateRangePicker dateOne = new EnhancedDateRangePicker();
    private DatePicker dateTwo = new DatePicker();
    ComboBox<Carrier> comboCarrier = new ComboBox<>();
    TextField textPhoneNumer = new TextField();
    MultiselectComboBox<SystemId> multi_systemIds = new MultiselectComboBox<>();
    MultiselectComboBox<OMessageType> multi_messagetype = new MultiselectComboBox<>();
    Button searchButton = new Button("Buscar");
    /**/
    private List<AbstractSMS> labs;
    private final OPageable opage = new OPageable();
    private static int maxSelect = 3;
    private int onPage = 0;
    private int currentPageSize = 0;
    private int currentElements = 0;
    private int currentPageCount = 0;
    /**/
    private final AbstractSmsService sms_serv;
    private final CarrierService carrier_serv;
    private final OUserSession ouser_session;
    /**/
    private ListDataProvider<AbstractSMS> dataProvider;
    private Span pageCounter = new Span("No se ha presionado ningún botón.");

    Button previous = new Button("Anterior", new ComponentEventListener<ClickEvent<Button>>() {
        @Override
        public void onComponentEvent(ClickEvent<Button> t) {
            /* Preguntar si existe la pagina antes de retroceder */
            if (opage.hasBefore()) {
                opage.beforePage();
            }

            labs = getSms();
            /* Encender o apagar el boton */
            previous.setEnabled(opage.hasBefore());
            next.setEnabled(opage.hasNext());
            dataProvider.getItems().clear();
            pageCounter.setText(opage.getCurrentPageToShow() + "/" + opage.getTotalPage());
            if (labs != null) {
                dataProvider.getItems().addAll(labs);
            }
            dataProvider.refreshAll();
        }
    });
    Button next = new Button("Siguiente", new ComponentEventListener<ClickEvent<Button>>() {
        @Override
        public void onComponentEvent(ClickEvent<Button> t) {
            if (opage.hasNext()) {
                System.out.println("Antes: " + opage);
                opage.nextPage();
                System.out.println("Despues: " + opage);
            }

            labs = getSms();
            /* Encender o apagar el boton */
            previous.setEnabled(opage.hasBefore());
            next.setEnabled(opage.hasNext());
            pageCounter.setText(opage.getCurrentPageToShow() + "/" + opage.getTotalPage());
            dataProvider.getItems().clear();
            if (labs != null) {
                dataProvider.getItems().addAll(labs);
            }
            dataProvider.refreshAll();
        }
    });
    public SmsView(@Autowired AbstractSmsService sms_serv,
                   @Autowired OUserSession ouser_session,
                   @Autowired CarrierService carrier_serv,
                   SetGenericBean<SystemId> systemIdSetGenericBean) {
        this.sms_serv = sms_serv;
        this.ouser_session = ouser_session;
        this.carrier_serv = carrier_serv;
        /**/
        Page<Carrier> carrierList = carrier_serv.findAll();
        Set<Carrier> carrierSet = carrierList.toSet();
        comboCarrier.setLabel("Operadoras");
        comboCarrier.setItems(carrierSet);
        comboCarrier.setItemLabelGenerator(Carrier::getCarrierCharcode);
        /**/
        multi_systemIds.setLabel("Credenciales");
        multi_systemIds.setItems(systemIdSetGenericBean.getSet());
        multi_systemIds.setItemLabelGenerator(SystemId::getSystemId);
        multi_systemIds.setValue(systemIdSetGenericBean.getSet());
        /**/
        multi_messagetype.setLabel("Tipo de Mensajes");
        multi_messagetype.setItems(OMessageType.values());
        /**/
        textPhoneNumer.setLabel("Numero a buscar");
        /**/
        /**/
        dateOne.setMin(LocalDate.now().minusMonths(1));
        dateOne.setMax(LocalDate.now());
        dateOne.setClearButtonVisible(true);
        dateOne.setRequired(true);
        dateOne.setInitialPosition(LocalDate.now());
        dateOne.setValue(new DateRange(LocalDate.now().minusDays(1), LocalDate.now()));
        dateOne.setLabel("Rango de busqueda");
        /**/
        firstline.add(dateOne);
        secondline.add(textPhoneNumer, multi_messagetype, comboCarrier, multi_systemIds, searchButton);
        footer.add(previous, pageCounter, next);
/****/
        smsGrid.addColumn(AbstractSMS::getSystemId).setHeader("Credencial");
        smsGrid.addColumn(AbstractSMS::getCarrierCharCode).setHeader("Operadora");
        smsGrid.addColumn(AbstractSMS::getDate).setHeader("Fecha de envio");
        smsGrid.addColumn(AbstractSMS::getDestination).setHeader("Destino");
        smsGrid.addColumn(AbstractSMS::getSource).setHeader("Source");
        smsGrid.addColumn(AbstractSMS::getMessageType).setHeader("Destino");
        searchButton.addClickListener(click -> {
            smsGrid.setItems(getSms());

        });
    }

    private Page<? extends AbstractSMS> getSmsPage(LocalDate dateOne, LocalDate dateTwo) {
        Page<? extends AbstractSMS> l = null;
        switch (getFindype()) {
            case 0:
                l = sms_serv.getAllMessages(dateOne, dateTwo, ouser_session.getStringSystemid(), onPage);
                break;
            case 1:
                l = sms_serv.findByPhoneNumer(dateOne, dateTwo,
                        ouser_session.getStringSystemid(),
                        textPhoneNumer.getValue().trim(),
                        opage.getCurrentPage());
                break;
            case 2:
                System.out.println(opage);
                l = sms_serv.findBySystemIdIn(dateOne,
                        dateTwo,
                        getSystemIdString(multi_systemIds.getSelectedItems()),
                        opage.getCurrentPage());
                break;
            case 3:
                l = sms_serv.findByPhoneNumber(dateOne,
                        dateTwo,
                        getSystemIdString(multi_systemIds.getSelectedItems()),
                        textPhoneNumer.getValue().trim(),
                        opage.getCurrentPage());
                break;
            case 4:
                l = sms_serv.findByCarrier(dateOne,
                        dateTwo,
                        ouser_session.getStringSystemid(),
                        comboCarrier.getValue().getCarrierCharcode().trim(),
                        opage.getCurrentPage());
                break;
            case 5:
                l = sms_serv.findByPhoneNumber(dateOne,
                        dateTwo,
                        getSystemIdString(multi_systemIds.getSelectedItems()),
                        textPhoneNumer.getValue().trim(),
                        opage.getCurrentPage());
                break;
            case 6:
                l = sms_serv.findByCarrier(dateOne,
                        dateTwo,
                        getSystemIdString(multi_systemIds.getSelectedItems()),
                        comboCarrier.getValue().getCarrierCharcode().trim(),
                        opage.getCurrentPage());
                break;
            case 8:
                l = sms_serv.findByMessageType(dateOne,
                        dateTwo,
                        ouser_session.getStringSystemid(),
                        valueOfMessageType(multi_messagetype.getSelectedItems()),
                        opage.getCurrentPage());
                break;
            case 9:
                l = sms_serv.findByPhoneNumber(dateOne,
                        dateTwo,
                        ouser_session.getStringSystemid(),
                        textPhoneNumer.getValue().trim(),
                        valueOfMessageType(multi_messagetype.getSelectedItems()),
                        opage.getCurrentPage());
                break;
            case 10:
                l = sms_serv.findByMessageType(dateOne,
                        dateTwo,
                        getSystemIdString(multi_systemIds.getSelectedItems()),
                        valueOfMessageType(multi_messagetype.getSelectedItems()),
                        opage.getCurrentPage());
                break;
            case 11:
                l = sms_serv.findByPhoneNumber(dateOne,
                        dateTwo,
                        getSystemIdString(multi_systemIds.getSelectedItems()),
                        textPhoneNumer.getValue().trim(),
                        opage.getCurrentPage());
                break;

            case 12:
                l = sms_serv.findByCarrierAndMessageType(dateOne,
                        dateTwo,
                        ouser_session.getStringSystemid(),
                        comboCarrier.getValue().getCarrierCharcode().trim(),
                        valueOfMessageType(multi_messagetype.getSelectedItems()),
                        opage.getCurrentPage());
                break;
            case 13:
                l = sms_serv.findByPhoneNumber(dateOne,
                        dateTwo,
                        ouser_session.getStringSystemid(),
                        textPhoneNumer.getValue().trim(),
                        valueOfMessageType(multi_messagetype.getSelectedItems()),
                        comboCarrier.getValue().getCarrierCharcode().trim(),
                        opage.getCurrentPage());
                break;
            case 14:
                l = sms_serv.findByCarrierAndMessageType(dateOne,
                        dateTwo,
                        getSystemIdString(multi_systemIds.getSelectedItems()),
                        comboCarrier.getValue().getCarrierCharcode().trim(),
                        valueOfMessageType(multi_messagetype.getSelectedItems()),
                        opage.getCurrentPage());
                break;
            case 15:
                l = sms_serv.findByPhoneNumber(dateOne,
                        dateTwo,
                        getSystemIdString(multi_systemIds.getSelectedItems()),
                        textPhoneNumer.getValue().trim(),
                        valueOfMessageType(multi_messagetype.getSelectedItems()),
                        comboCarrier.getValue().getCarrierCharcode().trim(),
                        opage.getCurrentPage());
                break;
        }
        if (l == null) {
            return null;
        }
        /* PAGEABLE */
        opage.setTotalPage(l.getTotalPages());
        opage.setCurrentPage(l.getPageable().getPageNumber());
        opage.setTotalData(l.getTotalElements());
        opage.setTotalDataPage(l.getNumberOfElements());
        currentPageSize = l.getContent().size();
        currentElements = (int) l.getTotalElements();
        currentPageCount = l.getTotalPages();

        System.out.println("PAGING - getSize: " + l.getSize());
        System.out.println("PAGING - getNumber: " + l.getNumber());
        System.out.println("PAGING - getNumberOfElements: " + l.getNumberOfElements());
        System.out.println("PAGING - getTotalElements: " + l.getTotalElements());
        System.out.println("PAGING - getPageable().getPageNumber() " + l.getPageable().getPageNumber());
        System.out.println("PAGING - l.getPageable(): " + l.getPageable());
        System.out.println("AFTER CALL: " + opage);
        updateDownloadButton(obtainAbstractOf(l));
        return l;
    }

    private void updateDownloadButton(List<? extends AbstractSMS> messages) {
        footer.removeAll();
        footer.add(getDownloadButton(messages));
    }

    private Component getDownloadButton(List<? extends AbstractSMS> messages) {
        int year = localDateTime.getYear();
        int month = localDateTime.getMonthValue();
        int day = localDateTime.getDayOfMonth();
        int hour = localDateTime.getHour();
        String fileName = "" + year + "." + month + "." + day + "." + hour + ":00-Mensajes.csv";
        Button download = new Button("Descargar Datos (" + year + "/" + month + "/" + day + "-" + hour + ":00)");

        FileDownloadWrapper buttonWrapper = new FileDownloadWrapper(
                new StreamResource(fileName, () -> new ByteArrayInputStream(getStringData(messages).getBytes())));
        download.addClickListener(click -> {
            LocalDate selectedStartDate = (dateOne.getValue()==null)?null:dateOne.getValue().getStartDate();
            LocalDate selectedEndDate = (dateOne.getValue()==null)?null:dateOne.getValue().getEndDate();
            StringBuilder sb = new StringBuilder();
            sb
                    .append("Desde: ")
                    .append(ODateUitls.dd_MM_yyyy.format(ODateUitls.valueOf(selectedStartDate)))
                    .append(" Hasta: ")
                    .append(ODateUitls.dd_MM_yyyy.format(ODateUitls.valueOf(selectedEndDate)));
//            auditEvent.add(ODashAuditEvent.OEVENT_TYPE.DOWNLOAD_FILE_SEARCH_SMS, sb.toString());
        });
        buttonWrapper.wrapComponent(download);
        return buttonWrapper;
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
    private List<String> getSystemIdString(Set<SystemId> s) {
        List<String> l = new ArrayList<>(s.size());
        for (SystemId oSystemIdSession : s) {
            l.add(oSystemIdSession.getSystemId());

        }
        return l;
    }

    public String getStringData(List<? extends AbstractSMS> messages) {
        if (messages.size() > 5000000) {
            System.out.println("Daily message limit reached. Code not able to handle this size of string.");
            return "";
        }
        StringBuilder sb = new StringBuilder("id,\"address\",datacoding,\"date\",\"iso2\",\"message_type\",\"messages_text\",\"msg_received\",\"msg_sended\",\"source\",\"systemid\",\"carrierCharCode\"\n");
        for (AbstractSMS msg : messages) {
            sb.append(msg.toString());
            sb.append("\n");
        }
        return sb.toString();
    }

    private Set<String> valueOfMessageType(Set<OMessageType> s) {
        Set<String> l = new HashSet<>(s.size());
        for (OMessageType oMessageType : s) {
            l.add(oMessageType.name());
        }
        return l;
    }

    public List<AbstractSMS> obtainAbstractOf(Page<? extends AbstractSMS> l) {
        return l == null ? new ArrayList<>() : new ArrayList<>(l.getContent());
    }

    private List<AbstractSMS> getSms() {
//        if (dateOne == null || dateTwo == null) {
//            LocalDate now = LocalDate.now();
//            return obtainAbstractOf(getSmsPage(LocalDate.of(now.getYear(), now.getMonthValue(), 1), now));
//        }
        LocalDate selectedStartDate = (dateOne.getValue()==null)?null:dateOne.getValue().getStartDate();
        LocalDate selectedEndDate = (dateOne.getValue()==null)?null:dateOne.getValue().getEndDate();
        return obtainAbstractOf(getSmsPage(selectedStartDate, selectedEndDate));
    }
}
