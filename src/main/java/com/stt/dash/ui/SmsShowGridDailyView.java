package com.stt.dash.ui;

import com.stt.dash.app.OMonths;
import com.stt.dash.app.session.ListGenericBean;
import com.stt.dash.backend.data.AbstractSmsByYearMonth;
import com.stt.dash.backend.service.SmsHourService;
import com.stt.dash.ui.utils.BakeryConst;
import com.stt.dash.ui.utils.FormattingUtils;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.Tag;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dependency.JsModule;
import com.vaadin.flow.component.grid.ColumnTextAlign;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.littemplate.LitTemplate;
import com.vaadin.flow.component.template.Id;
import com.vaadin.flow.data.provider.ListDataProvider;
import com.vaadin.flow.data.renderer.LocalDateTimeRenderer;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.server.StreamResource;
import org.vaadin.olli.FileDownloadWrapper;

import java.io.ByteArrayInputStream;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collection;


@Tag("sms-show-grid-view")
@JsModule("./src/views/smsgridview/sms-show-grid-view.ts")
//@Route(value = BakeryConst.PAGE_SMS_SHOW_GRID_VIEW, layout = MainView.class)
@PageTitle(BakeryConst.TITLE_SMS_SHOW_VIEW)
public class SmsShowGridDailyView extends LitTemplate implements Viewnable<AbstractSmsByYearMonth> {

    @Id("row-header")
    Div rowHeader;

    @Id("row-body")
    Div rowBody;

    @Id("row-footer")
    Div rowFooter;

    @Id("smsGrid")
    Grid<AbstractSmsByYearMonth> grid;
    /**/
    private final SmsShowGridDailyPresenter presenter;
    /**/
    private Grid.Column<AbstractSmsByYearMonth> groupByColum;
    private Grid.Column<AbstractSmsByYearMonth> someCodeColum;
    private Grid.Column<AbstractSmsByYearMonth> totalColumn;
    private Grid.Column<AbstractSmsByYearMonth> messageTypeColum;
    private Grid.Column<AbstractSmsByYearMonth> dateColumn;
    private String stringDate;

    public SmsShowGridDailyView(SmsHourService smsHourService, int actualYear, int actualMonth, int actualDay, ListGenericBean<String> stringListGenericBean) {
        presenter = new SmsShowGridDailyPresenter(smsHourService, actualYear, actualMonth, actualDay, stringListGenericBean, this);
        stringDate = actualDay + "/" + actualMonth + "/" + actualYear;
        rowHeader.add(new H3("Dia de hoy"));
        createColumns();
        grid.setHeight("75%");
    }

    private void createColumns() {
        createTodayColumn();
        createGroupByColumn();
        createSomeCodeColumn();
        createTotalColumn();
        grid.addThemeVariants(GridVariant.LUMO_ROW_STRIPES);
    }

    @Override
    public void setGridDataProvider(ListDataProvider<AbstractSmsByYearMonth> dataProvider) {
        grid.setDataProvider(dataProvider);
    }

    @Override
    public void updateDownloadButton(Collection<AbstractSmsByYearMonth> messages) {
        rowBody.add(getDownloadButton(messages));
    }

    private Component getDownloadButton(Collection<AbstractSmsByYearMonth> messages) {
        String fileName = "dia-Mensajes.csv";
        Button download = new Button("Descargar");

        FileDownloadWrapper buttonWrapper = new FileDownloadWrapper(
                new StreamResource(fileName, () -> {
                    return new ByteArrayInputStream(getStringData(messages).getBytes());
                }
                )
        );
        download.addClickListener(click -> {
        });
        buttonWrapper.wrapComponent(download);
        return buttonWrapper;
    }

    public String getStringData(Collection<AbstractSmsByYearMonth> messages) {
        if (messages.size() > 5000000) {
            System.out.println("Daily message limit reached. Code not able to handle this size of string.");
            return "";
        }
        StringBuilder sb = new StringBuilder("dia,\"hora\",\"tipo de mensaje\",total\n");
        for (AbstractSmsByYearMonth msg : messages) {
            sb.append(stringDate).append(",");
            sb.append(msg.getGroupBy()).append(",");
            sb.append(msg.getSomeCode()).append(",");
            sb.append(msg.getTotal());
            sb.append("\n");
        }
        return sb.toString();
    }

    private void createTodayColumn() {
        dateColumn = grid.addColumn(c -> {
                    return stringDate;
                })
                .setHeader("Dia")
                .setAutoWidth(true);
    }

    private void createGroupByColumn() {
        groupByColum = grid.addColumn(o -> {
                    return o.getGroupBy() + ":00";
                })
                .setComparator(com -> com.getGroupBy())
                .setHeader("Hora")
                .setAutoWidth(true);
    }

    private void createSomeCodeColumn() {
        someCodeColum = grid.addColumn(AbstractSmsByYearMonth::getSomeCode)
                .setComparator(com -> com.getSomeCode())
                .setHeader("Tipo de Mensaje")
                .setAutoWidth(true);
    }

    private void createTotalColumn() {
        someCodeColum = grid.addColumn(
                        total -> {
                            return FormattingUtils.getUiSmsFormatter().format(total.getTotal());
                        })
                .setHeader("TOTAL")
                .setTextAlign(ColumnTextAlign.END)
                .setAutoWidth(true);
    }
}
//    private static Component createSubscriberHeader() {
//        Span span = new Span("Subscriber");
//        Icon icon = VaadinIcon.INFO_CIRCLE.create();
//        icon.getElement()
//                .setAttribute("title", "Subscribers are paying customers");
//        icon.getStyle().set("height", "var(--lumo-font-size-m)")
//                .set("color", "var(--lumo-contrast-70pct)");
//
//        HorizontalLayout layout = new HorizontalLayout(span, icon);
//        layout.setAlignItems(FlexComponent.Alignment.CENTER);
//        layout.setSpacing(false);
//
//        return layout;
//    }
//private static String createSubscriberFooterText(List<Person> people) {
//    long subscriberCount = people.stream().filter(Person::isSubscriber)
//            .count();
//
//    return String.format("%s subscribers", subscriberCount);
//}
//
//    private static Component createMembershipHeader() {
//        Span span = new Span("Membership");
//        Icon icon = VaadinIcon.INFO_CIRCLE.create();
//        icon.getElement().setAttribute("title",
//                "Membership levels determines which features a client has access to");
//        icon.getStyle().set("height", "var(--lumo-font-size-m)")
//                .set("color", "var(--lumo-contrast-70pct)");
//
//        HorizontalLayout layout = new HorizontalLayout(span, icon);
//        layout.setAlignItems(FlexComponent.Alignment.CENTER);
//        layout.setSpacing(false);
//
//        return layout;
//    }
//
//    private static String createMembershipFooterText(List<Person> people) {
//        long regularCount = people.stream()
//                .filter(person -> "Regular".equals(person.getMembership()))
//                .count();
//        long premiumCount = people.stream()
//                .filter(person -> "Premium".equals(person.getMembership()))
//                .count();
//        long vipCount = people.stream()
//                .filter(person -> "VIP".equals(person.getMembership())).count();
//
//        return String.format("%s regular, %s premium, %s VIP", regularCount,
//                premiumCount, vipCount);
//    }