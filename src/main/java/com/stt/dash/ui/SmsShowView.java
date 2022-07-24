package com.stt.dash.ui;

import com.stt.dash.app.security.CurrentUser;
import com.stt.dash.app.session.ListGenericBean;
import com.stt.dash.backend.data.entity.Client;
import com.stt.dash.backend.data.entity.ORole;
import com.stt.dash.backend.data.entity.SystemId;
import com.stt.dash.backend.data.entity.User;
import com.stt.dash.backend.data.entity.sms.AbstractSMS;
import com.stt.dash.backend.service.AbstractSmsService;
import com.stt.dash.ui.utils.BakeryConst;
import com.stt.dash.ui.utils.I18nUtils;
import com.stt.dash.ui.utils.ODateUitls;
import com.stt.dash.ui.utils.messages.Message;
import com.stt.dash.ui.views.HasConfirmation;
import com.vaadin.componentfactory.DateRange;
import com.vaadin.componentfactory.EnhancedDateRangePicker;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.Tag;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.charts.model.HorizontalAlign;
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
import com.vaadin.flow.data.provider.ListDataProvider;
import com.vaadin.flow.data.renderer.LocalDateTimeRenderer;
import com.vaadin.flow.data.renderer.TemplateRenderer;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.StreamResource;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.vaadin.olli.FileDownloadWrapper;

import java.io.ByteArrayInputStream;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Set;

@Tag("sms-show-view")
@JsModule("./src/views/smsview/sms-show-view.ts")
@Route(value = BakeryConst.PAGE_SMS_SHOW_VIEW, layout = MainView.class)
@PageTitle(BakeryConst.TITLE_SMS_SHOW_VIEW)
public class SmsShowView extends LitTemplate {
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
    private final SmsShowPresenter presenter;
    /**/
    private Component componentWrapper;
    /* Hora del servidor para establecer busquedas de YYYY-MM-DD*/
    public static LocalDateTime localDateTime = LocalDateTime.now();
    /**/
    private EnhancedDateRangePicker dateOne = new EnhancedDateRangePicker();
    private DatePicker firstDate = new DatePicker();
    private DatePicker secondDate = new DatePicker();
    /**/
//    private Button searchButton = new Button("Buscar");
    private IntegerField currentPageTextbox = new IntegerField("Página");
    private Label totalAmountOfPagesLabel = new Label();
    ComboBox<Integer> comboItemsPerPage = new ComboBox<>("Mensajes por página");
    private final ComboBox<Client> clientCombobox = new ComboBox<>("Cliente");
    FooterRow footerRow;
    /**/
    private int itemsPerPage = 25;
    List<SystemId> systemIdList = new ArrayList<>(1);
    /**/
    private Grid.Column<AbstractSMS> phoneColum;
    private Grid.Column<AbstractSMS> carrierColum;
    private Grid.Column<AbstractSMS> systemIdColumn;
    private Grid.Column<AbstractSMS> messageTypeColum;
    private Grid.Column<AbstractSMS> dateColumn;
    /**/
    private boolean hasAuthToViewMsgTextColumn = false;

    public SmsShowView(@Autowired CurrentUser currentUser,
                       @Autowired AbstractSmsService service,
                       @Qualifier("getUserSystemIdString") ListGenericBean<String> stringListGenericBean) {
        presenter = new SmsShowPresenter(service, this);
        hasAuthToViewMsgTextColumn = isGrantedMsgTextColumn(currentUser.getUser().getRoles());
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
        firstDate.setI18n(I18nUtils.getDatepickerI18n());
        firstDate.setLabel("Desde");
        firstDate.setRequired(true);
        firstDate.setLocale(esLocale);
        secondDate.setLabel("Hasta");
        secondDate.setI18n(I18nUtils.getDatepickerI18n());
        secondDate.setRequired(true);
        secondDate.setLocale(esLocale);
        /**/
        searchButton.setText("Buscar");

//        dateOne.setWidthFull();
        /**/
        createGridComponent();
        /**/
        createGrid();
        /* ******* Client */
        clientCombobox.setItemLabelGenerator(Client::getClientName);
        clientCombobox.setWidth("100%");
        /* Client & Systemids*/
        if (currentUser.getUser().getUserType() == User.OUSER_TYPE.HAS) {
            clientCombobox.setItems(currentUser.getUser().getClients());
        } else if (currentUser.getUser().getUserType() == User.OUSER_TYPE.IS) {
            clientCombobox.setItems(currentUser.getUser().getClient());
            /*TODO: Seleccionar por defecto el unico cliente. Validar que no este vacio.*/
            clientCombobox.setReadOnly(true);
        }
        /**/
        comboItemsPerPage.setItems(Arrays.asList(25, 50, 100, 200, 400, 800));
        comboItemsPerPage.setValue(itemsPerPage);
        comboItemsPerPage.addValueChangeListener(change -> {
            if (change.isFromClient()) {
                itemsPerPage = change.getValue();
                presenter.updateDataProviderPagin(firstDate.getValue(),
                        secondDate.getValue(),
                        systemIdList,
                        currentPageTextbox.getValue().intValue() - 1, itemsPerPage);
                try {
                    grid.setPageSize(itemsPerPage);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        /**/
        currentPageTextbox.setValue(1);
        currentPageTextbox.setMin(1);
        currentPageTextbox.setHasControls(true);
        currentPageTextbox.addValueChangeListener(change -> {
            if (change.isFromClient()) {
                try {
                    presenter.updateDataProvider(firstDate.getValue(),
                            secondDate.getValue(),
                            systemIdList,
                            currentPageTextbox.getValue().intValue() - 1, itemsPerPage);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        /**/
        firstline.add(new HorizontalLayout(firstDate, secondDate), clientCombobox);
        HorizontalLayout h = new HorizontalLayout(comboItemsPerPage, currentPageTextbox, totalAmountOfPagesLabel);
        h.setVerticalComponentAlignment(FlexComponent.Alignment.END, totalAmountOfPagesLabel);
        footer.add(h);
        addValueChangeListener();
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

    private void addValueChangeListener() {
        searchButton.addClickListener(click -> {
            click.getSource().setEnabled(false);
            presenter.updateDataProviderPagin(firstDate.getValue(),
                    secondDate.getValue(),
                    systemIdList,
                    currentPageTextbox.getValue().intValue() - 1, itemsPerPage);
            grid.setPageSize(itemsPerPage);
            click.getSource().setEnabled(true);
            ListDataProvider<AbstractSMS> dataProvider = (ListDataProvider<AbstractSMS>) grid.getDataProvider();
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

        clientCombobox.addValueChangeListener(clientListener -> {
            if (CollectionUtils.isEmpty(clientListener.getValue().getSystemids())) {
                systemIdList = new ArrayList<>(1);
                return;
            }
            systemIdList.clear();
            systemIdList.addAll(clientListener.getValue().getSystemids());
        });
    }

    public void updateDownloadButton(List<? extends AbstractSMS> messages) {
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

    public void setGridDataProvider(ListDataProvider<AbstractSMS> dataProvider) {
        grid.setDataProvider(dataProvider);
    }

    public void setGridPageSize(int pageSize) {
        grid.setPageSize(pageSize);
    }

    private void createGrid() {
        createGridComponent();
        addColumnsToGrid();
    }

    private void createGridComponent() {
        grid.setSelectionMode(Grid.SelectionMode.NONE);
        grid.addThemeVariants(GridVariant.LUMO_NO_BORDER,
                GridVariant.LUMO_COLUMN_BORDERS);
        grid.setHeightFull();
        grid.setWidthFull();
        footerRow = grid.appendFooterRow();
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
                .setHeader("tipo de mensaje")
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

    private void createDateColumn() {
        dateColumn = grid
                .addColumn(new LocalDateTimeRenderer<>(
                        client -> client.getDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime(),
                        DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss.SSS")))
                .setComparator(AbstractSMS::getDate).setHeader("Fecha de envío")
                .setAutoWidth(true);
    }

    public void updateTotalPage(int totalSmsPage) {
        /**/
        currentPageTextbox.setValue(1);
        currentPageTextbox.setMin(1);
        currentPageTextbox.setMax(totalSmsPage);
        /**/
        totalAmountOfPagesLabel.setText(" de " + totalSmsPage);
    }
}
