package com.stt.dash.ui.popup;

import com.stt.dash.app.OMonths;
import com.stt.dash.backend.data.AbstractSmsByYearMonth;
import com.stt.dash.backend.data.SmsByYearMonth;
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
import java.util.function.Consumer;

public class MainDashBoardMonthlyPopUpView extends MonthlySmsPopupView {

    /**
     * Dashboard: Chart
     *
     * @param smsHourService
     * @param yearSms
     * @param monthToShow
     * @param systemidStringList
     */
    public MainDashBoardMonthlyPopUpView(SmsHourService smsHourService, int yearSms, int monthToShow, List<String> systemidStringList) {
        presenter = new MonthlySmsPopupPresenter(smsHourService, yearSms, monthToShow, systemidStringList, this);
        createColumns(grid);
        grid.setHeight("75%");
        setTitles("Gráfico - Mensajes este mes", "Dashboard");
        downloadButton.addClickListener(buttonClickEvent -> {
            if (buttonClickEvent.isFromClient()) {
                downloadButton.setEnabled(false);
                updateDownloadButton(presenter.getdataFromProvider());
                consumer.accept("Cerrado por descarga");
            }
        });
    }

    /**
     * Dashboard: Point.
     *
     * @param smsHourService
     * @param yearSms
     * @param monthSms
     * @param selectedDay
     * @param systemidStringList
     */
    public MainDashBoardMonthlyPopUpView(SmsHourService smsHourService, int yearSms, int monthSms, int selectedDay, List<String> systemidStringList) {
        presenter = new MonthlySmsPopupPresenter(smsHourService, yearSms, monthSms, selectedDay, systemidStringList, this);
        createColumns(grid);
        grid.setHeight("75%");
        setTitles("Gráfico - Mensajes este mes", "Dashboard");
        downloadButton.addClickListener(buttonClickEvent -> {
            if (buttonClickEvent.isFromClient()) {
                downloadButton.setEnabled(false);
                updateDownloadButton(presenter.getdataFromProvider());
                consumer.accept("Cerrado por descarga");
            }
        });
    }

    @Override
    public void updateDownloadButton(Collection<SmsByYearMonthDay> messages) {
        Dialog d = new Dialog();
        d.add(getDownloadButton(messages, s -> d.close()));
        d.open();
    }

    private Component getDownloadButton(Collection<SmsByYearMonthDay> messages, Consumer<String> consumer) {
        String fileName = "mes-Mensajes-dashboard.csv";
        Button download = new Button("Descargar", VaadinIcon.ARROW_DOWN.create());
        download.setIconAfterText(true);

        FileDownloadWrapper buttonWrapper = new FileDownloadWrapper(new StreamResource(fileName, () -> {
            return new ByteArrayInputStream(getStringData(messages).getBytes());
        }));
        buttonWrapper.wrapComponent(download);
        download.addClickListener(click -> consumer.accept(""));
        return buttonWrapper;
    }

    public String getStringData(Collection<SmsByYearMonthDay> messages) {
        if (messages.size() > 10000) {
            System.out.println("Daily message limit reached. Code not able to handle this size of string.");
            return "";
        }
        StringBuilder sb = new StringBuilder("mes,\"tipo de mensaje\",total\n");
        for (AbstractSmsByYearMonth msg : messages) {
            sb.append(msg.getGroupBy()).append(",");
            sb.append(msg.getSomeCode()).append(",");
            sb.append(msg.getTotal());
            sb.append("\n");
        }
        return sb.toString();
    }

    @Override
    public void createGroupByColumn(Grid<SmsByYearMonthDay> grid) {
        groupByColum = grid.addColumn(o -> {
            return o.getGroupBy();
        }).setComparator(com -> com.getGroupBy()).setHeader("Dia").setAutoWidth(true);
    }

    @Override
    public void createSomeCodeColumn(Grid<SmsByYearMonthDay> grid) {
        someCodeColum = grid.addColumn(AbstractSmsByYearMonth::getSomeCode).setComparator(com -> com.getSomeCode()).setHeader("Tipo de Mensaje").setAutoWidth(true);
    }


    @Override
    public void createColumns(Grid<SmsByYearMonthDay> grid) {
        createGroupByColumn(grid);
        createSomeCodeColumn(grid);
        createTotalColumn();
        grid.addThemeVariants(GridVariant.LUMO_ROW_STRIPES);
    }
}
