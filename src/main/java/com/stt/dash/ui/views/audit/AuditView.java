package com.stt.dash.ui.views.audit;

import com.opencsv.bean.StatefulBeanToCsv;
import com.opencsv.bean.StatefulBeanToCsvBuilder;
import com.opencsv.exceptions.CsvDataTypeMismatchException;
import com.opencsv.exceptions.CsvRequiredFieldEmptyException;
import com.stt.dash.app.security.CurrentUser;
import com.stt.dash.app.session.ListGenericBean;
import com.stt.dash.backend.data.OUserSession;
import com.stt.dash.backend.data.Role;
import com.stt.dash.backend.data.entity.MyAuditEventComponent;
import com.stt.dash.backend.data.entity.ODashAuditEvent;
import com.stt.dash.backend.data.entity.User;
import com.stt.dash.backend.repositories.UserRepository;
import com.stt.dash.backend.service.ODashAuditEventService;
import com.stt.dash.backend.service.OUserService;
import com.stt.dash.ui.MainView;
import com.stt.dash.ui.utils.BakeryConst;
import com.stt.dash.ui.utils.ODateUitls;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.details.Details;
import com.vaadin.flow.component.details.DetailsVariant;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridSortOrder;
import com.vaadin.flow.component.html.Hr;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.provider.DataProvider;
import com.vaadin.flow.data.provider.ListDataProvider;
import com.vaadin.flow.data.provider.Query;
import com.vaadin.flow.data.provider.QuerySortOrder;
import com.vaadin.flow.data.renderer.TemplateRenderer;
import com.vaadin.flow.function.SerializableComparator;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.StreamResource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.access.annotation.Secured;
import org.vaadin.olli.FileDownloadWrapper;

import java.io.ByteArrayInputStream;
import java.io.StringWriter;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.function.BinaryOperator;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.stt.dash.ui.utils.BakeryConst.PAGE_AUDIT;

@Route(value = PAGE_AUDIT, layout = MainView.class)
@PageTitle(BakeryConst.TITLE_AUDIT)
@Secured({Role.ADMIN, "UI_AUDIT"})
public class AuditView extends VerticalLayout {

    private static Logger log = LoggerFactory.getLogger(AuditView.class);
    /**/
    private ODashAuditEventForm form;
    /**/
//    private final SessionObjectUtils utils;
    /**/
    private Grid<ODashAuditEvent> grid;
    private Button downloadButton = new Button("Descargar");
    //    private TextField filter = new TextField("Filtrar por Evento");
    private Button filterButton = new Button(VaadinIcon.FILTER.create());
    //    private TextArea dataTextArea = new TextArea("Data");
    /**/
    HorizontalLayout detailsVerticalLayout = new HorizontalLayout();
    Details details;
    /**/
    private final ODashAuditEventService event_serv;
    /**/
    private TextField fileNameText = new TextField("Nombre de Archivo");
    private TextField separatorText = new TextField("Separador");
    private TextField quoteDelimiterText = new TextField("Delimitador de texto");
    private Checkbox withHeaderCheckBox = new Checkbox("Con encabezado");
    /**/
    FileDownloadWrapper downloadButtonWrapper;
    /**/
    private final List<String> userChildren = new ArrayList<>();

    public AuditView(@Autowired CurrentUser currentUser,
                     @Qualifier("getUserMeAndChildren") ListGenericBean<User> userChildrenList,
                     @Autowired ODashAuditEventService event_serv,
                     @Autowired OUserService user_serv,
                     @Autowired OUserSession ouser_session,
                     @Autowired UserRepository ouser_repo,
                     @Autowired MyAuditEventComponent auditEvent,
                     @Autowired Grid<ODashAuditEvent> grid) {
//        utils = new SessionObjectUtils(ouser_session);
        this.grid = grid;
        /**/
        this.event_serv = event_serv;
//        List<User> user = utils.getUserFamily(currentUser);
        List<User> user = userChildrenList.getList();
        userChildren.addAll(user.stream().map(User::getEmail).collect(Collectors.toList()));
//        user.forEach(cnsmr -> {
//            userChildren.add(cnsmr.getEmail());
//        });
        Label titleSpan = new Label("Auditoría de Eventos");
        Span s = new Span(titleSpan, new Hr());
        s.setWidthFull();
        /**/
        form = new ODashAuditEventForm(user);
        initForm();
        /**/
        log.info("User childrens {}", user);
        form.setWidth("100%");
        add(s, form);
        setWidth("100%");
        /**/
        grid.setSelectionMode(Grid.SelectionMode.MULTI);
//        grid.setHeight("400px");
//        grid.addColumn(ODashAuditEvent::getPrincipal)
//                .setHeader("Usuario");
//
//        grid.addColumn(ODashAuditEvent::getEventDate)
//                .setHeader("Fecha");
//
//        grid.addColumn(TemplateRenderer.<ODashAuditEvent>of(
//                        "<div><small><b>[[item.name]]</b></small><br>[[item.purchasedate]]</div>")
//                .withProperty("name", col -> {
//                    return col.getEventType().name();
//                })
//                .withProperty("purchasedate", ODashAuditEvent::getEventDesc));
//        grid.getColumns().stream().forEach(col -> col.setAutoWidth(true));

        /**/
        createDetailsVertical();
        details = new Details("Detalles", detailsVerticalLayout);
        details.addThemeVariants(DetailsVariant.FILLED, DetailsVariant.SMALL);
        downloadButtonWrapper = new FileDownloadWrapper(new StreamResource(
                "stt_eventos.csv",
                () -> new ByteArrayInputStream(export(grid).getBytes())
        ));
        downloadButton.addClickListener(listener -> {
            StringBuilder sb = new StringBuilder();
            String userName = "Todos los usuarios";
            if (form.getBinderBean().getUserCombo() != null) {
                userName = form.getBinderBean().getUserCombo().getEmail();
            }
            sb.append(userName)
                    .append(".")
                    .append(" Desde: ")
                    .append(ODateUitls.dd_MM_yyyy.format(ODateUitls.valueOf(form.getBinderBean().getFirstDate())))
                    .append(" Hasta: ")
                    .append(ODateUitls.dd_MM_yyyy.format(ODateUitls.valueOf(form.getBinderBean().getSecondDate())));
            auditEvent.add(ODashAuditEvent.OEVENT_TYPE.DOWNLOAD_FILE_AUDITEVENT, sb.toString());
        });
        downloadButtonWrapper.wrapComponent(downloadButton);
        downloadButtonWrapper.getElement().getStyle().set("margin-left", "auto");
//        Span s = new Span(filter);
//        s.setWidthFull();
//        h.setWidthFull();
//        h.getStyle().set("margin-left", "auto");
        VerticalLayout h = new VerticalLayout(downloadButtonWrapper, grid, details);
        h.setSizeFull();
        /**/
        h.setSpacing(false);
        h.setPadding(false);
        h.setMargin(false);
        /**/
//        h.setDefaultVerticalComponentAlignment(Alignment.BASELINE);
        add(h);
//        filter.setClearButtonVisible(true);
//        filter.setAutoselect(true);
//        filter.setValueChangeMode(ValueChangeMode.LAZY);
//        filter.addValueChangeListener(listener -> {
//            ListDataProvider<ODashAuditEvent> l = (ListDataProvider<ODashAuditEvent>) grid.getDataProvider();
//            applyFilter(l);
//        });
//        filterButton.addThemeVariants(ButtonVariant.LUMO_CONTRAST);
//        filterButton.addClickListener(listener -> {
//            if (filter.getValue() != null && !filter.getValue().isEmpty()) {
//                filterButton.removeThemeVariants(ButtonVariant.LUMO_CONTRAST);
//                filterButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
//            } else {
//                filterButton.removeThemeVariants(ButtonVariant.LUMO_PRIMARY);
//                filterButton.addThemeVariants(ButtonVariant.LUMO_CONTRAST);
//            }
//        });
    }

    private void initForm() {
        downloadButton.setEnabled(false);
        form.addListener(ODashAuditEventForm.SearchEvent.class, this::searchEventListener);
    }

    private void applyFilter(ListDataProvider<ODashAuditEvent> dataProvider) {
//        dataProvider.clearFilters();
//        if (filter.getValue() != null) {
//            dataProvider.addFilter(person -> person.getEventType().name().contains(filter.getValue().toUpperCase()));
//        }
    }

    private void searchEventListener(ODashAuditEventForm.ODashAuditEventFormEvent event) {
        try {
            List<ODashAuditEvent> items = new ArrayList<>();
            /* Colocar nombre del archivo. */
            fileNameText.setValue(LocalDate.now().toString());
            Date one = ODateUitls.parseToYearMonthDay(event.getUser().getFirstDate());
            Date two = ODateUitls.parseToYearMonthDay(event.getUser().getSecondDate().plusDays(1));
            /* Buscar todos los usuarios. */
            if (event.getUser().getUserCombo() == null) {
                /* Todos los eventos */
                if (event.getUser().getEventCombo() == null
                        || "".equals(event.getUser().getEventCombo())) {
                    items = event_serv.findAll(one, two);
                    setItemsGrid(items);
                } else {
                    items = event_serv.findAll(userChildren,
                            event.getUser().getEventCombo(),
                            one, two);
                    setItemsGrid(items);
                }
            } else {
                /* Todos los eventos */
                if (event.getUser().getEventCombo() == null
                        || "".equals(event.getUser().getEventCombo())) {
                    items = event_serv.findAll(event.getUser().getUserCombo().getEmail(),
                            one, two);
                    setItemsGrid(items);
                } else {
                    items = event_serv.findAll(event.getUser().getUserCombo().getEmail(),
                            event.getUser().getEventCombo(),
                            one, two);
                    setItemsGrid(items);
                }
            }
            /**/
            if (items.isEmpty()) {
                showNotification("No hay información a mostrar.");
            }
        } catch (Exception e) {
            log.warn("", e);
            showNotification("Ha ocurrido un error - E205. Intente de nuevo. Si el erorr persiste comuníquese con STT");
        }
    }

    public void setItemsGrid(List<ODashAuditEvent> items) {
        grid.setItems(items);
        downloadButton.setEnabled(!items.isEmpty());
    }

    private void showNotification(String text) {
        Notification notification = new Notification();
        Span label = new Span(text);
        Button closeButton = new Button("Cerrar", e -> notification.close());
        notification.open();
        notification.setPosition(Notification.Position.MIDDLE);
        notification.add(label, closeButton);
    }

    private void createDetailsVertical() {
        separatorText.setValue(",");
        quoteDelimiterText.setValue("\"");
        withHeaderCheckBox.setValue(true);
        withHeaderCheckBox.setEnabled(false);
        fileNameText.setEnabled(false);
        detailsVerticalLayout.add(withHeaderCheckBox, separatorText, quoteDelimiterText, fileNameText);
    }

    private String export(Grid<ODashAuditEvent> grid) {
        log.info("OCURRIO EL EXPORT DEL ARCHIVO.......");
        // Fetch all data from the grid in the current sorted order
        Stream<ODashAuditEvent> persons = null;
        Set<ODashAuditEvent> selection = grid.asMultiSelect().getValue();
        if (selection != null && !selection.isEmpty()) {
            persons = selection.stream();
        } else {
//            persons = dataView.getItems();
            // Alternative approach without DataView
            persons = ((DataProvider<ODashAuditEvent, String>) grid.getDataProvider()).fetch(createQuery(grid));
        }
        /**/
//        persons.forEach(cnsmr->{
//
//        });
        StringWriter output = new StringWriter();
        StatefulBeanToCsv<ODashAuditEvent> writer = new StatefulBeanToCsvBuilder<ODashAuditEvent>(output)
                .withSeparator(separatorText.getValue().toCharArray()[0])
                .build();
        try {
            writer.write(persons);
        } catch (CsvDataTypeMismatchException | CsvRequiredFieldEmptyException e) {
            output.write("An error occured during writing: " + e.getMessage());
        }

//        result.setValue(output.toString());
        return output.toString();
    }

    private Query<ODashAuditEvent, String> createQuery(Grid<ODashAuditEvent> grid) {
        List<GridSortOrder<ODashAuditEvent>> gridSort = grid.getSortOrder();
        List<QuerySortOrder> sortOrder = gridSort
                .stream()
                .map(order -> order.getSorted().getSortOrder(order.getDirection()))
                .flatMap(orders -> orders)
                .collect(Collectors.toList());

        BinaryOperator<SerializableComparator<ODashAuditEvent>> operator = (comparator1, comparator2) -> {
            return comparator1.thenComparing(comparator2)::compare;
        };
        SerializableComparator<ODashAuditEvent> inMemorySorter = gridSort
                .stream()
                .map(order -> order.getSorted().getComparator(order.getDirection()))
                .reduce(operator)
                .orElse(null);

        return new Query<ODashAuditEvent, String>(0, Integer.MAX_VALUE, sortOrder, inMemorySorter, null);
    }
}
