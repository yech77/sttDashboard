package com.stt.dash.ui;

import com.stt.dash.app.OMonths;
import com.stt.dash.app.session.ListGenericBean;
import com.stt.dash.backend.data.AbstractSmsByYearMonth;
import com.stt.dash.backend.data.SmsByYearMonth;
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
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.server.StreamResource;
import org.vaadin.olli.FileDownloadWrapper;

import java.io.ByteArrayInputStream;
import java.util.Collection;
import java.util.List;


@Tag("sms-show-grid-view")
@JsModule("./src/views/smsgridview/sms-show-grid-view.ts")
//@Route(value = BakeryConst.PAGE_SMS_SHOW_GRID_VIEW, layout = MainView.class)
@PageTitle(BakeryConst.TITLE_SMS_SHOW_VIEW)
public class MonthlySmsShowGridView extends LitTemplate implements Viewnable<SmsByYearMonth> {

    @Id("row-header")
    Div rowHeader;

    @Id("row-body")
    Div rowBody;

    @Id("row-footer")
    Div rowFooter;

    @Id("smsGrid")
    Grid<SmsByYearMonth> grid;
    /**/
    private final SmsShowGridPresenter<SmsByYearMonth> presenter;
    /**/
    private Grid.Column<SmsByYearMonth> groupByColum;
    private Grid.Column<SmsByYearMonth> someCodeColum;
    private Grid.Column<SmsByYearMonth> totalColumn;
    private Grid.Column<SmsByYearMonth> messageTypeColum;
    private Grid.Column<SmsByYearMonth> dateColumn;

    public MonthlySmsShowGridView(SmsHourService smsHourService, List<Integer> monthToShowList, List<String> systemidList) {
        presenter = new MonthlySmsShowGridPresenter(smsHourService, monthToShowList, systemidList, this);
        setRowHeader("Últimos tres meses");
        createColumns();
        grid.setHeight("75%");
    }

    private void createColumns() {
        createGroupByColumn();
        createSomeCodeColumn();
        createTotalColumn();
        grid.addThemeVariants(GridVariant.LUMO_ROW_STRIPES);
    }

    public void setRowHeader(String title) {
        rowHeader.add(new H3(title));
    }

    @Override
    public void setGridDataProvider(ListDataProvider<SmsByYearMonth> dataProvider) {
        grid.setDataProvider(dataProvider);
    }

    @Override
    public void updateDownloadButton(Collection<SmsByYearMonth> messages) {
        rowBody.add(getDownloadButton(messages));
    }

    private Component getDownloadButton(Collection<SmsByYearMonth> messages) {
        String fileName = "trimestre-Mensajes.csv";
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

    public String getStringData(Collection<SmsByYearMonth> messages) {
        if (messages.size() > 5000000) {
            System.out.println("Daily message limit reached. Code not able to handle this size of string.");
            return "";
        }
        StringBuilder sb = new StringBuilder("mes,\"tipo de mensjae\",total\n");
        for (AbstractSmsByYearMonth msg : messages) {
            sb.append(OMonths.valueOf(msg.getGroupBy()).name()).append(",");
            sb.append(msg.getSomeCode()).append(",");
            sb.append(msg.getTotal());
            sb.append("\n");
        }
        return sb.toString();
    }

    private void createGroupByColumn() {
        groupByColum = grid.addColumn(o -> {
                    return OMonths.valueOf(o.getGroupBy()).getMonthName();
                })
                .setComparator(com -> com.getGroupBy())
                .setHeader("Mes")
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