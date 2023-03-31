package com.stt.dash.ui.smsview;

import com.stt.dash.app.OMessageType;
import com.stt.dash.app.security.CurrentUser;
import com.stt.dash.app.session.SetGenericBean;
import com.stt.dash.backend.data.OUserSession;
import com.stt.dash.backend.data.Role;
import com.stt.dash.backend.data.entity.Carrier;
import com.stt.dash.backend.data.entity.ORole;
import com.stt.dash.backend.data.entity.SystemId;
import com.stt.dash.backend.data.entity.User;
import com.stt.dash.backend.data.entity.sms.AbstractSMS;
import com.stt.dash.backend.service.AbstractSmsService;
import com.stt.dash.backend.service.CarrierService;
import com.stt.dash.ui.MainView;
import com.stt.dash.ui.utils.BakeryConst;
import com.stt.dash.ui.utils.I18nUtils;
import com.stt.dash.ui.utils.ODateUitls;
import com.stt.dash.ui.utils.messages.Message;
import com.vaadin.componentfactory.DateRange;
import com.vaadin.componentfactory.EnhancedDateRangePicker;
import com.vaadin.componentfactory.multiselect.MultiComboBox;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.Tag;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.checkbox.CheckboxGroup;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.confirmdialog.ConfirmDialog;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.dependency.JsModule;
import com.vaadin.flow.component.grid.FooterRow;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.littemplate.LitTemplate;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.template.Id;
import com.vaadin.flow.component.textfield.IntegerField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.provider.ListDataProvider;
import com.vaadin.flow.data.renderer.LocalDateTimeRenderer;
import com.vaadin.flow.data.renderer.TemplateRenderer;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.StreamResource;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.security.access.annotation.Secured;
import org.vaadin.olli.FileDownloadWrapper;

import java.io.ByteArrayInputStream;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.stream.Collectors;

@Tag("sms-view")
@JsModule("./src/views/smsview/sms-view.js")
@Route(value = BakeryConst.PAGE_SMS_VIEW, layout = MainView.class)
@PageTitle(BakeryConst.TITLE_SMS_VIEW)
@Secured({Role.ADMIN, "UI_SEARCH_SMS"})
public class SmsView extends LitTemplate {
    @Id("firstline")
    Div firstline;
    @Id("secondline")
    Div secondline;
    @Id("filterButton")
    private Button searchButton;
    @Id("footer")
    Div footer;
    @Id("smsGrid")
    Grid<AbstractSMS> grid;
    /**/
    private Locale esLocale = new Locale("es", "ES");
    /**/
    private ListDataProvider<AbstractSMS> dataProvider;
    private List<AbstractSMS> abstractSMSList;
    //    private Grid.Column<AbstractSMS> idColumn;
    private Grid.Column<AbstractSMS> phoneColum;
    private Grid.Column<AbstractSMS> carrierColum;
    private Grid.Column<AbstractSMS> systemIdColumn;
    private Grid.Column<AbstractSMS> messageTypeColum;
    private Grid.Column<AbstractSMS> dateColumn;
    /**/
    private boolean hasAuthToViewMsgTextColumn = false;
    /* Hora del servidor para establecer busquedas de YYYY-MM-DD*/
    public static LocalDateTime localDateTime = LocalDateTime.now();
    /**/
    private EnhancedDateRangePicker dateOne = new EnhancedDateRangePicker();
    private DatePicker firstDate = new DatePicker();
    private DatePicker secondDate = new DatePicker();
    /**/
    private ComboBox<Carrier> comboCarrier = new ComboBox<>();
    private TextField textPhoneNumer = new TextField();
    private MultiComboBox<SystemId> multi_systemIds = new MultiComboBox<>();
    private CheckboxGroup<OMessageType> checkboxMessageType = new CheckboxGroup<>();
    //    private Button searchButton = new Button("Buscar");
    private IntegerField currentPageTextbox = new IntegerField("Página");
    private Label totalAmountOfPagesLabel = new Label();
    /**/
    HorizontalLayout layoutButtons = new HorizontalLayout();
    private Component componentWrapper;
    /**/
    private int itemsPerPage = 25;
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
    /**/
    FooterRow footerRow;

    public SmsView(@Autowired CurrentUser currentUser,
                   @Autowired AbstractSmsService sms_serv,
                   @Autowired OUserSession ouser_session,
                   @Autowired CarrierService carrier_serv,
                   @Autowired SetGenericBean<SystemId> systemIdSetGenericBean) {
        this.sms_serv = sms_serv;
        this.ouser_session = ouser_session;
        this.carrier_serv = carrier_serv;
        /**/
        this.hasAuthToViewMsgTextColumn = isGrantedMsgTextColumn(currentUser.getUser().getRoles());
        /**/
        Page<Carrier> carrierList = carrier_serv.findAll();
        Set<Carrier> carrierSet = carrierList.toSet();
        comboCarrier.setLabel("Operadoras");
        comboCarrier.setClearButtonVisible(true);
        comboCarrier.setItems(carrierSet);
        comboCarrier.setItemLabelGenerator(Carrier::getCarrierCharcode);
        comboCarrier.setHelperText("Dejar en blanco para buscar en todas las operadoras.");
        /**/
        multi_systemIds.setI18n(I18nUtils.getMulticomboI18n());
        multi_systemIds.setLabel("Credenciales");
        multi_systemIds.setItemLabelGenerator(SystemId::getSystemId);
        if (currentUser.getUser().getUserTypeOrd() == User.OUSER_TYPE_ORDINAL.COMERCIAL) {
            multi_systemIds.setItems(systemIdSetGenericBean.getSet());
            multi_systemIds.setValue(systemIdSetGenericBean.getSet());
        } else {
            multi_systemIds.setItems(currentUser.getUser().getSystemids());
            multi_systemIds.setValue(currentUser.getUser().getSystemids());
        }
        multi_systemIds.setRequired(true);
        multi_systemIds.setHelperText("Seleccione al menos una credencial.");
        multi_systemIds.setErrorMessage("Se debe seleccionar al menos una credencial");
        multi_systemIds.addValueChangeListener(change -> {
            if (change.getValue().size() == 0) {
                multi_systemIds.setInvalid(true);
            } else {
                multi_systemIds.setInvalid(false);
            }
            searchButton.setEnabled(isValidSearch());
        });
        /**/
        checkboxMessageType.setLabel("Tipo de mensajes");
        checkboxMessageType.setItems(OMessageType.values());
        checkboxMessageType.setValue(new HashSet<OMessageType>(Arrays.asList(OMessageType.values())));
        checkboxMessageType.setRequired(true);
        checkboxMessageType.setErrorMessage("Seleccionar al menos un tipo de mensaje");
        checkboxMessageType.addValueChangeListener(change -> {
            if (checkboxMessageType.getValue().size() == 0) {
                checkboxMessageType.setInvalid(true);
            } else {
                checkboxMessageType.setInvalid(false);
            }
            searchButton.setEnabled(isValidSearch());
        });
        /**/
        textPhoneNumer.setLabel("Numero");
        textPhoneNumer.setHelperText("Dejar en blanco para buscar todos los numeros.");
        textPhoneNumer.setClearButtonVisible(true);
        /**/
        searchButton.setText("Buscar");
        /**/
        dateOne.setMin(LocalDate.now().minusMonths(1));
        dateOne.setMax(LocalDate.now());
        dateOne.setClearButtonVisible(true);
        dateOne.setRequired(true);
        dateOne.setInitialPosition(LocalDate.now());
        dateOne.setValue(new DateRange(LocalDate.now().minusDays(1), LocalDate.now()));
        dateOne.setSidePanelVisible(false);
        dateOne.setLabel("Rango de busqueda");
        dateOne.setPattern(" dd-MM-yyyy");
        /**/
        datePickerConf(firstDate, "Desde");
        datePickerConf(secondDate, "Hasta");
        /**/
        firstline.add(new HorizontalLayout(firstDate, secondDate, checkboxMessageType),
                new HorizontalLayout(textPhoneNumer, comboCarrier),
                new HorizontalLayout(multi_systemIds));
        textPhoneNumer.setWidthFull();
        checkboxMessageType.setWidthFull();
        comboCarrier.setWidthFull();
        multi_systemIds.setWidthFull();
        /**/
        createGrid();
        textPhoneNumer.addValueChangeListener(listener -> {
            if (!listener.isFromClient()) {
                return;
            }
            /* Todo: usar el servicio de admin.*/
            if (StringUtils.startsWith(listener.getValue(), "58414") ||
                    StringUtils.startsWith(listener.getValue(), "58424")) {
                Carrier carrier = searchCarrierbyName(carrierSet, "movistar");
                comboCarrier.setValue(carrier != null ? carrier : null);
            }
            if (StringUtils.startsWith(listener.getValue(), "58416") ||
                    StringUtils.startsWith(listener.getValue(), "58426")) {
                Carrier carrier = searchCarrierbyName(carrierSet, "movilnet");
                comboCarrier.setValue(carrier != null ? carrier : null);
            }
            if (StringUtils.startsWith(listener.getValue(), "58412")) {
                Carrier carrier = searchCarrierbyName(carrierSet, "digitel");
                comboCarrier.setValue(carrier != null ? carrier : null);
            }
        });
        searchButton.addClickListener(click -> {
            click.getSource().setEnabled(false);
            grid.setPageSize(itemsPerPage);
            try {
                abstractSMSList = getSms(0, itemsPerPage, true);
                dataProvider.getItems().clear();
                if (abstractSMSList != null) {
                    dataProvider.getItems().addAll(abstractSMSList);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            dataProvider.refreshAll();
            click.getSource().setEnabled(true);
            if (dataProvider.getItems().isEmpty()) {
                Message message = Message.NO_DATA.createMessage();
                ConfirmDialog confirmDialog = new ConfirmDialog();
                confirmDialog.setText(message.getMessage());
                confirmDialog.setHeader(message.getCaption());
                confirmDialog.setCancelText(message.getCancelText());
                confirmDialog.setConfirmText(message.getOkText());
                confirmDialog.setOpened(true);
                confirmDialog.addConfirmListener(e -> confirmDialog.close());
            }
        });
        /**/
        ComboBox<Integer> comboItemsPerPage = new ComboBox<>("Mensajes por página");
        comboItemsPerPage.setItems(Arrays.asList(25, 50, 100, 200, 400, 800));
        comboItemsPerPage.setValue(itemsPerPage);
        comboItemsPerPage.addValueChangeListener(change -> {
            if (change.isFromClient()) {
                itemsPerPage = change.getValue();
                try {
                    abstractSMSList = getSms(0, itemsPerPage, true);
                    dataProvider.getItems().clear();
                    grid.setPageSize(itemsPerPage);
                    if (abstractSMSList != null) {
                        dataProvider.getItems().addAll(abstractSMSList);
                    }
                    dataProvider.refreshAll();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        currentPageTextbox.setValue(1);
        currentPageTextbox.setMin(1);
        currentPageTextbox.setHasControls(true);
        currentPageTextbox.addValueChangeListener(change -> {
            if (change.isFromClient()) {
                try {
                    abstractSMSList = getSms(currentPageTextbox.getValue().intValue() - 1, itemsPerPage, false);
                    dataProvider.getItems().clear();
                    if (abstractSMSList != null) {
                        dataProvider.getItems().addAll(abstractSMSList);
                    }
                    dataProvider.refreshAll();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        HorizontalLayout h = new HorizontalLayout(comboItemsPerPage, currentPageTextbox, totalAmountOfPagesLabel);
        h.setVerticalComponentAlignment(FlexComponent.Alignment.END, totalAmountOfPagesLabel);
        footer.add(h);
    }

    private void datePickerConf(DatePicker datePicker, String label) {
        datePicker.setLabel(label);
        datePicker.setRequired(true);
        datePicker.setLocale(esLocale);
        datePicker.setValue(LocalDate.now());
        /**/
        datePicker.isRequired();
        /**/
        datePicker.addValueChangeListener(datePickerLocalDateComponentValueChangeEvent -> {
            searchButton.setEnabled(isValidSearch());
        });
    }

    private Carrier searchCarrierbyName(Set<Carrier> carrierSet, String movistar) {
        return carrierSet
                .stream()
                .filter(f -> f.getCarrierCharcode().equalsIgnoreCase(movistar))
                .findFirst()
                .orElse(null);
    }

    private Page<? extends AbstractSMS> getSmsPage(LocalDate dateOne,
                                                   LocalDate dateTwo,
                                                   int actualpage,
                                                   int itemsPerPage,
                                                   boolean updateView) {
        Page<? extends AbstractSMS> smsPage = null;
        Page<? extends AbstractSMS> smsPage2 = null;
        switch (getFindype()) {
            case 10:
                smsPage = sms_serv.findByMessageType(dateOne,
                        dateTwo,
                        getSystemIdString(multi_systemIds.getValue()),
                        valueOfMessageType(checkboxMessageType.getSelectedItems()),
                        actualpage, itemsPerPage);
                /*TODO: ELIMINAR ESTA SEGUNDA BUSQUEDA PARA LA DESCARGA. */
                smsPage2 = sms_serv.findByMessageType(dateOne,
                        dateTwo,
                        getSystemIdString(multi_systemIds.getValue()),
                        valueOfMessageType(checkboxMessageType.getSelectedItems()),
                        0, 5000000);
                break;
            case 11:
                smsPage = sms_serv.findByPhoneNumber(dateOne,
                        dateTwo,
                        getSystemIdString(multi_systemIds.getValue()),
                        valueOfMessageType(checkboxMessageType.getSelectedItems()),
                        textPhoneNumer.getValue().trim(),
                        actualpage, itemsPerPage);
                smsPage2 = sms_serv.findByPhoneNumber(dateOne,
                        dateTwo,
                        getSystemIdString(multi_systemIds.getValue()),
                        valueOfMessageType(checkboxMessageType.getSelectedItems()),
                        textPhoneNumer.getValue().trim(),
                        0, 5000000);
                break;
            case 14:
                smsPage = sms_serv.findByCarrierAndMessageType(dateOne,
                        dateTwo,
                        getSystemIdString(multi_systemIds.getValue()),
                        comboCarrier.getValue().getCarrierCharcode().trim(),
                        valueOfMessageType(checkboxMessageType.getSelectedItems()),
                        actualpage, itemsPerPage);
                smsPage2 = sms_serv.findByCarrierAndMessageType(dateOne,
                        dateTwo,
                        getSystemIdString(multi_systemIds.getValue()),
                        comboCarrier.getValue().getCarrierCharcode().trim(),
                        valueOfMessageType(checkboxMessageType.getSelectedItems()),
                        0, 5000000);
                break;
            case 15:
                smsPage = sms_serv.findByPhoneNumer(dateOne,
                        dateTwo,
                        getSystemIdString(multi_systemIds.getValue()),
                        textPhoneNumer.getValue().trim(),
                        valueOfMessageType(checkboxMessageType.getSelectedItems()),
                        comboCarrier.getValue().getCarrierCharcode().trim(),
                        actualpage);
                smsPage2 = sms_serv.findByPhoneNumer(dateOne,
                        dateTwo,
                        getSystemIdString(multi_systemIds.getValue()),
                        textPhoneNumer.getValue().trim(),
                        valueOfMessageType(checkboxMessageType.getSelectedItems()),
                        comboCarrier.getValue().getCarrierCharcode().trim(),
                        actualpage);
                break;
        }
        if (smsPage == null) {
            return null;
        }
        if (updateView) {
            updateDataView(smsPage);
        }
        /* PAGEABLE */
        currentPageSize = smsPage.getContent().size();
        currentElements = (int) smsPage.getTotalElements();
        currentPageCount = smsPage.getTotalPages();
        /* Crear boton descargar */
        updateDownloadButton(obtainAbstractOf(smsPage2));
        return smsPage;
    }

    private void updateDownloadButton(List<? extends AbstractSMS> messages) {
        if (componentWrapper != null) {
            footer.remove(componentWrapper);
        }
        componentWrapper = getDownloadButton(messages);
        footer.add(componentWrapper);
    }

    private Component getDownloadButton(List<? extends AbstractSMS> messages) {
        int year = localDateTime.getYear();
        int month = localDateTime.getMonthValue();
        int day = localDateTime.getDayOfMonth();
        int hour = localDateTime.getHour();
        int min = localDateTime.getMinute();
        String fileName = "" + year + "." + month + "." + day + "." + hour + ":" + min + "-Mensajes.csv";
        Button download = new Button("Descargar Datos (" + year + "/" + month + "/" + day + "-" + hour + ":" + min + ")");

        FileDownloadWrapper buttonWrapper = new FileDownloadWrapper(
                new StreamResource(fileName, () -> {
                    return new ByteArrayInputStream(getStringData(messages).getBytes());
                }
                )
        );
        download.addClickListener(click -> {
            LocalDate selectedStartDate = firstDate.getValue();
            LocalDate selectedEndDate = secondDate.getValue();
            if (selectedStartDate == null || selectedEndDate == null) {
                return;
            }

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
     * phonenumber -> 1
     * systemid->2 -> siempre
     * carrier->4
     * messagetype->8 -> siempre
     * **************************
     * 10: Tipo de mensaje y SystemId.
     * 11: Tipo de mensaje, SystemId y num de telefono.
     * 14: tipo de mensaje, Systemid y operadora.
     * 15: tipo de mensaje, Systemid, num de telefono y operadora.
     *
     * @return
     */
    private int getFindype() {
        int n = 10;

        if (StringUtils.isNotBlank(textPhoneNumer.getValue())) {
            n += 1;
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
        /*TODO: Cambiar a CSVFormat standard*/
        StringBuilder sb = new StringBuilder("\"destino\",\"fecha\",\"tipo de mensaje\",\"mensaje\",\"id recibido\",\"id enviando\",\"origen\",\"credencial\",\"operadora\"\n");

        for (AbstractSMS msg : messages) {
            sb.append(msg.getDestination()).append(",");
            sb.append(ODateUitls.dd_MM_yyyy_HH_mm_SS.format(msg.getDate())).append(",");
            sb.append(msg.getMessageType()).append(",");
            if (hasAuthToViewMsgTextColumn) {
                sb.append(msg.getMessagesText()).append(",");
            } else {
                sb.append("").append(",");
            }
            sb.append(msg.getMsgReceived()).append(",");
            sb.append(msg.getMsgSended()).append(",");
            sb.append(msg.getSource()).append(",");
            sb.append(msg.getSystemId()).append(",");
            sb.append(msg.getCarrierCharCode());
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

    private List<AbstractSMS> getSms(int actualpage, int itemsPerPage, boolean updateDataView) {
        LocalDate selectedStartDate = firstDate.getValue();
        LocalDate selectedEndDate = secondDate.getValue();
        if (selectedEndDate == null || selectedEndDate == null) {
            return Collections.emptyList();
        }
        return obtainAbstractOf(getSmsPage(selectedStartDate, selectedEndDate, actualpage, itemsPerPage, updateDataView));
    }

    private void addColumnsToGrid() {
        createPhoneNumberColumn();
        createSystemIdColumn();
        createCarrierColumn();
        if (!hasAuthToViewMsgTextColumn) {
            createMessageypeColumn();
        } else {
            createMessageypeAndMsgTExtColumn();
        }
        createDateColumn();
    }

    private void createPhoneNumberColumn() {
        phoneColum = grid.addColumn(TemplateRenderer.<AbstractSMS>of(
                                "<div><b>[[item.dest]]</b><br><small>[[item.source]]</small></div>")
                        .withProperty("dest", col -> {
                            return col.getDestination();
                        })
                        .withProperty("source", AbstractSMS::getSource))
                .setComparator(client -> client.getDestination()).setHeader("Destino / Origen")
                .setWidth("180px").setFlexGrow(0);
    }

    private void createCarrierColumn() {
        carrierColum = grid
                .addColumn(AbstractSMS::getCarrierCharCode)
                .setHeader("Operadora")
                .setAutoWidth(true);
    }

    private void createSystemIdColumn() {
        systemIdColumn = grid
                .addColumn(AbstractSMS::getSystemId)
                .setComparator(client -> client.getSystemId()).setHeader("Credencial")
                .setAutoWidth(true);
    }

    private void createMessageypeColumn() {

        messageTypeColum = grid
                .addColumn(AbstractSMS::getMessageType)
                .setComparator(client -> client.getMessageType())
                .setHeader("Tipo de mensaje")
                .setAutoWidth(true);
    }

    private void createDateColumn() {
        dateColumn = grid
                .addColumn(new LocalDateTimeRenderer<>(
                        client -> client.getDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime(),
                        DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss.SSS")))
                .setComparator(AbstractSMS::getDate).setHeader("Fecha de envío")
                .setAutoWidth(true);
    }

    private void createMessageypeAndMsgTExtColumn() {
        messageTypeColum = grid
                .addColumn(TemplateRenderer.<AbstractSMS>of(
                                "<div><small><b>[[item.msgtype]]</b></small><br><small>[[item.msgtext]]</small></div>")
                        .withProperty("msgtype", col -> {
                            return col.getMessageType();
                        })
                        .withProperty("msgtext", AbstractSMS::getMessagesText))
                .setComparator(client -> client.getMessageType()).setHeader("Tipo de mensaje / mensaje")
                .setAutoWidth(true);
    }

    private void addFiltersToGrid() {
//        HeaderRow filterRow = grid.appendHeaderRow();

        TextField idFilter = new TextField();
        idFilter.setPlaceholder("Filter");
        idFilter.setClearButtonVisible(true);
        idFilter.setWidth("100%");
        idFilter.setValueChangeMode(ValueChangeMode.EAGER);
        idFilter.addValueChangeListener(
                event -> dataProvider.addFilter(client -> StringUtils
                        .containsIgnoreCase(Long.toString(client.getId()),
                                idFilter.getValue())));
//        filterRow.getCell(idColumn).setComponent(idFilter);

        TextField phoneFilter = new TextField();
        phoneFilter.setPlaceholder("Filter");
        phoneFilter.setClearButtonVisible(true);
        phoneFilter.setWidth("100%");
        phoneFilter.setValueChangeMode(ValueChangeMode.EAGER);
        phoneFilter.addValueChangeListener(event -> dataProvider.addFilter(
                client -> StringUtils.containsIgnoreCase(client.getDestination(),
                        phoneFilter.getValue())));
//        filterRow.getCell(phoneColum).setComponent(phoneFilter);

        TextField carrierFilter = new TextField();
        carrierFilter.setPlaceholder("Filter");
        carrierFilter.setClearButtonVisible(true);
        carrierFilter.setWidth("100%");
        carrierFilter.setValueChangeMode(ValueChangeMode.EAGER);
        carrierFilter.addValueChangeListener(event -> dataProvider.addFilter(
                client -> StringUtils.containsIgnoreCase(client.getCarrierCharCode(),
                        carrierFilter.getValue())));
//        filterRow.getCell(carrierColum).setComponent(carrierFilter);

        TextField systemIdFilter = new TextField();
        systemIdFilter.setPlaceholder("Filter");
        systemIdFilter.setClearButtonVisible(true);
        systemIdFilter.setWidth("100%");
        systemIdFilter.setValueChangeMode(ValueChangeMode.EAGER);
        systemIdFilter.addValueChangeListener(event -> dataProvider.addFilter(
                client -> StringUtils.containsIgnoreCase(client.getSystemId(),
                        systemIdFilter.getValue())));
//        filterRow.getCell(systemIdColumn).setComponent(systemIdFilter);

        TextField messageTypeFilter = new TextField();
//        messageTypeFilter.setItems(Arrays.asList("Pending", "Success", "Error"));
        messageTypeFilter.setPlaceholder("Filter");
        messageTypeFilter.setClearButtonVisible(true);
        messageTypeFilter.setWidth("100%");
        systemIdFilter.setValueChangeMode(ValueChangeMode.EAGER);
        messageTypeFilter.addValueChangeListener(event -> dataProvider.addFilter(
                client -> StringUtils.containsIgnoreCase(client.getMessageType(),
                        messageTypeFilter.getValue())));
//        filterRow.getCell(messageTypeColum).setComponent(messageTypeFilter);

        DatePicker dateFilter = new DatePicker();
        dateFilter.setI18n(I18nUtils.getDatepickerI18n());
        dateFilter.setPlaceholder("Filter");
        dateFilter.setClearButtonVisible(true);
        dateFilter.setWidth("100%");
        dateFilter.addValueChangeListener(event -> dataProvider
                .addFilter(client -> areDatesEqual(client, dateFilter)));
//        filterRow.getCell(dateColumn).setComponent(dateFilter);
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
//        addFiltersToGrid();
    }

    private void createGridComponent() {
//        grid = new GridPro<>();
        grid.setSelectionMode(Grid.SelectionMode.NONE);
        grid.addThemeVariants(GridVariant.LUMO_NO_BORDER,
                GridVariant.LUMO_COLUMN_BORDERS);
        grid.setHeightFull();
        grid.setWidthFull();
        abstractSMSList = new ArrayList<>();
        dataProvider = new ListDataProvider<>(abstractSMSList);
        grid.setDataProvider(dataProvider);
        grid.setPageSize(itemsPerPage);
        footerRow = grid.appendFooterRow();
    }

    private Page<AbstractSMS> updateDataPage(int itemsPerPage, int page) {
        Page<AbstractSMS> pageSms = sms_serv.findAll(page, itemsPerPage);
        grid.setPageSize(itemsPerPage);
        grid.setItems(pageSms.getContent());
        return pageSms;
    }

    private void updateDataView(Page<? extends AbstractSMS> pageSms) {
        /**/
        currentPageTextbox.setValue(1);
        currentPageTextbox.setMin(1);
        currentPageTextbox.setMax(pageSms.getTotalPages());
        /**/
        totalAmountOfPagesLabel.setText(" de " + pageSms.getTotalPages());
    }

    private boolean isValidSearch() {
        return (!checkboxMessageType.isInvalid() && !multi_systemIds.isInvalid());
    }

    private boolean isGrantedMsgTextColumn(Set<ORole> roles) {
        return roles.stream().filter(rol -> {
            return rol.getAuthorities().stream().filter(auth -> {
                        return auth.getAuthName().equalsIgnoreCase("VIEW_MSG_TEXT");
                    })
                    .findFirst()
                    .isPresent();
        }).findFirst().isPresent();
    }
}
