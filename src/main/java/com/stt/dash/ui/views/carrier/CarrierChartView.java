package com.stt.dash.ui.views.carrier;

import com.stt.dash.app.OMessageType;
import com.stt.dash.app.OMonths;
import com.stt.dash.app.security.CurrentUser;
import com.stt.dash.app.session.ListGenericBean;
import com.stt.dash.backend.data.AbstractSmsByYearMonth;
import com.stt.dash.backend.data.SmsByYearMonth;
import com.stt.dash.backend.data.SmsByYearMonthDay;
import com.stt.dash.backend.data.SmsByYearMonthDayHour;
import com.stt.dash.backend.data.entity.Carrier;
import com.stt.dash.backend.service.CarrierService;
import com.stt.dash.backend.service.SmsHourService;
import com.stt.dash.ui.MainView;
import com.stt.dash.ui.utils.BakeryConst;
import com.vaadin.flow.component.Tag;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.charts.Chart;
import com.vaadin.flow.component.charts.model.*;
import com.vaadin.flow.component.dependency.JsModule;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.polymertemplate.Id;
import com.vaadin.flow.component.polymertemplate.PolymerTemplate;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.VaadinSession;
import com.vaadin.flow.templatemodel.TemplateModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.Page;
import org.vaadin.gatanaso.MultiselectComboBox;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Tag("carrier-chart-view")
@JsModule("./src/views/carrier/carrier-chart-view.js")
@Route(value = BakeryConst.PAGE_CARRIER, layout = MainView.class)
@PageTitle(BakeryConst.TITLE_CARRIER)
public class CarrierChartView extends PolymerTemplate<TemplateModel> {
    @Id("divHeader")
    Div divHeader;
    @Id("deliveriesThisMonth")
    private Chart carrierTriMixChart;

    @Id("carrierTriLineChart")
    private Chart carrierTriLineChart;

    @Id("carrierTriPieChart")
    private Chart carrierTriPieChart;

    @Id("carrierDailyChart")
    private Chart carrierDailyChart;

    @Id("carrierMonthlyPieChart")
    private Chart carrierMonthlyPieChart;

    @Id("carrierHourlyPieChart")
    private Chart carrierHourlyPieChart;
    /**/
    private static String CARRIER_VIEW_SELECTED_CARRIER = "carrier_view_selected_carrier";
    private static String CARRIER_VIEW_SELECTED_MESSAGETYPE = "carrier_view_selected_messageType";
    /**/
    Logger log = LoggerFactory.getLogger(CarrierChartView.class);
    private final SmsHourService smsHourService;
    private final ListGenericBean<String> userSystemIdList;
    private final CurrentUser currentUser;
    /* OPERADORAS */
    private MultiselectComboBox<Carrier> multi_carrier = new MultiselectComboBox<>("Operadoras");
    private final MultiselectComboBox<OMessageType> multi_messagetype = new MultiselectComboBox<>("Mensajes");
    /* Para Graficos y servicios */
    private List<Integer> monthToShowList;
    private List<Integer> hourList = new ArrayList<>();
    private List<Integer> dayList = new ArrayList<>();
    private String[] ml;
    /*Fechas */
    private int actual_month;
    private int actual_day;
    private int actual_year;
    private int actual_hour;
    /* Button */
    private Button filterButton = new Button("Actualizar");

    public CarrierChartView(SmsHourService smsHourService,
                            CarrierService carrierService,
                            @Qualifier("getUserSystemIdString")
                                    ListGenericBean<String> stringListGenericBean,
                            CurrentUser currentUser) {

        this.currentUser = currentUser;
        this.userSystemIdList = stringListGenericBean;
        this.smsHourService = smsHourService;
        setActualDate();
        /* Nombre de los Meses */
        monthToShowList = monthsIn(2);
        ml = new String[]{OMonths.valueOf(monthToShowList.get(0)).getMonthName(),
                OMonths.valueOf(monthToShowList.get(1)).getMonthName(),
                OMonths.valueOf(monthToShowList.get(2)).getMonthName()};
        /* Message type */
        multi_messagetype.setItems(new HashSet<>(Arrays.asList(OMessageType.values())));
        if (VaadinSession.getCurrent().getAttribute(CARRIER_VIEW_SELECTED_MESSAGETYPE) != null) {
            multi_messagetype.setValue((Set<OMessageType>) VaadinSession.getCurrent().getAttribute(CARRIER_VIEW_SELECTED_MESSAGETYPE));
            VaadinSession.getCurrent().setAttribute(CARRIER_VIEW_SELECTED_MESSAGETYPE, null);
        } else {
            multi_messagetype.setValue(new HashSet<>(Arrays.asList(OMessageType.values())));
        }
        multi_messagetype.setItemLabelGenerator(OMessageType::name);
        /* Carrier */
        Page<Carrier> carrierPage = carrierService.findAll();
        multi_carrier.setItems(carrierPage.getContent());
        multi_carrier.setItemLabelGenerator(Carrier::getCarrierCharcode);
        if (VaadinSession.getCurrent().getAttribute(CARRIER_VIEW_SELECTED_CARRIER) != null) {
            multi_carrier.setValue((Set<Carrier>) VaadinSession.getCurrent().getAttribute(CARRIER_VIEW_SELECTED_CARRIER));
            VaadinSession.getCurrent().setAttribute(CARRIER_VIEW_SELECTED_CARRIER, null);
        } else {
            multi_carrier.setValue(new HashSet<>(carrierPage.getContent()));
        }

        /* HEADER */
        divHeader.add(multi_carrier, multi_messagetype, filterButton);
        /*Actualiza trimestra al entrar en la pantalla. */
        updateTrimestral(userSystemIdList.getList());
        updateDaily(userSystemIdList.getList());
        updateHourlyChart(userSystemIdList.getList());
        filterButton.addClickListener(click -> {
            filterButton.setEnabled(false);
            VaadinSession.getCurrent().setAttribute(CARRIER_VIEW_SELECTED_CARRIER, multi_carrier.getSelectedItems());
            VaadinSession.getCurrent().setAttribute(CARRIER_VIEW_SELECTED_MESSAGETYPE, multi_messagetype.getSelectedItems());
            UI.getCurrent().getPage().reload();
        });
        filterButton.setEnabled(true);
    }

    private void updateHourlyChart(List<String> sids) {
        Configuration confHourlyChart = carrierTriLineChart.getConfiguration();
        confHourlyChart.setTitle("JUNIO - dia de hoy");
        confHourlyChart.setSubTitle("por hora");
        confHourlyChart.getyAxis().setTitle("SMS");
        PlotOptionsColumn plotColum = new PlotOptionsColumn();
        /**/
        confHourlyChart.getxAxis().setTitle("Hora");
//        String[] da = new String[LocalDateTime.now().getHour())];
//        for (int i = 0; i <= LocalDateTime.now().getHour(); i++) {
//            da[i] = i + ":";
//        }
//        confHourlyChart.getxAxis().setCategories(da);
        /**/
        Tooltip tooltip = new Tooltip();
        tooltip.setValueDecimals(0);
        tooltip.setShared(true);
        tooltip.setHeaderFormat("<span style=\"font-size: 10px\">Hora: {point.key}</span><br/>");
        confHourlyChart.setTooltip(tooltip);

        List<String> carrier_list = multi_carrier.getSelectedItems().stream().map(Carrier::getCarrierCharcode).collect(Collectors.toList());
        /* Column Chart*/
        List<SmsByYearMonthDayHour> l = smsHourService.getGroupSmsByYearMonthDayHourMessageType(actual_year, actual_month, actual_day, sids);
        List<Series> LineDateSeriesList = paEntender(l, hourList);
        addToChart(confHourlyChart, LineDateSeriesList, plotColum);
        /* Line Chart */
        l = smsHourService.getGroupCarrierByYeMoDaHoWhYeMoDayEqMessageTypeIn(actual_year,
                actual_month,
                actual_day,
                carrier_list,
                multi_messagetype.getSelectedItems(), sids);
        List<SmsByYearMonthDayHour> l2 = new ArrayList<>(l);
        PlotOptionsLine plotLine = new PlotOptionsLine();
        LineDateSeriesList = paEntenderLine(l, hourList);
        addToChart(confHourlyChart, LineDateSeriesList, plotLine);
        /* DAY PIE*/
        populateHourPieChart(l2);
    }

    private void addToChart(Configuration configuration, List<Series> LineDateSeriesList, AbstractPlotOptions plot) {
        if (LineDateSeriesList == null || LineDateSeriesList.size() == 0) {
            log.info("{} NO DATA FOR CARRIER CHART LINE");
        } else {
            for (int i = 0; i < LineDateSeriesList.size(); i++) {
                System.out.println("ADDING LINE********" + LineDateSeriesList.get(i).getName());
                Series series = LineDateSeriesList.get(i);
                System.out.println("ADDING LINE COPY********" + series.getName());
                series.setPlotOptions(plot);
                configuration.addSeries(series);
            }
        }
    }
    public List<Series> paEntenderLine(List<? extends AbstractSmsByYearMonth> l,
                                       List<Integer> integerList) {
        l.stream().forEach(System.out::println);

        List<Series> dataSeriesList = new ArrayList<>();
        /*TODO nullpointer*/
        /* Recorre los Carrier seleccionados. */
        multi_carrier.getSelectedItems().forEach(carrier -> {
            ListSeries series = new ListSeries();
            series.setName(carrier.getCarrierCharcode());
            /* Recorre los meses del trimestre */
            integerList.forEach(month -> {
                /* Total por Month y Carrier*/
                Long tot = l.stream()
                        .filter(sms -> sms.getGroupBy() == month
                                && carrier.getCarrierCharcode().equalsIgnoreCase(sms.getSomeCode()))
                        .mapToLong(sms -> sms.getTotal()).sum();
                System.out.println("MONTH-> " + month + ". Message Type: " + carrier.getCarrierCharcode() + " - TOTAL: " + tot);
                series.addData(tot);
            });
            dataSeriesList.add(series);
        });
        return dataSeriesList;
    }

    private void updateHourly(List<String> sids) {
        List<String> carrier_list = multi_carrier.getSelectedItems().stream().map(Carrier::getCarrierCharcode).collect(Collectors.toList());
        /* --------------POR HORA */
        List<SmsByYearMonthDayHour> smsHourGroup = smsHourService.getGroupSmsByYearMonthDayHourMessageType(actual_year, actual_month, actual_day, sids);
        List<SmsByYearMonthDayHour> carrierHourGroup = smsHourService.getGroupCarrierByYeMoDaHoWhYeMoDayEqMessageTypeIn(actual_year,
                actual_month,
                actual_day,
                carrier_list,
                multi_messagetype.getSelectedItems(), sids);
        populateHourChart(smsHourGroup, new ArrayList<>(carrierHourGroup));
        /* PIE */
        populateHourPieChart(carrierHourGroup);
    }

    private void updateDaily(List<String> sids) {
        List<String> carrier_list = multi_carrier.getSelectedItems().stream().map(Carrier::getCarrierCharcode).collect(Collectors.toList());
        /* --------------DIARIO */
        List<SmsByYearMonthDay> smsDayGroup = smsHourService.getGroupSmsByYearMonthDayMessageType(actual_year, actual_month, sids);
        List<SmsByYearMonthDay> carrierDayGroup = smsHourService.getGroupCarrierByYeMoMe(actual_year,
                actual_month,
                carrier_list,
                multi_messagetype.getSelectedItems(),
                sids);
        populateMonthChart(smsDayGroup, new ArrayList<>(carrierDayGroup));
        /* PIE */
        populateMonthlyPieChart(carrierDayGroup);
    }

    private void updateTrimestral(List<String> sids) {
        /* ------------- TRIMESTRAL: SMS
         * Ejem: SmsByYearMonth{total=2775, yearSms=2021, monthSms=3, someCode=MO}*/
        List<SmsByYearMonth> smsGroup = smsHourService.getGroupSmsByYearMonthMessageTypeWhMo(actual_year, monthToShowList, sids);
        List<SmsByYearMonth> carrierGroup = smsHourService.getGroupCarrierByYeMoWhMoInMessageTypeIn(actual_year,
                monthToShowList,
                multi_carrier.getSelectedItems(),
                multi_messagetype.getSelectedItems(),
                sids);
        populateTriColumnLineChart(smsGroup, new ArrayList<>(carrierGroup));
        /* PIE */
        populatePieChart(carrierGroup);
    }

    private void populateHourPieChart(List<SmsByYearMonthDayHour> carrierHourGroup) {
        Configuration confOut = carrierHourlyPieChart.getConfiguration();
        /**/
        Tooltip tooltip = new Tooltip();
        tooltip.setValueDecimals(0);
        tooltip.setHeaderFormat("<span style=\"font-size: 10px\">{point.key} {point.percentage:%02.2f}%</span><br/>");
        confOut.setTooltip(tooltip);
        /**/
        PlotOptionsPie innerPieOptions = new PlotOptionsPie();
        innerPieOptions.setSize("70%");
        List<DataSeries> list_series = findDataSeriesPieBase(carrierHourGroup,
                "Total",
                innerPieOptions);
        for (DataSeries list_sery : list_series) {
            confOut.addSeries(list_sery);
        }
        confOut.setTitle("Hoy");
    }

    private void populateHourChart(List<? extends AbstractSmsByYearMonth> smsHourGroup, List<SmsByYearMonthDayHour> carrierHourGroup) {
        Configuration confIn = carrierTriLineChart.getConfiguration();

        confIn.setTitle("JUNIO - dia de hoy");
        confIn.setSubTitle("por hora");
        confIn.getyAxis().setTitle("SMS");
        PlotOptionsColumn plotColum = new PlotOptionsColumn();
        /**/
        confIn.getxAxis().setTitle("Hora");
//        String[] da = new String[LocalDateTime.now().getHour())];
//        for (int i = 0; i <= LocalDateTime.now().getHour(); i++) {
//            da[i] = i + ":";
//        }
//        confHourlyChart.getxAxis().setCategories(da);
        /**/
        Tooltip tooltip = new Tooltip();
        tooltip.setValueDecimals(0);
        tooltip.setShared(true);
        tooltip.setHeaderFormat("<span style=\"font-size: 10px\">Hora: {point.key}</span><br/>");
        confIn.setTooltip(tooltip);
        /**/
        /**/
//        configureColumnChart(confIn);
        smsHourGroup = orderGroup(fillWithCero(smsHourGroup, monthToShowList));
        List<? extends AbstractSmsByYearMonth> l = new ArrayList<>(smsHourGroup);
        /**/
        List<DataSeries> list_series1 = findDataSeriesColumnsBase(smsHourGroup);
        for (DataSeries list_sery : list_series1) {
            confIn.addSeries(list_sery);
        }
//        DataProvider<SmsByYearMonth, ?> dataProvider = new ListDataProvider<>(smsGroup);
//
//        DataProviderSeries<SmsByYearMonth> series = new DataProviderSeries<>(dataProvider, SmsByYearMonth::getTotal);
//        confIn.addSeries(series);

        log.info("TRIMESTRE LINE DATA: {}", l);
        /* Averiguar cuales son los tres meses a calular. */
        List<Integer> monthList = monthToShowList;

        l = orderGroup(fillWithCero(l, monthToShowList));
        List<DataSeries> list_series2 = findDataSeriesLineBase(l);
        if (list_series2 == null || list_series2.size() == 0) {
            log.info("{} NO DATA FOR CARRIER CHART LINE", getStringLog());
        } else {
            for (int i = 0; i < list_series2.size() - 1; i++) {
                confIn.addSeries(list_series2.get(i));
            }
        }
    }

    private void populateMonthlyPieChart(List<SmsByYearMonthDay> carrierDayGroup) {
        Configuration confOut = carrierMonthlyPieChart.getConfiguration();
        /**/
        Tooltip tooltip = new Tooltip();
        tooltip.setValueDecimals(0);
        tooltip.setHeaderFormat("<span style=\"font-size: 10px\">{point.key} {point.percentage:%02.2f}%</span><br/>");
        confOut.setTooltip(tooltip);
        /**/
        PlotOptionsPie innerPieOptions = new PlotOptionsPie();
        innerPieOptions.setSize("70%");
        List<DataSeries> list_series =
                findDataSeriesPieBase(carrierDayGroup,
                        "Total",
                        innerPieOptions);
        for (DataSeries list_sery : list_series) {
            confOut.addSeries(list_sery);
        }
        confOut.setTitle("Junio");
    }

    /**
     * @param smsGroup     Agrupado por Year-Month-MessageType
     * @param carrierGroup Data agrupada por Year-Month-Carrier-MessageType
     */
    private void populateTriColumnLineChart(List<? extends AbstractSmsByYearMonth> smsGroup, List<SmsByYearMonth> carrierGroup) {
        Configuration confTriChart = carrierTriMixChart.getConfiguration();
        /**/
        Tooltip tooltip = new Tooltip();
        tooltip.setShared(true);
        tooltip.setValueDecimals(0);
        tooltip.setHeaderFormat("<span style=\"font-size: 10px\">{point.key} {point.percentage:%02.2f}%</span><br/>");
        confTriChart.setTitle("Trimestre");
        confTriChart.setTooltip(tooltip);
        /**/
//        System.out.println("************************************************ ");
//        carrierGroup.stream().forEach(System.out::println);
//        System.out.println("************************************************ ");

        /**/
//        configureColumnChart(confIn);
        /* Averiguar cuales son los tres meses a calular. */
        confTriChart.getxAxis().setCategories(ml);
        /**/
//        System.out.println("BEFORE FILL AND ORDER ");
//        smsGroup.stream().forEach(System.out::println);
        smsGroup = orderGroup(fillWithCero(smsGroup, monthToShowList));
//        System.out.println("AFTER FILL AND ORDER ");
//        smsGroup.stream().forEach(System.out::println);
        List<DataSeries> columnDataSeriesList = findDataSeriesColumnsBase(smsGroup);

        for (DataSeries list_sery : columnDataSeriesList) {
            confTriChart.addSeries(list_sery);
        }
//        DataProvider<SmsByYearMonth, ?> dataProvider = new ListDataProvider<>(smsGroup);
//
//        DataProviderSeries<SmsByYearMonth> series = new DataProviderSeries<>(dataProvider, SmsByYearMonth::getTotal);
//        confIn.addSeries(series);

        /**/
        System.out.println("BEFORE FILL AND ORDER: CARRIER GROUP ");
        carrierGroup.stream().forEach(System.out::println);
        smsGroup = orderGroup(fillWithCero(smsGroup, monthToShowList));
        System.out.println("AFTER FILL AND ORDER: CARRIER GROUP ");
        carrierGroup.stream().forEach(System.out::println);
        /**/
        List<? extends AbstractSmsByYearMonth> l = carrierGroup;
//        log.info("TRIMESTRE LINE DATA: {}", l);
        /* Averiguar cuales son los tres meses a calular. */
        List<Integer> monthList = monthToShowList;

        System.out.println("L ANTES DE");
        /* AGRUPAR POR  */
        Map<String, List<SmsByYearMonth>> gbc =
                carrierGroup.stream()
                        .collect(Collectors.groupingBy(s -> "" + s.getGroupBy()));
        System.out.println("AGRUPADO ***");
        gbc.entrySet().stream().forEach(sms -> {
            long total = 0;
            for (SmsByYearMonth bym : sms.getValue()) {
                total += bym.getTotal();
            }
        });
        gbc.entrySet().stream().forEach(System.out::println);

        l.stream().forEach(System.out::println);
//        l = orderGroup(fillWithCero(l, monthToShowList));
        List<Series> LineDateSeriesList = paEntenderLine(l, monthToShowList);
        if (LineDateSeriesList == null || LineDateSeriesList.size() == 0) {
            log.info("{} NO DATA FOR CARRIER CHART LINE", getStringLog());
        } else {
            for (int i = 0; i < LineDateSeriesList.size(); i++) {
                System.out.println("ADDING ********" + LineDateSeriesList.get(i).getName());
                confTriChart.addSeries(LineDateSeriesList.get(i));
            }
        }
    }

    private void populatePieChart(List<SmsByYearMonth> smsByYearMonth) {
        Configuration conf = carrierTriPieChart.getConfiguration();
        PlotOptionsPie innerPieOptions = new PlotOptionsPie();
        innerPieOptions.setSize("70%");
        /**/
        Tooltip tooltip = new Tooltip();
        tooltip.setValueDecimals(0);
        tooltip.setHeaderFormat("<span style=\"font-size: 10px\">{point.key} {point.percentage:%02.2f}%</span><br/>");
        conf.setTooltip(tooltip);
        /**/
        List<DataSeries> list_series = findDataSeriesPieBase(smsByYearMonth,
                "Total",
                innerPieOptions);
        for (DataSeries list_sery : list_series) {
            conf.addSeries(list_sery);
        }
        conf.setTitle("Trimestre");
    }

    private void populateMonthChart(List<? extends AbstractSmsByYearMonth> smsGroup, List<SmsByYearMonthDay> carrierGroup) {
        Configuration confIn = carrierDailyChart.getConfiguration();
        /**/
        confIn.getxAxis().setTitle("Hora");
        confIn.getyAxis().setTitle("SMS");
        Tooltip tooltip = new Tooltip();
        tooltip.setValueDecimals(0);
        tooltip.setHeaderFormat("<span style=\"font-size: 10px\">{point.key} {point.percentage:%02.2f}%</span><br/>");
        tooltip.setShared(true);
        confIn.setTooltip(tooltip);
        /**/
        configureColumnChart(confIn);
        smsGroup = orderGroup(fillWithCero(smsGroup, monthToShowList));
        /**/
        List<DataSeries> list_series1 = findDataSeriesColumnsBase(smsGroup);
        for (DataSeries list_sery : list_series1) {
            confIn.addSeries(list_sery);
        }
//        DataProvider<SmsByYearMonth, ?> dataProvider = new ListDataProvider<>(smsGroup);
//
//        DataProviderSeries<SmsByYearMonth> series = new DataProviderSeries<>(dataProvider, SmsByYearMonth::getTotal);
//        confIn.addSeries(series);

        List<? extends AbstractSmsByYearMonth> l = carrierGroup;
        log.info("MONTH CHART: {}", l);
        /* Averiguar cuales son los tres meses a calular. */
        List<Integer> monthList = monthToShowList;

        l = orderGroup(fillWithCero(l, monthToShowList));
        List<DataSeries> list_series2 = findDataSeriesLineBase(l);
        if (list_series2 == null || list_series2.size() == 0) {
            log.info("{} NO DATA FOR CARRIER CHART LINE", getStringLog());
        } else {
            for (int i = 0; i < list_series2.size() - 1; i++) {
                confIn.addSeries(list_series2.get(i));
            }
        }
    }

    /**
     * Agrega la columna TOTAL
     *
     * @param smsGroupList
     * @return
     */
    public List<DataSeries> findDataSeriesColumnsBase(List<? extends AbstractSmsByYearMonth> smsGroupList) {
        Map<Integer, Map<String, Long>> monthlyDataToShowMap = new HashMap<>();
        int m = 0;
        System.out.println("CarrierEvolution: Columns size() " + smsGroupList.size());
        List<Integer> monthToShowList = new ArrayList<>(4);

        Map<String, Long> messageTypeToShowMap;
        /* Recorre todos los meses para agregar e inicializar
         los MessageType seleccionados en 0.*/
        for (AbstractSmsByYearMonth smsByYearMonth : smsGroupList) {
            System.out.println("columns base-> " + smsByYearMonth);
            if (!monthlyDataToShowMap.containsKey(smsByYearMonth.getGroupBy())) {
                monthToShowList.add(smsByYearMonth.getGroupBy());
                messageTypeToShowMap = new HashMap<>();
                /* Agregar el MessageType al Map e inicializar en 0 */
                for (OMessageType selectedItem : OMessageType.values()) {
                    messageTypeToShowMap.put(selectedItem.name(), 0l);
                }
                monthlyDataToShowMap.put(smsByYearMonth.getGroupBy(), messageTypeToShowMap);
            }
            /* Hasta este momento todos los valores de lso MessageType estan inicializados
             * en 0.*/
            messageTypeToShowMap = monthlyDataToShowMap.get(smsByYearMonth.getGroupBy());
            /* Sustituyo en el map el valor real del MessageType  */
            messageTypeToShowMap.put(smsByYearMonth.getSomeCode(), smsByYearMonth.getTotal());
        }

        /*Llenar los MessageType con los valores de los meses
        ejem: {TOTAL=[7023, 0, 56933], MO=[2775, 0, 22620], MT=[4248, 0, 34313]}*/
        Map<String, List<Number>> MessageTypeMap = new HashMap<>();
        for (Integer integer : monthToShowList) {
            messageTypeToShowMap = monthlyDataToShowMap.get(integer);
            System.out.println("Column Month: " + integer + " " + messageTypeToShowMap);
            /* Agragar al map los Message Type selecionados. */
            for (OMessageType omessage_type : OMessageType.values()) {
                if (!MessageTypeMap.containsKey(omessage_type.name())) {
                    MessageTypeMap.put(omessage_type.name(), new ArrayList<>());
                }
                MessageTypeMap.get(omessage_type.name()).add(messageTypeToShowMap.get(omessage_type.name()));
            }
        }
        /* Calculo parra la columna total */
        List<Number> list_total = new ArrayList<>();
        MessageTypeMap.put("TOTAL", list_total);
        long total = 0;
        long max_value_y = 0;
        for (Integer integer : monthToShowList) {
            messageTypeToShowMap = monthlyDataToShowMap.get(integer);
            for (OMessageType omessage_type : OMessageType.values()) {
                total += messageTypeToShowMap.get(omessage_type.name());
            }
            if (total > max_value_y) {
                max_value_y = total;
            }
            list_total.add(total);
            total = 0;
        }

        List<DataSeries> list_series = new ArrayList<>();
        PlotOptionsColumn splinePlotOptions = new PlotOptionsColumn();
        System.out.println("**************** " + MessageTypeMap);
        for (OMessageType omessage_type : OMessageType.values()) {
            DataSeries series = new DataSeries();
            series.setPlotOptions(splinePlotOptions);
            series.setName(omessage_type.name());
            series.setData(MessageTypeMap.get(omessage_type.name()).toArray(new Number[MessageTypeMap.get(omessage_type.name()).size()]));
            list_series.add(series);
//            conf.addSeries(series);
        }

        /* Vaidar si las columnas son mas de una
         * para agregar la columna total */
//        if (OMessageType.values().length > 1) {
//            DataSeries series = new DataSeries();
//            series.setPlotOptions(splinePlotOptions);
//            series.setName("TOTAL");
//            series.setData(list_total.toArray(new Number[list_total.size()]));
//            list_series.add(series);
//        }
//        conf.getyAxis().setMax(max_value_y * 1.30);
        return list_series;
    }

    public List<Series> paEntender(List<? extends AbstractSmsByYearMonth> l, List<Integer> integerList) {
        l.stream().forEach(System.out::println);

        List<Series> dataSeriesList = new ArrayList<>();
        /*TODO nullpointer*/
        /* Recorre los Carrier seleccionados. */
        PlotOptionsColumn splinePlotOptions = new PlotOptionsColumn();
        multi_messagetype.getSelectedItems().forEach(messageType -> {
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
                series.setPlotOptions(splinePlotOptions);
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


    public List<DataSeries> findDataSeriesLineBase(List<? extends AbstractSmsByYearMonth> l) {

        Map<Integer, Map<String, Long>> data_monthly = new HashMap<>();
        List<Integer> lmonth = new ArrayList<>(4);

        Map<String, Long> carrierTotalMap = new HashMap<>();
        /* Agrega 0 a todos los carrier en toda la agrupacion (MONTH/DAY) */
        for (AbstractSmsByYearMonth smsByYearMonth : l) {
            log.info("GROUP BY: {}  - SOMECODE: {} ", smsByYearMonth.getSomeCode(), smsByYearMonth.getGroupBy());

            /* Si no esta el Month/day/Hour agregarlos carrier seleccionados en 0l */
            if (!data_monthly.containsKey(smsByYearMonth.getGroupBy())) {
                log.info("GRUPO AGREGADO: {}, {}", smsByYearMonth.getGroupBy(), smsByYearMonth.getSomeCode());

                lmonth.add(smsByYearMonth.getGroupBy());
                carrierTotalMap = new HashMap<>();

                log.info("GRUPO INIC EN 0: {}, {}", smsByYearMonth.getGroupBy(), smsByYearMonth.getSomeCode());
                /* Agragar al map cada uno de los carrier selecionados con 0. */
                for (Carrier ocarrier : multi_carrier.getSelectedItems()) {
                    carrierTotalMap.put(ocarrier.getCarrierCharcode(), 0l);
                }
                /* Agregar al Month/day/Hour el map con los 0l*/
                data_monthly.put(smsByYearMonth.getGroupBy(), carrierTotalMap);
            } else {
                log.info("* NO AGREGADO: {}, {}", smsByYearMonth.getSomeCode(), smsByYearMonth.getGroupBy());
            }
            /* TODO: Hacer que del repositorio solo vengan las operadoras */
            if (!carrierTotalMap.containsKey(smsByYearMonth.getSomeCode())) {
                log.info("* SOMECODE NO VA AL GRAFICO : SOME:{}, GROUP:{}", smsByYearMonth.getSomeCode(), smsByYearMonth.getGroupBy());
                continue;
            }
            /* Agrego en el Month/day/Hour en valor real */
            carrierTotalMap = data_monthly.get(smsByYearMonth.getGroupBy());
            carrierTotalMap.put(smsByYearMonth.getSomeCode(), smsByYearMonth.getTotal());
        }

        /*Llenar los Meses con number*/
        Map<String, List<Number>> list = new HashMap<>();
        for (Integer integer : lmonth) {
            carrierTotalMap = data_monthly.get(integer);
            System.out.println("Month/Day/Hour: " + integer + " " + carrierTotalMap);

            /* Agragar al map los carrier selecionados. */
            for (Carrier ocarrier : multi_carrier.getSelectedItems()) {
                if (!list.containsKey(ocarrier.getCarrierCharcode())) {
                    list.put(ocarrier.getCarrierCharcode(), new ArrayList<>());
                }
                System.out.println("list.get " + ocarrier.getCarrierCharcode() + " " + carrierTotalMap.get(ocarrier.getCarrierCharcode()));
                list.get(ocarrier.getCarrierCharcode()).add(carrierTotalMap.get(ocarrier.getCarrierCharcode()));
            }
        }

        List<Number> list_total = new ArrayList<>();
        list.put("TOTAL", list_total);
        long total = 0;
        for (Integer integer : lmonth) {
            carrierTotalMap = data_monthly.get(integer);
            for (Carrier ocarrier : multi_carrier.getSelectedItems()) {
                total += carrierTotalMap.get(ocarrier.getCarrierCharcode());
            }
            list_total.add(total);
            total = 0;
        }

        List<DataSeries> list_series = new ArrayList<>();
        PlotOptionsSpline splinePlotOptions = new PlotOptionsSpline();
        for (Carrier ocarrier : multi_carrier.getSelectedItems()) {
            DataSeries series = new DataSeries();
            series.setPlotOptions(splinePlotOptions);
            series.setName(ocarrier.getCarrierCharcode());

            List<Number> numberList = list.get(ocarrier.getCarrierCharcode());
            log.info("[{}] Colum size [{}]", getStringLog(), numberList);
            if (numberList == null || numberList.isEmpty()) {
                log.info("[{}] No data. Return empty DataSeries", getStringLog());
                return new ArrayList<>();
            }
            series.setData(list.get(ocarrier.getCarrierCharcode()).toArray(new Number[numberList.size()]));
            list_series.add(series);
//            conf.addSeries(series);
        }
        /* Agregaar el TOTAL */
        DataSeries series = new DataSeries();
        series.setPlotOptions(splinePlotOptions);
        series.setName("TOTAL");
        series.setData(list_total.toArray(new Number[list_total.size()]));
        list_series.add(series);
        return list_series;
    }

    private void configureColumnChart(Configuration conf) {
        conf.getChart().setType(ChartType.COLUMN);
        conf.getChart().setBorderRadius(4);

        conf.getxAxis().setTickInterval(1);
        conf.getxAxis().setMinorTickLength(0);
        conf.getxAxis().setTickLength(0);

        conf.getyAxis().getTitle().setText(null);

        conf.getLegend().setEnabled(false);
    }

    private String getStringLog() {
        return "";
//        return "[" + Application.getAPP_NAME() + "] [" + ouser_session.getUserEmail() + "]"
//                + " [" + UI_CODE + "]";
    }

    private List<AbstractSmsByYearMonth> fillWithCero(List<? extends AbstractSmsByYearMonth> listToFill, List<Integer> monthList) {
        boolean hasToFill = false;
        List<AbstractSmsByYearMonth> l = new ArrayList<>(listToFill);
        for (Integer monthLoop : monthList) {
            hasToFill = true;
            for (AbstractSmsByYearMonth sms : listToFill) {
                if (sms.getGroupBy() == monthLoop) {
                    hasToFill = false;
                    break;
                }
            }
            if (hasToFill) {
                log.info("TRIMESTRE COLUMN DATA - ADDING MONTH({}) WITH 0 ", monthLoop);
                AbstractSmsByYearMonth o = listToFill.get(0).getObject(0, actual_year, monthLoop, "N/A");
                l.add(o);
            }
        }
        return l;
    }

    private List<AbstractSmsByYearMonth> orderGroup(List<? extends AbstractSmsByYearMonth> listToFill) {
        /* Ordenar la lista por YearMonth */
        Map<String, AbstractSmsByYearMonth> forOrderedMap = new HashMap<>();
        for (AbstractSmsByYearMonth smsByYearMonth : listToFill) {
            forOrderedMap.put(smsByYearMonth.forKey(), smsByYearMonth);
        }
        SortedSet<String> keys = new TreeSet<>(forOrderedMap.keySet());
//        log.info("TRIMESTRE LINE DATA - BEFORE CLEARING {}", listToFill);
        listToFill.clear();
        List<AbstractSmsByYearMonth> l = new ArrayList<>();
        keys.forEach(key -> {
            l.add(forOrderedMap.get(key));
        });
        return l;
    }

    private List<DataSeries> findDataSeriesPieBase(List<? extends SmsByYearMonth> l,
                                                   String serieName,
                                                   PlotOptionsPie plotOptionsPie) {

        Map<String, Long> mapMx = new HashMap<>();

        /* Agregar al Map e inicializar a 0 los carrier selecionados. */
        multi_carrier.getSelectedItems().forEach(oCarrier -> {
            mapMx.put(oCarrier.getCarrierCharcode(), 0l);
        });
        DataSeries pieSeries = new DataSeries();

        System.out.println("********************//1///**************************");
        l.stream().forEach(System.out::println);
        /* Agrupar por Carrier */
        Map<String, List<SmsByYearMonth>> groupByCarrier =
                l.stream().collect(Collectors.groupingBy(s -> s.getSomeCode()));
        System.out.println("********************//2///**************************");
        groupByCarrier.entrySet().stream().forEach(System.out::println);
        DataSeries donutSeries = new DataSeries();
        long startTime = System.currentTimeMillis();
        groupByCarrier.entrySet().forEach(carrier -> {
                    long totalCarrier = carrier.getValue()
                            .stream()
                            .mapToLong(p -> p.getTotal())
                            .sum();
                    pieSeries.add(new DataSeriesItem(carrier.getKey(), totalCarrier));
                    log.info("{} ****** {}", carrier.getKey(), totalCarrier);


                    Map<String, List<SmsByYearMonth>> g =
                            carrier.getValue().stream().collect(Collectors.groupingBy(s -> s.getMessageType()));
                    g.entrySet().stream().forEach(System.out::println);
                    g.entrySet().forEach(c -> {
                        long tc = c.getValue()
                                .stream()
                                .mapToLong(p -> p.getTotal())
                                .sum();
                        donutSeries.add(new DataSeriesItem(c.getKey(), tc));
                        System.out.println("Este " + c.getKey() + " " + tc);

                    });
//                    carrier.getValue().forEach(mt -> {
//                        series.add(new DataSeriesItem(mt.getMessageType(), mt.getTotal()));
//                        log.info("{} **C** {}", mt.getMessageType(), mt.getTotal());
//                    });
//                    System.out.println("Este "+carrier.getKey() + " " + carrier.getValue());
                }
        );
        System.out.println("SE TARDO: " + (System.currentTimeMillis() - startTime));
        pieSeries.setPlotOptions(plotOptionsPie);

//        /* Guardar los totales del Mes*/
//        for (SmsByYearMonth smsByYearMonth : l) {
//            /*TODO: El repo debe devolver solo los Carrier Seleccionados*/
//            /*if (!mapMx.containsKey(smsByYearMonth.getSomeCode())) {
//                continue;
//            }*/
//            log.info("ELE {}  ************************* ", smsByYearMonth);
//            mapMx.put(smsByYearMonth.getSomeCode(), smsByYearMonth.getTotal() + mapMx.get(smsByYearMonth.getSomeCode()));
//        }
//
//        DataSeries series = new DataSeries();
//        series.setName(serieName);
//        mapMx.forEach((cod, ocarrier) -> {
//            DataSeriesItem item = new DataSeriesItem(cod, ocarrier);
//            log.info("Serie:{} Cod: {} Long:{} ************************* ", serieName, cod, ocarrier);
//            series.add(item);
//        });
////        PlotOptionsPie plotOptionsPie = new PlotOptionsPie();
////        plotOptionsPie.setInnerSize("60%");
////        plotOptionsPie.getDataLabels().setCrop(false);
//        series.setPlotOptions(plotOptionsPie);
////        conf.addSeries(deliveriesPerProductSeries);
//
////        PlotOptionsPie plotOptionsPie = new PlotOptionsPie();
////        plotOptionsPie.setSize("100px");
////        plotOptionsPie.setCenter("100px", "80px");
////        plotOptionsPie.setShowInLegend(false);
////        plotOptionsPie.setDepth(5);
////        series.setPlotOptions(plotOptionsPie);
        PlotOptionsPie plotOptionsPie2 = new PlotOptionsPie();
        plotOptionsPie2.setInnerSize("75%");
        plotOptionsPie2.getDataLabels().setEnabled(false);
        plotOptionsPie2.getDataLabels().setCrop(false);
        donutSeries.setPlotOptions(plotOptionsPie2);
        List<DataSeries> list_series = new ArrayList<>(2);
        list_series.add(pieSeries);
        list_series.add(donutSeries);
        /**/
        pieSeries.setName("SMS");
        donutSeries.setName("SMS");
        return list_series;
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

    private void setActualDate() {
        Calendar c = Calendar.getInstance();
        actual_day = c.get(Calendar.DAY_OF_MONTH);
        actual_month = c.get(Calendar.MONTH) + 1;
        actual_year = c.get(Calendar.YEAR);
        log.info("{} HOUR OF DAY {} HOUR {}", getStringLog(), c.get(Calendar.HOUR_OF_DAY), c.get(Calendar.HOUR));
        actual_hour = c.get(Calendar.HOUR_OF_DAY);
        for (int i = 0; i <= actual_hour; i++) {
            hourList.add(i);
        }
        for (int i = 1; i <= actual_day; i++) {
            dayList.add(i);
        }
    }
}
