package com.stt.dash.ui;

import com.stt.dash.app.session.ListGenericBean;
import com.stt.dash.backend.data.AbstractSmsByYearMonth;
import com.stt.dash.backend.data.SmsByYearMonthDayHour;
import com.stt.dash.backend.data.entity.sms.AbstractSMS;
import com.stt.dash.backend.service.AbstractSmsService;
import com.stt.dash.backend.service.SmsHourService;
import com.stt.dash.ui.utils.BakeryConst;
import com.stt.dash.ui.utils.FormattingUtils;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.Tag;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dependency.JsModule;
import com.vaadin.flow.component.grid.ColumnTextAlign;
import com.vaadin.flow.component.grid.FooterRow;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.littemplate.LitTemplate;
import com.vaadin.flow.component.template.Id;
import com.vaadin.flow.data.provider.ListDataProvider;
import com.vaadin.flow.data.renderer.LocalDateTimeRenderer;
import com.vaadin.flow.data.renderer.TemplateRenderer;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.server.StreamResource;
import org.vaadin.olli.FileDownloadWrapper;

import java.io.ByteArrayInputStream;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Collection;
import java.util.List;


@Tag("sms-show-grid-view")
@JsModule("./src/views/smsgridview/sms-show-grid-view.js")
//@Route(value = BakeryConst.PAGE_SMS_SHOW_GRID_VIEW, layout = MainView.class)
@PageTitle(BakeryConst.TITLE_SMS_SHOW_VIEW)
public class SmsShowGridAllView extends LitTemplate implements Viewnable<AbstractSMS> {

    @Id("row-header")
    Div rowHeader;

    @Id("row-body")
    Div rowBody;

    @Id("row-footer")
    Div rowFooter;

    @Id("smsGrid")
    Grid<AbstractSMS> grid;
    /**/
    private Grid.Column<AbstractSMS> phoneColum;
    private Grid.Column<AbstractSMS> carrierColum;
    private Grid.Column<AbstractSMS> systemIdColumn;
    private Grid.Column<AbstractSMS> messageTypeColum;
    private Grid.Column<AbstractSMS> dateColumn;
    /**/
    private boolean hasAuthToViewMsgTextColumn = false;
    /**/
    private final SmsShowGridPresenter<AbstractSMS> presenter;
    /**/
    private String stringDate;
    /**/
    FooterRow footerRow;

    public SmsShowGridAllView(AbstractSmsService abstractSmsService, SmsHourService smsHourService, int actualHour, List<String> stringListGenericBean) {
        presenter = new AllSmsShowGridPresenter(abstractSmsService, smsHourService, actualHour, stringListGenericBean, this);
//        stringDate = actualDay + "/" + actualMonth + "/" + actualYear;
        extracted("Hora");
        createGridComponent();
        /**/
        createGrid();
        /**/
        grid.setHeight("75%");
    }

    public void extracted(String title) {
        rowHeader.removeAll();
        rowHeader.add(new H3(title));
    }

    private void createGrid() {
        createGridComponent();
        addColumnsToGrid();
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

    @Override
    public void setGridDataProvider(ListDataProvider<AbstractSMS> dataProvider) {
        grid.setDataProvider(dataProvider);
    }

    @Override
    public void updateDownloadButton(Collection<AbstractSMS> messages) {
        rowBody.add(getDownloadButton(messages));
    }

    private Component getDownloadButton(Collection<AbstractSMS> msg) {
        String fileName = "dia-Mensajes.csv";
        Button download = new Button("Descargar");

        FileDownloadWrapper buttonWrapper = new FileDownloadWrapper(
                new StreamResource(fileName, () -> {
                    return new ByteArrayInputStream(getStringData(msg).getBytes());
                }
                )
        );
        download.addClickListener(click -> {
        });
        buttonWrapper.wrapComponent(download);
        return buttonWrapper;
    }

    private void createTodayColumn() {
        dateColumn = grid.addColumn(c -> {
                    return stringDate;
                })
                .setHeader("Dia")
                .setAutoWidth(true);
    }

    private void createGridComponent() {
        grid.setSelectionMode(Grid.SelectionMode.NONE);
        grid.addThemeVariants(GridVariant.LUMO_NO_BORDER,
                GridVariant.LUMO_COLUMN_BORDERS);
        grid.setHeightFull();
        grid.setWidthFull();
        footerRow = grid.appendFooterRow();
    }

    public String getStringData(Collection<AbstractSMS> messages) {
        if (messages.size() > 5000000) {
            System.out.println("Daily message limit reached. Code not able to handle this size of string.");
            return "";
        }
        StringBuilder sb = new StringBuilder("id,\"address\",datacoding,\"date\",\"iso2\",\"message_type\",\"messages_text\",\"msg_received\",\"msg_sended\",\"origen\",\"systemid\",\"carrierCharCode\"\n");
        for (AbstractSMS msg : messages) {
            sb.append(msg.toString());
            sb.append("\n");
        }
        return sb.toString();
    }

}