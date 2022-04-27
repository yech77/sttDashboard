package com.stt.dash.ui;

import com.stt.dash.app.session.ListGenericBean;
import com.stt.dash.backend.data.AbstractSmsByYearMonth;
import com.stt.dash.backend.data.SmsByYearMonthDay;
import com.stt.dash.backend.service.SmsHourService;
import com.stt.dash.ui.utils.BakeryConst;
import com.stt.dash.ui.utils.FormattingUtils;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.Tag;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dependency.JsModule;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.grid.ColumnTextAlign;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.html.H5;
import com.vaadin.flow.component.littemplate.LitTemplate;
import com.vaadin.flow.component.template.Id;
import com.vaadin.flow.data.provider.ListDataProvider;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.server.StreamResource;
import org.apache.commons.lang3.StringUtils;
import org.vaadin.olli.FileDownloadWrapper;

import java.io.ByteArrayInputStream;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.function.Consumer;


@Tag("sms-show-grid-view-v2")
@JsModule("./src/views/smsgridview/sms-show-grid-view-v2.ts")
@PageTitle(BakeryConst.TITLE_SMS_SHOW_VIEW)
public class SmsShowGridViewV2 extends LitTemplate implements Viewnable<SmsByYearMonthDay> {

    @Id("grid-title")
    H3 title;

    @Id("grid-subtitle")
    H5 subtitle;

    @Id("grid-btn-download")
    Button downloadButton;

    @Id("grid-btn-close")
    Button closeButton;

    @Id("smsGrid")
    Grid<SmsByYearMonthDay> grid;

    private Consumer<String> consumer;
    private final SmsShowGridPresenter presenter;
    /**/
    private Grid.Column<SmsByYearMonthDay> groupByColum;
    private Grid.Column<SmsByYearMonthDay> someCodeColum;
    private Grid.Column<SmsByYearMonthDay> totalColumn;
    private Grid.Column<SmsByYearMonthDay> messageTypeColum;
    private Grid.Column<SmsByYearMonthDay> dateColumn;
//    private String stringDate;

    public SmsShowGridViewV2(SmsHourService smsHourService, int actualYear, int actualMonth, ListGenericBean<String> stringListGenericBean) {
        this(smsHourService, actualYear, actualMonth, stringListGenericBean.getList());
    }

    public SmsShowGridViewV2(SmsHourService smsHourService, int actualYear, int actualMonth, List<String> systemidStringList) {
        presenter = new DailySmsShowGridPresenter(smsHourService, Arrays.asList(actualYear, actualMonth), systemidStringList, this);
        createColumns();
        grid.setHeight("75%");
    }

    public SmsShowGridViewV2(SmsHourService smsHourService, int actualYear, int actualMonth, int actualDay, List<String> systemidStringList, List<String> messageType) {
        presenter = new DailySmsShowGridPresenter(smsHourService, Arrays.asList(actualYear, actualMonth, actualDay), systemidStringList, messageType, this);
        createColumns();
        grid.setHeight("75%");
    }

    private void createColumns() {
        title.setText("Titulo");
        subtitle.setText("Sub Titulo");
        createGroupByColumn();
        createSomeCodeColumn();
        createTotalColumn();
        grid.addThemeVariants(GridVariant.LUMO_ROW_STRIPES);
    }

    @Override
    public void setGridDataProvider(ListDataProvider<SmsByYearMonthDay> dataProvider) {
        grid.setDataProvider(dataProvider);
    }

    @Override
    public void updateDownloadButton(Collection<SmsByYearMonthDay> messages) {
    }

    private Component getDownloadButton(Collection<SmsByYearMonthDay> messages) {
        String fileName = "mes-actual-Mensajes.csv";
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

    public String getStringData(Collection<SmsByYearMonthDay> messages) {
        if (messages.size() > 5000000) {
            System.out.println("Daily message limit reached. Code not able to handle this size of string.");
            return "";
        }
        StringBuilder sb = new StringBuilder("dia,\"tipo de mensaje\",total\n");
        for (SmsByYearMonthDay msg : messages) {
            sb.append(msg.getDaySms()).append("/").append(msg.getMonthSms()).append("/").append(msg.getYearSms());
            sb.append(msg.getSomeCode()).append(",");
            sb.append(msg.getTotal());
            sb.append("\n");
        }
        return sb.toString();
    }

    private void createGroupByColumn() {
        groupByColum = grid.addColumn(o -> {
                    return o.getDaySms() + "/" + o.getMonthSms() + "/" + o.getYearSms();
                })
                .setComparator(com -> com.getGroupBy())
                .setHeader("Dia")
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

    public void setConsumer(Consumer<String> c) {
        consumer = c;
        closeButton.setEnabled(true);
        closeButton.addClickListener(buttonClickEvent -> consumer.accept("Cerrando"));
    }

    public void setTitles(String mainTitle, String subT) {
        title.setText(StringUtils.isEmpty(mainTitle) ? "" : mainTitle);
        subtitle.setText(StringUtils.isEmpty(subT) ? "" : subT);
    }
}