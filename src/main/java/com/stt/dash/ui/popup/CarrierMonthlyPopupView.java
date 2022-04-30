package com.stt.dash.ui.popup;

import com.stt.dash.app.OMessageType;
import com.stt.dash.backend.data.AbstractSmsByYearMonth;
import com.stt.dash.backend.data.SmsByYearMonthDay;
import com.stt.dash.backend.service.SmsHourService;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.server.StreamResource;
import org.vaadin.olli.FileDownloadWrapper;

import java.io.ByteArrayInputStream;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;

public class CarrierMonthlyPopupView extends MonthlySmsPopupView {

    public CarrierMonthlyPopupView(SmsHourService smsHourService, int yearSms, int monthSms, List<String> systemidStringList, Set<OMessageType> messageType) {
        presenter = new MonthlySmsPopupPresenter(smsHourService, yearSms, monthSms, systemidStringList, messageType, this);
        createColumns(grid);
        grid.setHeight("75%");
        setTitles("Gráfico - Mensajes este mes", "Cliente");
        downloadButton.addClickListener(buttonClickEvent -> {
            if (buttonClickEvent.isFromClient()) {
                downloadButton.setEnabled(false);
                updateDownloadButton(presenter.getdataFromProvider());
                consumer.accept("Cerrado por descarga");
            }
        });
    }

    public CarrierMonthlyPopupView(SmsHourService smsHourService, int yearSms, int monthSms, List<String> carrierStringList, List<String> messageTypeStringList, List<String> systemidStringList) {
        presenter = new MonthlySmsPopupPresenter(smsHourService, yearSms, monthSms, carrierStringList, messageTypeStringList, systemidStringList, this);
        createColumns(grid);
        grid.setHeight("75%");
        setTitles("Gráfico - Mensajes este mes", "Cliente");
        downloadButton.addClickListener(buttonClickEvent -> {
            if (buttonClickEvent.isFromClient()) {
                downloadButton.setEnabled(false);
                updateDownloadButton(presenter.getdataFromProvider());
                consumer.accept("Cerrado por descarga");
            }
        });
    }

    public CarrierMonthlyPopupView(SmsHourService smsHourService, int yearSms, int monthSms, int daySms, List<String> carrierStringList, List<String> messageTypeStringList, List<String> systemidStringList) {
        presenter = new MonthlySmsPopupPresenter(smsHourService, yearSms, monthSms, daySms, carrierStringList, messageTypeStringList, systemidStringList, this);
        createColumns(grid);
        grid.setHeight("75%");
        setTitles("Gráfico - Mensajes este mes", "Cliente");
        downloadButton.addClickListener(buttonClickEvent -> {
            if (buttonClickEvent.isFromClient()) {
                downloadButton.setEnabled(false);
                updateDownloadButton(presenter.getdataFromProvider());
                consumer.accept("Cerrado por descarga");
            }
        });
    }

    public String getStringData(Collection<SmsByYearMonthDay> messages) {
        if (messages.size() > 10000) {
            System.out.println("Daily message limit reached. Code not able to handle this size of string.");
            return "";
        }
        StringBuilder sb = new StringBuilder("dia,\"credencial\",\"tipo de mensaje\",total\n");
        for (AbstractSmsByYearMonth msg : messages) {
            sb.append(msg.getGroupBy()).append(",");
            sb.append(msg.getSomeCode()).append(",");
            sb.append(msg.getMessageType()).append(",");
            sb.append(msg.getTotal());
            sb.append("\n");
        }
        return sb.toString();
    }

    private Component getDownloadButton(Collection<SmsByYearMonthDay> messages, Consumer<String> consumer) {
        String fileName = "cliente-Mensajes-dashboard.csv";
        Button download = new Button("Descargar", VaadinIcon.ARROW_DOWN.create());
        download.setIconAfterText(true);

        FileDownloadWrapper buttonWrapper = new FileDownloadWrapper(new StreamResource(fileName, () -> {
            return new ByteArrayInputStream(getStringData(messages).getBytes());
        }));
        buttonWrapper.wrapComponent(download);
        download.addClickListener(click -> consumer.accept(""));
        return buttonWrapper;
    }

    @Override
    public void updateDownloadButton(Collection<SmsByYearMonthDay> messages) {
        Dialog d = new Dialog();
        d.add(getDownloadButton(messages, s -> d.close()));
        d.open();
    }

    @Override
    public void createGroupByColumn(Grid<SmsByYearMonthDay> grid) {
        groupByColum = grid.addColumn(o -> {
            return o.getGroupBy();
        }).setComparator(com -> com.getGroupBy()).setHeader("Dia").setAutoWidth(true);
    }

    @Override
    public void createSomeCodeColumn(Grid<SmsByYearMonthDay> grid) {
        someCodeColum = grid.addColumn(AbstractSmsByYearMonth::getSomeCode).setComparator(com -> com.getSomeCode()).setHeader("Credencial").setAutoWidth(true);
    }

    @Override
    public void createColumns(Grid<SmsByYearMonthDay> grid) {
        createGroupByColumn(grid);
        createSomeCodeColumn(grid);
        createMessageType();
        createTotalColumn();
        grid.addThemeVariants(GridVariant.LUMO_ROW_STRIPES);
    }
}
