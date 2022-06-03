package com.stt.dash.ui.popup;

import com.stt.dash.backend.data.AbstractSmsByYearMonth;
import com.stt.dash.backend.data.SmsByYearMonthDay;
import com.stt.dash.backend.data.SmsByYearMonthDayHour;
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
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Collection;
import java.util.List;
import java.util.function.Consumer;

import static com.stt.dash.ui.views.dashboard.DashboardBase.MILITARY_HOURS;

public class MainDashboardDailyPopUpView extends DailySmsPopupView {

    /**
     * Dashboard: Chart
     *
     * @param smsHourService
     * @param yearSms
     * @param monthToShow
     * @param systemidStringList
     */
    public MainDashboardDailyPopUpView(SmsHourService smsHourService, int yearSms, int monthToShow, int dayToShow, List<String> systemidStringList) {
        presenter = new DailySmsPopupPresenter(smsHourService, yearSms, monthToShow, dayToShow, systemidStringList, this);
        createColumns(grid);
        grid.setHeight("75%");
        setTitles("Gráfico - Tráfico del día", "Dashboard");
        downloadButton.addClickListener(buttonClickEvent -> {
            if (buttonClickEvent.isFromClient()) {
                downloadButton.setEnabled(false);
                updateDownloadButton(presenter.getdataFromProvider());
                consumer.accept("Cerrado por descarga");
            }
        });
    }

    public MainDashboardDailyPopUpView(SmsHourService smsHourService, int yearSms, int monthToShow, int dayToShow, int hourToShow, List<String> systemidStringList) {
        presenter = new DailySmsPopupPresenter(smsHourService, yearSms, monthToShow, dayToShow, hourToShow, systemidStringList, this);
        createColumns(grid);
        grid.setHeight("75%");
        setTitles("Gráfico - Tráfico del día", "Dashboard");
        downloadButton.addClickListener(buttonClickEvent -> {
            if (buttonClickEvent.isFromClient()) {
                downloadButton.setEnabled(false);
                updateDownloadButton(presenter.getdataFromProvider());
                consumer.accept("Cerrado por descarga");
            }
        });
    }


    @Override
    public void updateDownloadButton(Collection<SmsByYearMonthDayHour> messages) {
        Dialog d = new Dialog();
        d.add(getDownloadButton(messages, (s) -> d.close()));
        d.open();
    }

    private Component getDownloadButton(Collection<SmsByYearMonthDayHour> messages, Consumer<String> consumer) {
        String fileName = LocalDate.now().format(DateTimeFormatter.ofPattern("ddMMyyyy")) + "-Mensajes-dashboard.csv";
        Button download = new Button("Descargar", VaadinIcon.ARROW_DOWN.create());
        download.setIconAfterText(true);

        FileDownloadWrapper buttonWrapper = new FileDownloadWrapper(new StreamResource(fileName, () -> {
            return new ByteArrayInputStream(getStringData(messages).getBytes());
        }));
        buttonWrapper.wrapComponent(download);
        download.addClickListener(click -> {
            consumer.accept("");
        });
        return buttonWrapper;
    }

    public String getStringData(Collection<SmsByYearMonthDayHour> messages) {
        if (messages.size() > 10000) {
            System.out.println("Daily message limit reached. Code not able to handle this size of string.");
            return "";
        }
        StringBuilder sb = new StringBuilder("hora,\"tipo de mensaje\",total\n");
        for (AbstractSmsByYearMonth msg : messages) {
            sb.append(MILITARY_HOURS[msg.getGroupBy()]).append(",");
            sb.append(msg.getSomeCode()).append(",");
            sb.append(msg.getTotal());
            sb.append("\n");
        }
        return sb.toString();
    }

    @Override
    public void createGroupByColumn(Grid<SmsByYearMonthDayHour> grid) {
        groupByColum = grid.addColumn(o -> {
            return MILITARY_HOURS[o.getGroupBy()];
        }).setComparator(com -> com.getGroupBy()).setHeader("Hora").setAutoWidth(true);
    }

    @Override
    public void createSomeCodeColumn(Grid<SmsByYearMonthDayHour> grid) {
        someCodeColum = grid.addColumn(AbstractSmsByYearMonth::getSomeCode).setComparator(com -> com.getSomeCode()).setHeader("Tipo de Mensaje").setAutoWidth(true);
    }


    @Override
    public void createColumns(Grid<SmsByYearMonthDayHour> grid) {
        createGroupByColumn(grid);
        createSomeCodeColumn(grid);
        createTotalColumn();
        grid.addThemeVariants(GridVariant.LUMO_ROW_STRIPES);
    }
}
