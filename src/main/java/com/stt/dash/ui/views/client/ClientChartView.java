package com.stt.dash.ui.views.client;

import com.stt.dash.app.OMessageType;
import com.stt.dash.app.OMonths;
import com.stt.dash.app.security.CurrentUser;
import com.stt.dash.app.session.ListGenericBean;
import com.stt.dash.backend.data.AbstractSmsByYearMonth;
import com.stt.dash.backend.data.SmsByYearMonth;
import com.stt.dash.backend.data.SmsByYearMonthDay;
import com.stt.dash.backend.data.SmsByYearMonthDayHour;
import com.stt.dash.backend.data.entity.Client;
import com.stt.dash.backend.data.entity.SystemId;
import com.stt.dash.backend.data.entity.User;
import com.stt.dash.backend.service.SmsHourService;
import com.stt.dash.ui.MainView;
import com.stt.dash.ui.utils.BakeryConst;
import com.vaadin.flow.component.Tag;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.charts.Chart;
import com.vaadin.flow.component.charts.model.*;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.dependency.JsModule;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.polymertemplate.Id;
import com.vaadin.flow.component.polymertemplate.PolymerTemplate;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.templatemodel.TemplateModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.vaadin.gatanaso.MultiselectComboBox;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

@Tag("carrier-chart-view")
@JsModule("./src/views/carrier/carrier-chart-view.js")
@Route(value = BakeryConst.PAGE_CLIENT, layout = MainView.class)
@PageTitle(BakeryConst.TITLE_CLIENT)
public class ClientChartView extends PolymerTemplate<TemplateModel> {

    @Id("divHeader")
    Div divHeader;

    @Id("deliveriesThisMonth")
    private Chart clientTriMixChart;

    @Id("carrierTriLineChart")
    private Chart clientHourlyChart;

    @Id("carrierDailyChart")
    private Chart clientMonthlyChart;

    @Id("carrierTriPieChart")
    private Chart clientTriPieChart;
    /**/
    Logger log = LoggerFactory.getLogger(ClientChartView.class);
    private final SmsHourService smsHourService;
    private final ListGenericBean<String> stringListGenericBean;
    private final CurrentUser currentUser;
    /* OPERADORAS */
    private ComboBox<Client> clientCombobox = new ComboBox<>("Clientes");
    private MultiselectComboBox<SystemId> systemIdMultiCombo = new MultiselectComboBox<>("Credenciales");
    private final MultiselectComboBox<OMessageType> messageTypeMultiCombo = new MultiselectComboBox<>("Tipos de Mensaje");
    /* Para Graficos y servicios */
    private List<Integer> monthToShowList;
    private List<String> systemIdStringList;
    private String[] ml;
    /* Button */
    private Button filterButton = new Button("Actualizar");

    public ClientChartView(SmsHourService smsHourService,
                           @Qualifier("getUserSystemIdString") ListGenericBean<String> stringListGenericBean,
                           CurrentUser currentUser) {
        this.smsHourService = smsHourService;
        this.stringListGenericBean = stringListGenericBean;
        this.currentUser = currentUser;
        /* Nombre de los Meses */
        monthToShowList = monthsIn(2);
        ml = new String[]{OMonths.valueOf(monthToShowList.get(0)).getMonthName(),
                OMonths.valueOf(monthToShowList.get(1)).getMonthName(),
                OMonths.valueOf(monthToShowList.get(2)).getMonthName()};
        /* Listener */
        addValueChangeListener();
        /* Message type */
        messageTypeMultiCombo.setItems(new HashSet<>(Arrays.asList(OMessageType.values())));
        messageTypeMultiCombo.setValue(new HashSet<>(Arrays.asList(OMessageType.values())));
        messageTypeMultiCombo.setItemLabelGenerator(OMessageType::name);
        /* SystemId */
        systemIdMultiCombo.setItemLabelGenerator(SystemId::getSystemId);
        /* Client */
        clientCombobox.setItemLabelGenerator(Client::getClientName);
        /* Client & Systemids*/
        if (currentUser.getUser().getUserType() == User.OUSER_TYPE.HAS) {
            clientCombobox.setItems(currentUser.getUser().getClients());
        } else if (currentUser.getUser().getUserType() == User.OUSER_TYPE.IS) {
            clientCombobox.setItems(currentUser.getUser().getClient());
            /*TODO: Seleccionar por defecto el unico cliente. Validar que no este vacio.*/
            clientCombobox.setReadOnly(true);
            systemIdMultiCombo.setItems(currentUser.getUser().getClient().getSystemids());
        }

        /* HEADER */
        divHeader.add(clientCombobox, systemIdMultiCombo, messageTypeMultiCombo, filterButton);
    }

    private void addValueChangeListener() {
        clientCombobox.addValueChangeListener(clientListener -> {
            systemIdMultiCombo.setValue(null);
            systemIdMultiCombo.setItems(clientListener.getValue().getSystemids());
            systemIdMultiCombo.setValue(new HashSet<>(clientListener.getValue().getSystemids()));
        });
        filterButton.addClickListener(clickEvent -> {
            /* TODO: Validar si tien todos los datos */
            updateCharts();
        });
    }

    private void updateCharts() {
        updateTriMixChart();
        updateMonthlyLineChart();
        updateHourlyChart();
        /**/
        updateTrimestrePie();
    }

    private void updateTrimestrePie() {
        Configuration confHourlyChart = clientTriPieChart.getConfiguration();
        PlotOptionsPie innerPieOptions = new PlotOptionsPie();
//        innerPieOptions.setSize("70%");
        /* Column Chart*/
        List<SmsByYearMonth> l = smsHourService.getGroupSystemIdByYeMoCaWhMoInMessageTypeIn(LocalDate.now().getYear(), monthToShowList, messageTypeMultiCombo.getSelectedItems(), stringListGenericBean.getSet());
        List<DataSeries> LineDateSeriesList = paEntenderPie(l,monthToShowList);
        addToPieChart(confHourlyChart, LineDateSeriesList, innerPieOptions);
        /* Line Chart */
//        for (SystemId s : systemIdMultiCombo.getSelectedItems()) {
//            systemIdStringList.add(s.getSystemId());
//        }
//        l = smsHourService.getGroupSystemIdByYeMoDaHoWhYeMoDayEqMessageTypeIn(LocalDate.now().getYear(), 5, 2, messageTypeMultiCombo.getSelectedItems(), systemIdStringList);
//        PlotOptionsLine plotLine = new PlotOptionsLine();
//        LineDateSeriesList = paEntenderLine(l,
//                Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12,
//                        13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23, 24));
//        addToChart(confHourlyChart, LineDateSeriesList, plotLine);

    }

    private void updateHourlyChart() {
        Configuration confHourlyChart = clientHourlyChart.getConfiguration();
        PlotOptionsColumn plotColum = new PlotOptionsColumn();
        /* Column Chart*/
        List<SmsByYearMonthDayHour> l = smsHourService.getGroupSmsByYearMonthDayHourMessageType(LocalDate.now().getYear(), 5, 2, stringListGenericBean.getSet());
        List<Series> LineDateSeriesList = paEntender(l,
                Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12,
                        13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23, 24));
        addToChart(confHourlyChart, LineDateSeriesList, plotColum);
        /* Line Chart */
        for (SystemId s : systemIdMultiCombo.getSelectedItems()) {
            systemIdStringList.add(s.getSystemId());
        }
        l = smsHourService.getGroupSystemIdByYeMoDaHoWhYeMoDayEqMessageTypeIn(LocalDate.now().getYear(), 5, 2, messageTypeMultiCombo.getSelectedItems(), systemIdStringList);
        PlotOptionsLine plotLine = new PlotOptionsLine();
        LineDateSeriesList = paEntenderLine(l,
                Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12,
                        13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23, 24));
        addToChart(confHourlyChart, LineDateSeriesList, plotLine);
    }

    private void updateMonthlyLineChart() {
        Configuration confMonthlyLineChart = clientMonthlyChart.getConfiguration();
        PlotOptionsAreaspline plotColum = new PlotOptionsAreaspline();
        /* Column Chart*/
        List<SmsByYearMonthDay> l = smsHourService.getGroupSmsByYearMonthDayMessageType(LocalDate.now().getYear(), 5, stringListGenericBean.getSet());
        List<Series> LineDateSeriesList = paEntender(l,
                Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17));
        addToChart(confMonthlyLineChart, LineDateSeriesList, plotColum);
        /* Line Chart */
        l = smsHourService.getGroupSystemIdByYeMoDa(LocalDate.now().getYear(), 5, messageTypeMultiCombo.getSelectedItems(), stringListGenericBean.getSet());
        PlotOptionsAreaspline plotLine = new PlotOptionsAreaspline();
        LineDateSeriesList = paEntenderLine(l,
                Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17));
        addToChart(confMonthlyLineChart, LineDateSeriesList, plotLine);
    }

    private void updateTriMixChart() {
        Configuration confTriMixChart = clientTriMixChart.getConfiguration();

        PlotOptionsColumn plotColum = new PlotOptionsColumn();
        /* Averiguar cuales son los tres meses a calular. */
        XAxis x = new XAxis();
        x.setCategories(ml);
        /**/
        Tooltip tooltip = new Tooltip();
        tooltip.setValueDecimals(0);
        confTriMixChart.setTooltip(tooltip);
        confTriMixChart.addxAxis(x);
        systemIdStringList = new ArrayList<>();
        for (SystemId s :
                systemIdMultiCombo.getSelectedItems()) {
            systemIdStringList.add(s.getSystemId());
        }
        List<SmsByYearMonth> l = smsHourService.getGroupSmsByYearMonthMessageTypeWhMo(LocalDate.now().getYear(), monthToShowList, systemIdStringList);
        List<Series> LineDateSeriesList = paEntender(l, monthToShowList);
        addToChart(confTriMixChart, LineDateSeriesList, plotColum);
        /* LINE CHART */
        l = smsHourService.
                getGroupSystemIdByYeMoWhMoInMessageTypeIn(LocalDate.now().getYear(), monthToShowList, messageTypeMultiCombo.getSelectedItems(), systemIdStringList);

        PlotOptionsLine plotLine = new PlotOptionsLine();
        LineDateSeriesList = paEntenderLine(l, monthToShowList);
        addToChart(confTriMixChart, LineDateSeriesList, plotLine);
    }

    private void addToChart(Configuration configuration, List<Series> LineDateSeriesList, AbstractPlotOptions plot) {
        if (LineDateSeriesList == null || LineDateSeriesList.size() == 0) {
            log.info("{} NO DATA FOR CARRIER CHART LINE");
        } else {
            for (int i = 0; i < LineDateSeriesList.size(); i++) {
                System.out.println("ADDING LINE********" + LineDateSeriesList.get(i).getName());
                Series series = LineDateSeriesList.get(i);
                series.setPlotOptions(plot);
                configuration.addSeries(series);
            }
        }
    }
    private void addToPieChart(Configuration configuration, List<DataSeries> LineDateSeriesList, AbstractPlotOptions plot) {
        if (LineDateSeriesList == null || LineDateSeriesList.size() == 0) {
            log.info("{} NO DATA FOR CARRIER CHART LINE");
        } else {
            for (int i = 0; i < LineDateSeriesList.size(); i++) {
                System.out.println("ADDING LINE********" + LineDateSeriesList.get(i).getName());
                Series series = LineDateSeriesList.get(i);
//                series.setPlotOptions(plot);
                configuration.addSeries(series);
            }
        }
    }

    public List<Series> paEntender(List<? extends AbstractSmsByYearMonth> l, List<Integer> integerList) {
        l.stream().forEach(System.out::println);

        List<Series> dataSeriesList = new ArrayList<>();
        /*TODO nullpointer*/
        /* Recorre los Carrier seleccionados. */
        messageTypeMultiCombo.getSelectedItems().forEach(messageType -> {
            ListSeries series = new ListSeries();
            series.setName(messageType.name());
            /* Recorre los meses del trimestre */
            integerList.forEach(month -> {
                /* Total por Month y Carrier*/
                Long tot = l.stream()
                        .filter(sms -> sms.getGroupBy() == month
                                && messageType.name().equalsIgnoreCase(sms.getSomeCode()))
                        .mapToLong(sms -> sms.getTotal()).sum();
                System.out.println("MONTH-> " + month + ". Message Type: " + messageType + " - TOTAL: " + tot);
                series.addData(tot);
            });
            dataSeriesList.add(series);
        });
//        ListSeries digitelSerie = new ListSeries("DIGITel", 100,200,300);
//        ListSeries movilnetSerie = new ListSeries("MOVILnet", 200,300,400);
//        ListSeries movistarSerie = new ListSeries("MOVistar", 300,400,500);
//        List<ListSeries> dataSeriesList = new ArrayList<>();
//        dataSeriesList.add(digitelSerie);
//        dataSeriesList.add(movilnetSerie);
//        dataSeriesList.add(movistarSerie);
        /* FORMA 2 */
//        List<DataSeries> dataSeriesList = new ArrayList<>();
//        DataSeries series = new DataSeries();
//        series.setName("DIGITEL");
//        series.setData(1427, 11383, 0);
//        dataSeriesList.add(series);
//        series = new DataSeries();
//        series.setName("MOVILNET");
//        series.setData(2710, 23030, 0);
//        dataSeriesList.add(series);
//        series = new DataSeries();
//        series.setName("MOVISTAR");
//        series.setData(2795, 22520, 0);
//        dataSeriesList.add(series);
        return dataSeriesList;
    }

    public List<DataSeries> paEntenderPie(List<? extends AbstractSmsByYearMonth> l, List<Integer> integerList) {
        System.out.println("PA ENTENDER PIE ----------------");
        l.stream().forEach(System.out::println);

        List<DataSeries> dataSeriesList = new ArrayList<>();
        /*TODO nullpointer*/
        /* Recorre los Carrier seleccionados. */
        List<String> carriers = Arrays.asList("DIGITEL", "MOVILNET", "MOVISTAR");
        DataSeries pieSeries = new DataSeries();
        DataSeries donutSeries = new DataSeries();
        carriers.forEach(carrier -> {
                /* Total por Carrier */
                Long tot = l.stream()
                        .filter(sms -> carrier.equalsIgnoreCase(sms.getMessageType()))
                        .mapToLong(sms -> sms.getTotal()).sum();
                System.out.println("OPERADORA-> " + carrier + ". - TOTAL: " + tot);
                pieSeries.add(new DataSeriesItem(carrier, tot));
                /* Total por S*/
            systemIdMultiCombo.getValue().forEach(systemId -> {
                Long totSid = l.stream()
                        .filter(sms -> sms.getMessageType().equalsIgnoreCase(carrier)
                                && systemId.getSystemId().equalsIgnoreCase(sms.getSomeCode()))
                        .mapToLong(sms -> sms.getTotal()).sum();
                donutSeries.add(new DataSeriesItem(systemId.getSystemId(), totSid));
            });
        });
        PlotOptionsPie plotPie = new PlotOptionsPie();
        plotPie.setSize("70%");
        PlotOptionsPie plotDonut = new PlotOptionsPie();
        plotDonut.setInnerSize("75%");
        plotDonut.getDataLabels().setEnabled(false);
        /**/
        pieSeries.setPlotOptions(plotPie);
        donutSeries.setPlotOptions(plotDonut);
        /**/
        dataSeriesList.add(pieSeries);
        dataSeriesList.add(donutSeries);
        return dataSeriesList;
    }

    public List<Series> paEntenderLine(List<? extends AbstractSmsByYearMonth> l,
                                       List<Integer> integerList) {
        l.stream().forEach(System.out::println);

        List<Series> dataSeriesList = new ArrayList<>();
        /*TODO nullpointer*/
        /* Recorre los Carrier seleccionados. */
        systemIdMultiCombo.getSelectedItems().forEach(systemId -> {
            ListSeries series = new ListSeries();
            series.setName(systemId.getSystemId());
            /* Recorre los meses del trimestre */
            integerList.forEach(month -> {
                /* Total por Month y Carrier*/
                Long tot = l.stream()
                        .filter(sms -> sms.getGroupBy() == month
                                && systemId.getSystemId().equalsIgnoreCase(sms.getSomeCode()))
                        .mapToLong(sms -> sms.getTotal()).sum();
                System.out.println("MONTH-> " + month + ". Message Type: " + systemId.getSystemId() + " - TOTAL: " + tot);
                series.addData(tot);
            });
            dataSeriesList.add(series);
        });
        return dataSeriesList;
    }

    /**
     * Devuelve un listado de los meses atras, segun monthback.
     *
     * @param monthback
     * @return
     */
    private List<Integer> monthsIn(int monthback) {
        List<Integer> lm = new ArrayList<>(monthback);
        for (int i = monthback; i > 0; i--) {
            lm.add(LocalDate.now().getMonth().minus(i).getValue());
        }
        lm.add(LocalDate.now().getMonth().getValue());
        return lm;
    }
}
