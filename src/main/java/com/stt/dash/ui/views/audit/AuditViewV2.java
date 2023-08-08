package com.stt.dash.ui.views.audit;

import com.stt.dash.app.security.CurrentUser;
import com.stt.dash.app.session.ListGenericBean;
import com.stt.dash.backend.data.Role;
import com.stt.dash.backend.data.entity.Client;
import com.stt.dash.backend.data.entity.ODashAuditEvent;
import com.stt.dash.backend.data.entity.SystemId;
import com.stt.dash.backend.data.entity.User;
import com.stt.dash.backend.service.ODashAuditEventService;
import com.stt.dash.ui.MainView;
import com.stt.dash.ui.utils.BakeryConst;
import com.stt.dash.ui.utils.I18nUtils;
import com.stt.dash.ui.utils.ODateUitls;
import com.stt.dash.ui.utils.messages.Message;
import com.vaadin.componentfactory.EnhancedDateRangePicker;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.Tag;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.confirmdialog.ConfirmDialog;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.dependency.JsModule;
import com.vaadin.flow.component.grid.FooterRow;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Hr;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.littemplate.LitTemplate;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.template.Id;
import com.vaadin.flow.component.textfield.IntegerField;
import com.vaadin.flow.data.provider.ListDataProvider;
import com.vaadin.flow.data.renderer.LocalDateTimeRenderer;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.StreamResource;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.access.annotation.Secured;
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
import java.util.Objects;
import java.util.stream.Collectors;

import static com.stt.dash.ui.utils.BakeryConst.PAGE_AUDIT;

@Tag("sms-show-view")
@JsModule("./src/views/smsview/sms-show-view.js")
@PageTitle(BakeryConst.TITLE_AUDIT)
@Route(value = PAGE_AUDIT, layout = MainView.class)
@Secured({Role.ADMIN, "UI_AUDIT"})
public class AuditViewV2 extends LitTemplate {
    @Id("firstline")
    Div firstline;

    @Id("secondline")
    Div secondline;
    @Id("filterButton")
    private Button searchButton;
    @Id("footer")
    Div footer;
    @Id("smsGrid")
    Grid<ODashAuditEvent> grid;
    /**/
    private Locale esLocale = new Locale("es", "ES");
    /**/
    private final AuditPresenter presenter;
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
    private ComboBox<User> userCombo = new ComboBox<>();
    private ComboBox<ODashAuditEvent.OEVENT_TYPE> eventCombo = new ComboBox<>();
    private Checkbox allUserCheck = new Checkbox("Todos los usuarios");
    private Checkbox allEventCheck = new Checkbox("Todos los eventos");
    /**/
    private Grid.Column<ODashAuditEvent> userColumn;
    private Grid.Column<ODashAuditEvent> eventTypeColumn;
    private Grid.Column<ODashAuditEvent> dateColumn;
    private Grid.Column<ODashAuditEvent> eventDescColumn;
    /**/
    private final List<String> userChildren = new ArrayList<>();

    public AuditViewV2(@Autowired CurrentUser currentUser,
                       @Qualifier("getAllUsers") ListGenericBean<User> allUsers,
                       @Autowired ListGenericBean<User> mychildren,
                       @Autowired ODashAuditEventService service,
                       @Qualifier("getUserSystemIdString") ListGenericBean<String> stringListGenericBean) {
        presenter = new AuditPresenter(service, this);
        initDatepicker();
        /**/
        createGridComponent();
        /**/
        createGrid();
        /**/
        List<User> user;
        user = findUsers(currentUser, allUsers, mychildren);
        userChildren.addAll(user.stream().map(User::getEmail).collect(Collectors.toList()));
        /**/
        initCombo(user);
        initEventCombo();
        initEventCheck();
        /**/
        Label titleSpan = new Label("Auditoría de Eventos");
        Span s = new Span(titleSpan, new Hr());
        s.setWidthFull();
        /**/
        comboItemsPerPage.setItems(Arrays.asList(25, 50, 100, 200, 400, 800));
        comboItemsPerPage.setValue(itemsPerPage);
        comboItemsPerPage.addValueChangeListener(change -> {
            if (change.isFromClient()) {
                itemsPerPage = change.getValue();
                if (Objects.isNull(userCombo.getValue())) {
                    /* Todos los eventos */
                    if (Objects.isNull(eventCombo.getValue())) {
                        presenter.updateDataProviderPagin(firstDate.getValue(), secondDate.getValue(), currentPageTextbox.getValue().intValue() - 1, itemsPerPage);
                    } else {
                        presenter.updateDataProviderPagin(firstDate.getValue(), secondDate.getValue(), userChildren, eventCombo.getValue(), currentPageTextbox.getValue().intValue() - 1, itemsPerPage);
                    }
                } else {
                    /* Todos los eventos */
                    if (ObjectUtils.isEmpty(eventCombo.getValue())) {
                        presenter.updateDataProviderPagin(firstDate.getValue(), secondDate.getValue(), userCombo.getValue().getEmail(), currentPageTextbox.getValue().intValue() - 1, itemsPerPage);
                    } else {
                        presenter.updateDataProviderPagin(firstDate.getValue(), secondDate.getValue(), userCombo.getValue().getEmail(), currentPageTextbox.getValue().intValue() - 1, itemsPerPage);
                    }
                }
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
                    if (Objects.isNull(userCombo.getValue())) {
                        /* Todos los eventos */
                        if (Objects.isNull(eventCombo.getValue())) {
                            presenter.updateDataProvider(firstDate.getValue(), secondDate.getValue(), currentPageTextbox.getValue().intValue() - 1, itemsPerPage);
                        } else {
                            presenter.updateDataProvider(firstDate.getValue(), secondDate.getValue(), userChildren, eventCombo.getValue(), currentPageTextbox.getValue().intValue() - 1, itemsPerPage);
                        }
                    } else {
                        /* Todos los eventos */
                        if (ObjectUtils.isEmpty(eventCombo.getValue())) {
                            presenter.updateDataProvider(firstDate.getValue(), secondDate.getValue(), userCombo.getValue().getEmail(), currentPageTextbox.getValue().intValue() - 1, itemsPerPage);
                        } else {
                            presenter.updateDataProvider(firstDate.getValue(), secondDate.getValue(), userCombo.getValue().getEmail(), currentPageTextbox.getValue().intValue() - 1, itemsPerPage);
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        /**/
        firstline.add(new HorizontalLayout(firstDate, secondDate));
        secondline.add(userCombo, allUserCheck, eventCombo, allEventCheck);
        /* Footer; Pages */
        HorizontalLayout h = new HorizontalLayout(comboItemsPerPage, currentPageTextbox, totalAmountOfPagesLabel);
        h.setVerticalComponentAlignment(FlexComponent.Alignment.END, totalAmountOfPagesLabel);
        footer.add(h);
        addValueChangeListener();
        /**/
        searchButton.setEnabled(false);
    }

    private static List<User> findUsers(CurrentUser currentUser, ListGenericBean<User> allUsers, ListGenericBean<User> mychildren) {
        List<User> user;
        if (currentUser.getUser().getUserTypeOrd().equals(User.OUSER_TYPE_ORDINAL.COMERCIAL)) {
            user = allUsers.getList();
        } else {
            user = mychildren.getList();
            user.remove(currentUser.getUser());
        }
        return user;
    }

    private void initDatepicker() {
        firstDate.setI18n(I18nUtils.getDatepickerI18n());
        firstDate.setLabel("Desde");
        firstDate.setRequired(true);
        firstDate.setLocale(esLocale);
        firstDate.addValueChangeListener(listener -> searchButton.setEnabled(isValid()));
        firstDate.setValue(localDateTime.toLocalDate());
        /**/
        secondDate.setLabel("Hasta");
        secondDate.setI18n(I18nUtils.getDatepickerI18n());
        secondDate.setRequired(true);
        secondDate.setLocale(esLocale);
        secondDate.addValueChangeListener(listener -> searchButton.setEnabled(isValid()));
        secondDate.setValue(localDateTime.toLocalDate());
    }

    private void addValueChangeListener() {
        searchButton.addClickListener(click -> {
            click.getSource().setEnabled(false);
            if (allUserCheck.getValue()) {
                /* Un Evento. Todos los usuarios.*/
                presenter.updateDataProviderPagin(firstDate.getValue(),
                        secondDate.getValue(),
                        userChildren,
                        eventCombo.getValue(),
                        currentPageTextbox.getValue() - 1,
                        itemsPerPage);
            } else if (allEventCheck.getValue()) {
                /* Un usuario. Todos sus eventos. */
                presenter.updateDataProviderPagin(firstDate.getValue(),
                        secondDate.getValue(),
                        userCombo.getValue().getEmail(),
                        currentPageTextbox.getValue() - 1,
                        itemsPerPage);
            } else {
                /* Un usuario. Un evento. */
                presenter.updateDataProviderPagin(firstDate.getValue(),
                        secondDate.getValue(),
                        userCombo.getValue().getEmail(),
                        eventCombo.getValue(),
                        currentPageTextbox.getValue() - 1,
                        itemsPerPage);
            }
//            if (Objects.isNull(userCombo.getValue())) {
//                /* Todos los usuarios */
//                if (Objects.isNull(eventCombo.getValue())) {
//                    presenter.updateDataProviderPagin(firstDate.getValue(), secondDate.getValue(), currentPageTextbox.getValue().intValue() - 1, itemsPerPage);
//                } else {
//                    presenter.updateDataProviderPagin(firstDate.getValue(), secondDate.getValue(), userChildren, eventCombo.getValue(), currentPageTextbox.getValue().intValue() - 1, itemsPerPage);
//                }
//            } else {
//                if (ObjectUtils.isEmpty(eventCombo.getValue())) {
//                    /* Todos los eventos */
//                    presenter.updateDataProviderPagin(firstDate.getValue(), secondDate.getValue(), userCombo.getValue().getEmail(), currentPageTextbox.getValue().intValue() - 1, itemsPerPage);
//                } else {
//                    presenter.updateDataProviderPagin(firstDate.getValue(), secondDate.getValue(), userCombo.getValue().getEmail(), currentPageTextbox.getValue().intValue() - 1, itemsPerPage);
//                }
//            }
            grid.setPageSize(itemsPerPage);
            /**/
            click.getSource().setEnabled(true);
            /**/
            ListDataProvider<ODashAuditEvent> dataProvider = (ListDataProvider<ODashAuditEvent>) grid.getDataProvider();
            /**/
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
            clientCombobox.addCustomValueSetListener(listener -> searchButton.setEnabled(isValid()));
            if (CollectionUtils.isEmpty(clientListener.getValue().getSystemids())) {
                systemIdList = new ArrayList<>(1);
                return;
            }
            systemIdList.clear();
            systemIdList.addAll(clientListener.getValue().getSystemids());
        });
    }

    public void updateDownloadButton(List<ODashAuditEvent> messages) {
        if (componentWrapper != null) {
            footer.remove(componentWrapper);
        }
        componentWrapper = getDownloadButton(messages);
        footer.add(componentWrapper);
    }

    private Component getDownloadButton(List<ODashAuditEvent> messages) {
        LocalDateTime now = LocalDateTime.now();
        int year = now.getYear();
        int month = now.getMonthValue();
        int day = now.getDayOfMonth();
        int hour = now.getHour();
        int min = now.getMinute();
        String fileName = "" + year + "." + month + "." + day + "." + hour + ":" + (min < 9 ? "0" + min : min) + "-Auditoria.csv";
        Button download = new Button("Descargar Datos (" + year + "/" + month + "/" + day + "-" + hour + ":" + (min < 9 ? "0" + min : min) + ")");

        FileDownloadWrapper buttonWrapper = new FileDownloadWrapper(new StreamResource(fileName, () -> {
            return new ByteArrayInputStream(getStringData(messages).getBytes());
        }));
        download.addClickListener(click -> {
            LocalDate selectedStartDate = (dateOne.getValue() == null) ? null : dateOne.getValue().getStartDate();
            LocalDate selectedEndDate = (dateOne.getValue() == null) ? null : dateOne.getValue().getEndDate();
            StringBuilder sb = new StringBuilder();
            sb.append("Desde: ").append(ODateUitls.dd_MM_yyyy.format(ODateUitls.valueOf(selectedStartDate))).append(" Hasta: ").append(ODateUitls.dd_MM_yyyy.format(ODateUitls.valueOf(selectedEndDate)));
//            auditEvent.add(ODashAuditEvent.OEVENT_TYPE.DOWNLOAD_FILE_SEARCH_SMS, sb.toString());
        });
        buttonWrapper.wrapComponent(download);
        return buttonWrapper;
    }

    public String getStringData(List<ODashAuditEvent> messages) {
        if (messages.size() > 5000000) {
            System.out.println("Daily message limit reached. Code not able to handle this size of string.");
            return "";
        }
        /*TODO: Cambiar a CSVFormat standard*/
        StringBuilder sb = new StringBuilder("\"usuario\",\"fecha\",\"evento\",\"descripcion\"\n");

        for (ODashAuditEvent msg : messages) {
            sb.append(msg.getPrincipal()).append(",");
            sb.append(ODateUitls.dd_MM_yyyy_HH_mm_SS.format(msg.getEventDate())).append(",");
            sb.append(msg.getEventType()).append(",");
            sb.append("\"").append(msg.getEventDesc()).append(",").append("\"");
            sb.append("\n");
        }
        return sb.toString();
    }

    public void setGridDataProvider(ListDataProvider<ODashAuditEvent> dataProvider) {
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
        grid.addThemeVariants(GridVariant.LUMO_NO_BORDER, GridVariant.LUMO_COLUMN_BORDERS);
        grid.setHeightFull();
        grid.setWidthFull();
        footerRow = grid.appendFooterRow();
    }

    private void addColumnsToGrid() {
        createUserColumn();
        createEventColumn();
        createDateColumn();
        createEventDescColumn();
    }

    private void createUserColumn() {
        userColumn = grid.addColumn(ODashAuditEvent::getPrincipal).setHeader("Usuario").setAutoWidth(true);
    }

    private void createEventColumn() {
        eventTypeColumn = grid.addColumn(ODashAuditEvent::getEventType).setHeader("Evento").setAutoWidth(true);
    }

    private void createDateColumn() {
        dateColumn = grid.addColumn(new LocalDateTimeRenderer<>(client -> client.getEventDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime(), DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss.SSS"))).setComparator(ODashAuditEvent::getEventDate).setHeader("Fecha del evento").setAutoWidth(true);
    }

    private void createEventDescColumn() {
        eventDescColumn = grid.addColumn(ODashAuditEvent::getEventDesc).setHeader("Descripción").setAutoWidth(true);
    }

    public void updateTotalPage(int totalSmsPage) {
        /**/
        currentPageTextbox.setValue(1);
        currentPageTextbox.setMin(1);
        currentPageTextbox.setMax(totalSmsPage);
        /**/
        totalAmountOfPagesLabel.setText(" de " + totalSmsPage);
    }

    private void initCombo(List<User> userList) {
        userCombo.setItems(userList);
        userCombo.setClearButtonVisible(true);
        userCombo.setItemLabelGenerator(User::getEmail);
        userCombo.setWidthFull();
        userCombo.addValueChangeListener(listener -> {
            searchButton.setEnabled(isValid());
        });
    }

    private void initEventCombo() {
        eventCombo.setItems(ODashAuditEvent.OEVENT_TYPE.values());
        eventCombo.setClearButtonVisible(true);
        eventCombo.setItemLabelGenerator(ODashAuditEvent.OEVENT_TYPE::name);
        eventCombo.setWidthFull();
        eventCombo.addCustomValueSetListener(listener -> searchButton.setEnabled(isValid()));
        eventCombo.addValueChangeListener(listener -> searchButton.setEnabled(isValid()));
    }

    private void initEventCheck() {
        allUserCheck.addValueChangeListener(listener -> {
            if (allUserCheck.getValue()) {
                userCombo.setValue(null);
                allEventCheck.setValue(false);
            }
            userCombo.setEnabled(!allUserCheck.getValue());
            searchButton.setEnabled(isValid());

        });
        /**/
        allEventCheck.addValueChangeListener(listener -> {
            if (allUserCheck.getValue()) {
                eventCombo.setValue(null);
                allUserCheck.setValue(false);
            }
            eventCombo.setEnabled(!allEventCheck.getValue());
            searchButton.setEnabled(isValid());
        });
    }

    /**
     * @return
     */
    private boolean isValid() {
        if ((Objects.isNull(userCombo.getValue()) && !allUserCheck.getValue()) || (Objects.isNull(eventCombo.getValue()) && !allEventCheck.getValue()) || Objects.isNull(firstDate.getValue()) || Objects.isNull(secondDate.getValue())) {
            return false;
        }
        return true;
    }
}
