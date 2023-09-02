package com.stt.dash.ui.popup;

import com.stt.dash.app.OMessageType;
import com.stt.dash.app.OMonths;
import com.stt.dash.backend.data.AbstractSmsByYearMonth;
import com.stt.dash.backend.data.SmsByYearMonth;
import com.stt.dash.backend.data.entity.Carrier;
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

public class CarrierTrimestralPopUpView extends TrimestralSmsPopupView {

    public CarrierTrimestralPopUpView(SmsHourService smsHourService, int yearSms, int monthSms, List<String> systemidStringList, Set<OMessageType> messageType, Set<Carrier> carrierSet) {
        presenter = new TrimestralSmsPopupPresenter(smsHourService, yearSms, monthSms, systemidStringList, messageType, carrierSet, this);
        createColumns(grid);
        grid.setHeight("75%");
        setTitles("Gráfico - Últimos tres meses", "Operadora");
        downloadButton.addClickListener(buttonClickEvent -> {
            if (buttonClickEvent.isFromClient()) {
                downloadButton.setEnabled(false);
                updateDownloadButton(presenter.getdataFromProvider());
                consumer.accept("Cerrado por descarga");
            }
        });
    }

    public CarrierTrimestralPopUpView(SmsHourService smsHourService, int yearSms, List<Integer> monthSms, List<String> systemidStringList, Set<OMessageType> messageType, Set<Carrier> carrierSet) {
        presenter = new TrimestralSmsPopupPresenter(smsHourService, yearSms, monthSms, systemidStringList, messageType, carrierSet, this);
        createColumns(grid);
        grid.setHeight("75%");
        setTitles("Gráfico - Últimos tres meses", "Operadora");
        downloadButton.addClickListener(buttonClickEvent -> {
            if (buttonClickEvent.isFromClient()) {
                downloadButton.setEnabled(false);
                updateDownloadButton(presenter.getdataFromProvider());
                consumer.accept("Cerrado por descarga");
            }
        });
    }

    @Override
    public void updateDownloadButton(Collection<SmsByYearMonth> messages) {
        Dialog d = new Dialog();
        d.add(getDownloadButton(messages, s -> d.close()));
        d.open();
    }

    private Component getDownloadButton(Collection<SmsByYearMonth> messages, Consumer<String> consumer) {
        String fileName = "trimestre-Mensajes-operadora.csv";
        Button download = new Button("Descargar", VaadinIcon.ARROW_DOWN.create());
        download.setIconAfterText(true);

        FileDownloadWrapper buttonWrapper = new FileDownloadWrapper(new StreamResource(fileName, () -> {
            return new ByteArrayInputStream(getStringData(messages).getBytes());
        }));
        buttonWrapper.wrapComponent(download);
        download.addClickListener(click -> consumer.accept(""));
        return buttonWrapper;
    }

    public String getStringData(Collection<SmsByYearMonth> messages) {
        if (messages.size() > 10000) {
            System.out.println("Daily message limit reached. Code not able to handle this size of string.");
            return "";
        }
        StringBuilder sb = new StringBuilder("mes,\"Operadora\",\"Tipo de mensaje\",total\n");
        for (AbstractSmsByYearMonth msg : messages) {
            sb.append(OMonths.valueOf(msg.getGroupBy()).name()).append(",");
            sb.append(msg.getSomeCode()).append(",");
            sb.append(msg.getMessageType()).append(",");
            sb.append(msg.getTotal());
            sb.append("\n");
        }
        return sb.toString();
    }

    @Override
    public void createGroupByColumn(Grid<SmsByYearMonth> grid) {
        groupByColum = grid.addColumn(o -> {
            return OMonths.valueOf(o.getGroupBy()).getMonthName();
        }).setComparator(com -> com.getGroupBy()).setHeader("Mes").setAutoWidth(true);
    }

    @Override
    public void createSomeCodeColumn(Grid<SmsByYearMonth> grid) {
        someCodeColum = grid.addColumn(AbstractSmsByYearMonth::getSomeCode).setComparator(com -> com.getSomeCode()).setHeader("Operadora").setAutoWidth(true);
    }


    @Override
    public void createColumns(Grid<SmsByYearMonth> grid) {
        createGroupByColumn(grid);
        createSomeCodeColumn(grid);
        createMessageType();
        createTotalColumn();
        grid.addThemeVariants(GridVariant.LUMO_ROW_STRIPES);
    }
}
