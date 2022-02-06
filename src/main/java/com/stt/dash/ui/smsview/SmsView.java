package com.stt.dash.ui.smsview;

import com.stt.dash.app.OMessageType;
import com.stt.dash.app.session.SetGenericBean;
import com.stt.dash.backend.data.OUserSession;
import com.stt.dash.backend.data.bean.OPageable;
import com.stt.dash.backend.data.entity.Carrier;
import com.stt.dash.backend.data.entity.SystemId;
import com.stt.dash.backend.data.entity.sms.AbstractSMS;
import com.stt.dash.backend.service.AbstractSmsService;
import com.stt.dash.backend.service.CarrierService;
import com.stt.dash.ui.MainView;
import com.stt.dash.ui.utils.BakeryConst;
import com.stt.dash.ui.utils.ODateUitls;
import com.vaadin.componentfactory.DateRange;
import com.vaadin.componentfactory.EnhancedDateRangePicker;
import com.vaadin.componentfactory.multiselect.MultiComboBox;
import com.vaadin.flow.component.ClickEvent;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.Tag;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.checkbox.CheckboxGroup;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.dependency.JsModule;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.grid.HeaderRow;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.littemplate.LitTemplate;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.template.Id;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.provider.ListDataProvider;
import com.vaadin.flow.data.renderer.LocalDateTimeRenderer;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.StreamResource;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.vaadin.olli.FileDownloadWrapper;

import java.io.ByteArrayInputStream;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
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
    Grid<AbstractSMS> grid;
    /**/

    /**/
    private ListDataProvider<AbstractSMS> dataProvider;
    private List<AbstractSMS> labs;
    private Grid.Column<AbstractSMS> idColumn;
    private Grid.Column<AbstractSMS> phoneColum;
    private Grid.Column<AbstractSMS> carrierColum;
    private Grid.Column<AbstractSMS> systemIdColumn;
    private Grid.Column<AbstractSMS> messageTypeColum;
    private Grid.Column<AbstractSMS> dateColumn;
    /**/

    /* Hora del servidor para establecer busquedas de YYYY-MM-DD*/
    public static LocalDateTime localDateTime = LocalDateTime.now();
    private EnhancedDateRangePicker dateOne = new EnhancedDateRangePicker();
    private DatePicker dateTwo = new DatePicker();
    ComboBox<Carrier> comboCarrier = new ComboBox<>();
    TextField textPhoneNumer = new TextField();
    MultiComboBox<SystemId> multi_systemIds = new MultiComboBox<>();
    CheckboxGroup<OMessageType> checkboxMessageType = new CheckboxGroup<>();
    Button searchButton = new Button("Buscar");
    /**/
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
        comboCarrier.setHelperText("Dejar en blanco para buscar en todas las operadoras.");
        /**/
        multi_systemIds.setLabel("Credenciales");
        multi_systemIds.setItems(systemIdSetGenericBean.getSet());
        multi_systemIds.setItemLabelGenerator(SystemId::getSystemId);
        multi_systemIds.setValue(systemIdSetGenericBean.getSet());
        multi_systemIds.setValue(systemIdSetGenericBean.getSet());
        multi_systemIds.setRequired(true);
        multi_systemIds.setErrorMessage("Seleccione al menos una credencial");
        multi_systemIds.addValueChangeListener(change -> {
            if (change.getValue().size() == 0) {
                multi_systemIds.setInvalid(true);
            } else {
                multi_systemIds.setInvalid(false);
            }
            searchButton.setEnabled(isValidSearch());
        });
        /**/
        checkboxMessageType.setLabel("Tipo de Mensajes");
        checkboxMessageType.setItems(OMessageType.values());
        checkboxMessageType.setValue(new HashSet<OMessageType>(Arrays.asList(OMessageType.values())));
        checkboxMessageType.setRequired(true);
        checkboxMessageType.setErrorMessage("seleccionar al menos un tipo de sms");
        checkboxMessageType.addValueChangeListener(change -> {
            if (checkboxMessageType.getValue().size() == 0) {
                checkboxMessageType.setInvalid(true);
            } else {
                checkboxMessageType.setInvalid(false);
            }
            searchButton.setEnabled(isValidSearch());
        });
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
        dateOne.setSidePanelVisible(false);
        dateOne.setLabel("Rango de busqueda");
        /**/
        secondline.add(new HorizontalLayout(dateOne, checkboxMessageType),
                new HorizontalLayout(textPhoneNumer, comboCarrier),
                new HorizontalLayout(multi_systemIds), searchButton);
        dateOne.setWidthFull();
        textPhoneNumer.setWidthFull();
        checkboxMessageType.setWidthFull();
        comboCarrier.setWidthFull();
        multi_systemIds.setWidthFull();
        footer.add(previous, pageCounter, next);
        /**/
        createGrid();

/****/
//        grid.addColumn(AbstractSMS::getSystemId).setHeader("Credencial");
//        grid.addColumn(AbstractSMS::getCarrierCharCode).setHeader("Operadora");
//        grid.addColumn(AbstractSMS::getDate).setHeader("Fecha de envio");
//        grid.addColumn(AbstractSMS::getDestination).setHeader("Destino");
//        grid.addColumn(AbstractSMS::getSource).setHeader("Source");
//        grid.addColumn(AbstractSMS::getMessageType).setHeader("Destino");
        textPhoneNumer.addValueChangeListener(listener -> {
            if (!listener.isFromClient()) {
                return;
            }
            if (StringUtils.startsWith(listener.getValue(), "58414") ||
                    StringUtils.startsWith(listener.getValue(), "58424")) {
                Carrier carrier = carrierSet
                        .stream()
                        .filter(f -> f.getCarrierCharcode().equalsIgnoreCase("movistar"))
                        .findFirst()
                        .orElse(null);
                comboCarrier.setValue(carrier != null ? carrier : null);
            }
        });
        searchButton.addClickListener(click -> {
            if (multi_systemIds.getValue().size() < 1 && comboCarrier.getValue() == null
                    && (textPhoneNumer.getValue() == null || textPhoneNumer.getValue().length() < 1)) {
                Notification notification = new Notification("Por favor seleccione otro campo adicional para filtrar", 2500);
                notification.setPosition(Notification.Position.MIDDLE);
                notification.open();
                return;
            }
            click.getSource().setEnabled(false);
            try {
                opage.init();
                labs = getSms();
                /* Encender o apagar el boton */
                previous.setEnabled(opage.hasBefore());
                next.setEnabled(opage.hasNext());
                pageCounter.setText(opage.getCurrentPageToShow() + "/" + opage.getTotalPage());
                dataProvider.getItems().clear();
                if (labs != null) {
                    dataProvider.getItems().addAll(labs);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            click.getSource().setEnabled(true);
            dataProvider.refreshAll();
            if (dataProvider.getItems().isEmpty()) {
                Notification notification = new Notification();
                Span label = new Span("No hay información a mostrar.");
                Button closeButton = new Button("Cerrar", e -> notification.close());
                notification.open();
                notification.setPosition(Notification.Position.MIDDLE);
                notification.setText("Para que es el texto");
                notification.add(label, closeButton);
            }
        });
    }

    private Page<? extends AbstractSMS> getSmsPage(LocalDate dateOne, LocalDate dateTwo) {
        Page<? extends AbstractSMS> smsPage = null;
        switch (getFindype()) {
            case 10:
                smsPage = sms_serv.findByMessageType(dateOne,
                        dateTwo,
                        getSystemIdString(multi_systemIds.getValue()),
                        valueOfMessageType(checkboxMessageType.getSelectedItems()),
                        opage.getCurrentPage());
                break;
            case 11:
                smsPage = sms_serv.findByPhoneNumber(dateOne,
                        dateTwo,
                        getSystemIdString(multi_systemIds.getValue()),
                        textPhoneNumer.getValue().trim(),
                        opage.getCurrentPage());
                break;

            case 12:
                smsPage = sms_serv.findByCarrierAndMessageType(dateOne,
                        dateTwo,
                        getSystemIdString(multi_systemIds.getValue()),
                        comboCarrier.getValue().getCarrierCharcode().trim(),
                        valueOfMessageType(checkboxMessageType.getSelectedItems()),
                        opage.getCurrentPage());
                break;
            case 13:
                smsPage = sms_serv.findByPhoneNumer(dateOne,
                        dateTwo,
                        comboCarrier.getValue().getCarrierCharcode().trim(),
                        textPhoneNumer.getValue().trim(),
                        opage.getCurrentPage());
                break;
            case 14:
                smsPage = sms_serv.findByCarrierAndMessageType(dateOne,
                        dateTwo,
                        getSystemIdString(multi_systemIds.getValue()),
                        comboCarrier.getValue().getCarrierCharcode().trim(),
                        valueOfMessageType(checkboxMessageType.getSelectedItems()),
                        opage.getCurrentPage());
                break;
            case 15:
                smsPage = sms_serv.findByPhoneNumber(dateOne,
                        dateTwo,
                        getSystemIdString(multi_systemIds.getValue()),
                        textPhoneNumer.getValue().trim(),
                        valueOfMessageType(checkboxMessageType.getSelectedItems()),
                        comboCarrier.getValue().getCarrierCharcode().trim(),
                        opage.getCurrentPage());
                break;
        }
        if (smsPage == null) {
            return null;
        }
        /* PAGEABLE */
        opage.setTotalPage(smsPage.getTotalPages());
        opage.setCurrentPage(smsPage.getPageable().getPageNumber());
        opage.setTotalData(smsPage.getTotalElements());
        opage.setTotalDataPage(smsPage.getNumberOfElements());
        currentPageSize = smsPage.getContent().size();
        currentElements = (int) smsPage.getTotalElements();
        currentPageCount = smsPage.getTotalPages();

        System.out.println("PAGING - getSize: " + smsPage.getSize());
        System.out.println("PAGING - getNumber: " + smsPage.getNumber());
        System.out.println("PAGING - getNumberOfElements: " + smsPage.getNumberOfElements());
        System.out.println("PAGING - getTotalElements: " + smsPage.getTotalElements());
        System.out.println("PAGING - getPageable().getPageNumber() " + smsPage.getPageable().getPageNumber());
        System.out.println("PAGING - l.getPageable(): " + smsPage.getPageable());
        System.out.println("AFTER CALL: " + opage);
        updateDownloadButton(obtainAbstractOf(smsPage));
        return smsPage;
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
            LocalDate selectedStartDate = (dateOne.getValue() == null) ? null : dateOne.getValue().getStartDate();
            LocalDate selectedEndDate = (dateOne.getValue() == null) ? null : dateOne.getValue().getEndDate();
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

    /**
     * 8: No permitida busqueda por solo Tipo de mensaje.
     * 9: No existe dado que al buscar por num se busca tanbien por su operadora.
     * 10: Tipo de mensaje y SystemId.
     * 11: Tipo de mensaje, num de telefono y systemid.
     * 12: Tipo de mensaje y operadora.
     * 13: Tipo de mensaje, numero de telefono y operadora.
     * 14: tipo de mensaje, systemid y operadora.
     * 15: tipo de mensaje, numero de telefono, systemid y operadora.
     *
     * @return
     */
    private int getFindype() {
        int n = 8;

        if (StringUtils.isNotBlank(textPhoneNumer.getValue())) {
            n += 1;
        }

        if (CollectionUtils.isNotEmpty(multi_systemIds.getValue())) {
            n += 2;
        }

        if (ObjectUtils.isNotEmpty(comboCarrier.getValue()) && StringUtils.isNotBlank(comboCarrier.getValue().getCarrierName())) {
            n += 4;
        }

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
        LocalDate selectedStartDate = (dateOne.getValue() == null) ? null : dateOne.getValue().getStartDate();
        LocalDate selectedEndDate = (dateOne.getValue() == null) ? null : dateOne.getValue().getEndDate();
        if (selectedEndDate == null) {
            selectedEndDate = selectedStartDate;
        }
        return obtainAbstractOf(getSmsPage(selectedStartDate, selectedEndDate));
    }

    private void addColumnsToGrid() {
        createIdColumn();
        createPhoneNumberColumn();
        createCarrierColumn();
        createSystemIdColumn();
        createMessageypeColumn();
        createDateColumn();
    }

    private void createIdColumn() {
        idColumn = grid.addColumn(AbstractSMS::getId, "id").setHeader("ID")
                .setWidth("120px").setFlexGrow(0);
    }

    private void createPhoneNumberColumn() {
        phoneColum = grid
                .addColumn(AbstractSMS::getDestination)
                .setComparator(client -> client.getDestination()).setHeader("Telefono")
                .setWidth("180px").setFlexGrow(0);
    }

    private void createCarrierColumn() {
        carrierColum = grid
                .addColumn(AbstractSMS::getCarrierCharCode)
                .setComparator(client -> client.getCarrierCharCode()).setHeader("Operadora")
                .setAutoWidth(true);
//                .setWidth("180px").setFlexGrow(0);
    }

    private void createSystemIdColumn() {
        systemIdColumn = grid
                .addColumn(AbstractSMS::getSystemId)
                .setComparator(client -> client.getSystemId()).setHeader("Systemid")
                .setAutoWidth(true);
//                .setWidth("180px").setFlexGrow(0);
    }

    private void createMessageypeColumn() {
        messageTypeColum = grid
                .addColumn(AbstractSMS::getMessageType)
                .setComparator(client -> client.getMessageType()).setHeader("Message type")
                .setAutoWidth(true);
//                .setWidth("180px").setFlexGrow(0);
    }

    private void createDateColumn() {
        dateColumn = grid
                .addColumn(new LocalDateTimeRenderer<>(
                        client -> client.getDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime(),
                        DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss.SSS")))
                .setComparator(AbstractSMS::getDate).setHeader("Date")
                .setAutoWidth(true);
//                .setWidth("180px").setFlexGrow(0);
    }

    private void addFiltersToGrid() {
        HeaderRow filterRow = grid.appendHeaderRow();

        TextField idFilter = new TextField();
        idFilter.setPlaceholder("Filter");
        idFilter.setClearButtonVisible(true);
        idFilter.setWidth("100%");
        idFilter.setValueChangeMode(ValueChangeMode.EAGER);
        idFilter.addValueChangeListener(
                event -> dataProvider.addFilter(client -> StringUtils
                        .containsIgnoreCase(Long.toString(client.getId()),
                                idFilter.getValue())));
        filterRow.getCell(idColumn).setComponent(idFilter);

        TextField phoneFilter = new TextField();
        phoneFilter.setPlaceholder("Filter");
        phoneFilter.setClearButtonVisible(true);
        phoneFilter.setWidth("100%");
        phoneFilter.setValueChangeMode(ValueChangeMode.EAGER);
        phoneFilter.addValueChangeListener(event -> dataProvider.addFilter(
                client -> StringUtils.containsIgnoreCase(client.getDestination(),
                        phoneFilter.getValue())));
        filterRow.getCell(phoneColum).setComponent(phoneFilter);

        TextField carrierFilter = new TextField();
        carrierFilter.setPlaceholder("Filter");
        carrierFilter.setClearButtonVisible(true);
        carrierFilter.setWidth("100%");
        carrierFilter.setValueChangeMode(ValueChangeMode.EAGER);
        carrierFilter.addValueChangeListener(event -> dataProvider.addFilter(
                client -> StringUtils.containsIgnoreCase(client.getCarrierCharCode(),
                        carrierFilter.getValue())));
        filterRow.getCell(carrierColum).setComponent(carrierFilter);

        TextField systemIdFilter = new TextField();
        systemIdFilter.setPlaceholder("Filter");
        systemIdFilter.setClearButtonVisible(true);
        systemIdFilter.setWidth("100%");
        systemIdFilter.setValueChangeMode(ValueChangeMode.EAGER);
        systemIdFilter.addValueChangeListener(event -> dataProvider.addFilter(
                client -> StringUtils.containsIgnoreCase(client.getSystemId(),
                        systemIdFilter.getValue())));
        filterRow.getCell(systemIdColumn).setComponent(systemIdFilter);

        TextField messageTypeFilter = new TextField();
//        messageTypeFilter.setItems(Arrays.asList("Pending", "Success", "Error"));
        messageTypeFilter.setPlaceholder("Filter");
        messageTypeFilter.setClearButtonVisible(true);
        messageTypeFilter.setWidth("100%");
        systemIdFilter.setValueChangeMode(ValueChangeMode.EAGER);
        messageTypeFilter.addValueChangeListener(event -> dataProvider.addFilter(
                client -> StringUtils.containsIgnoreCase(client.getMessageType(),
                        messageTypeFilter.getValue())));
        filterRow.getCell(messageTypeColum).setComponent(messageTypeFilter);

        DatePicker dateFilter = new DatePicker();
        dateFilter.setPlaceholder("Filter");
        dateFilter.setClearButtonVisible(true);
        dateFilter.setWidth("100%");
        dateFilter.addValueChangeListener(event -> dataProvider
                .addFilter(client -> areDatesEqual(client, dateFilter)));
        filterRow.getCell(dateColumn).setComponent(dateFilter);
    }

    private boolean areDatesEqual(AbstractSMS client, DatePicker dateFilter) {
        LocalDate dateFilterValue = dateFilter.getValue();
        if (dateFilterValue != null) {
            LocalDate clientDate = client.getDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
            return dateFilterValue.equals(clientDate);
        }
        return true;
    }

    private void createGrid() {
        createGridComponent();
        addColumnsToGrid();
        addFiltersToGrid();
    }

    private void createGridComponent() {
//        grid = new GridPro<>();
        grid.setSelectionMode(Grid.SelectionMode.NONE);
        grid.addThemeVariants(GridVariant.LUMO_NO_BORDER,
                GridVariant.LUMO_COLUMN_BORDERS);
        grid.setHeightFull();
        grid.setWidthFull();
//        labs = getSms();
//        if (labs == null) {
        labs = new ArrayList<>();
//        }
        dataProvider = new ListDataProvider<>(labs);
        grid.setDataProvider(dataProvider);
        grid.setPageSize(25);
        grid.appendFooterRow();
    }

    private boolean isValidSearch() {
        return (!checkboxMessageType.isInvalid() && !multi_systemIds.isInvalid());
    }
}
