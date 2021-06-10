package com.stt.dash.ui.views.dashboard.main;

import com.stt.dash.app.OMessageType;
import com.stt.dash.app.OMonths;
import com.stt.dash.app.security.CurrentUser;
import com.stt.dash.app.session.ListGenericBean;
import com.stt.dash.backend.data.*;
import com.stt.dash.backend.data.entity.Order;
import com.stt.dash.backend.data.entity.OrderSummary;
import com.stt.dash.backend.data.entity.Product;
import com.stt.dash.backend.service.OrderService;
import com.stt.dash.backend.service.SmsHourService;
import com.stt.dash.ui.MainView;
import com.stt.dash.ui.dataproviders.OrdersGridDataProvider;
import com.stt.dash.ui.utils.BakeryConst;
import com.stt.dash.ui.utils.FormattingUtils;
import com.stt.dash.ui.views.dashboard.DataSeriesItemWithRadius;
import com.stt.dash.ui.views.storefront.OrderCard;
import com.stt.dash.ui.views.storefront.beans.OrdersCountData;
import com.stt.dash.ui.views.storefront.beans.OrdersCountDataWithChart;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.Tag;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.charts.Chart;
import com.vaadin.flow.component.charts.events.ChartLoadEvent;
import com.vaadin.flow.component.charts.model.*;
import com.vaadin.flow.component.dependency.JsModule;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.polymertemplate.Id;
import com.vaadin.flow.component.polymertemplate.PolymerTemplate;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.templatemodel.TemplateModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import java.time.LocalDate;
import java.time.MonthDay;
import java.time.Year;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Tag("main-view")
@JsModule("./src/views/main/main-view.js")
@Route(value = BakeryConst.PAGE_DASHBOARD_MAIN, layout = MainView.class)
@PageTitle(BakeryConst.TITLE_DASHBOARD_MAIN)
public class MainDashboardView extends PolymerTemplate<TemplateModel> {
    private Logger log = LoggerFactory.getLogger(MainDashboardView.class);
    private static final String[] MONTH_LABELS = new String[]{"ENERO", "FEBRERO", "MARZO", "ABRIL", "MAYO", "JUNIO",
            "JULIO", "AGOSTO", "SEPTIEMBRE", "OCTUBRE", "NOVIEMBRE", "DICIEMBRE"};

    private static final String[] MILITARY_HOURS = new String[]{"0:00", "1:00", "2:00", "3:00", "4:00", "5:00", "6:00", "7:00", "8:00", "9:00", "10:00", "11:00", "12:00", "13:00", "14:00", "15:00", "16:00", "17:00", "18:00", "19:00", "20:00", "21:00", "22:00", "23:00"};

    private final OrderService orderService;
    private final SmsHourService smsHourService;
    private final ListGenericBean<String> stingListGenericBean;
    private final CurrentUser currentUser;
    /**/
    private static int actualYear;
    private static int actualMonth;
    private static int actualDay;
    private static int actualHour;
    /**/
    private final static String UI_CODE = "MDV";
    @Id("todayCountt")
    private MainCounterLabel mtCounterLabel;
//
//    @Id("notAvailableCount")
//    private DashboardCounterLabel notAvailableCount;

    @Id("newCount")
    private MainCounterLabel moCounterLabel;

    @Id("tomorrowCount")
    private MainCounterLabel totalCounterLabel;

    @Id("deliveriesThisMonth")
    private Chart deliveriesThisMonthChart;

    @Id("deliveriesThisYear")
    private Chart deliveriesThisYearChart;

    @Id("yearlySalesGraph")
    private Chart yearlySalesGraph;

    @Id("ordersGrid")
    private Grid<Order> grid;

    @Id("monthlyProductSplit")
    private Chart monthlyProductSplit;

    @Id("todayCountChart")
    private Chart todayCountChart;

    @Autowired
    public MainDashboardView(OrderService orderService,
                             OrdersGridDataProvider orderDataProvider,
                             SmsHourService smsHourService,
                             @Qualifier("getUserSystemIdString") ListGenericBean<String> stringListGenericBean,
                             CurrentUser currentUser) {
        this.orderService = orderService;
        this.smsHourService = smsHourService;
        this.stingListGenericBean = stringListGenericBean;
        this.currentUser = currentUser;
        /**/
        setActualDate();
        /**/
        grid.addColumn(OrderCard.getTemplate()
                .withProperty("orderCard", OrderCard::create)
                .withProperty("header", order -> null)
                .withEventHandler("cardClick",
                        order -> UI.getCurrent().navigate(BakeryConst.PAGE_STOREFRONT + "/" + order.getId())));

        grid.setSelectionMode(Grid.SelectionMode.NONE);
        grid.setDataProvider(orderDataProvider);

        DashboardData data = orderService.getDashboardData(MonthDay.now().getMonthValue(), Year.now().getValue());
        populateMonthlySmsChart(data);
        populateDeliveriesCharts(data);
        populateOrdersCounts(data.getDeliveryStats());
        initProductSplitMonthlyGraph(data.getProductDeliveries());

        measurePageLoadPerformance();
    }

    // This method is overridden to measure the page load performance and can be safely removed
    // if there is no need for that.
    private void measurePageLoadPerformance() {
        final int nTotal = 5; // the total number of charts on the page
        AtomicInteger nLoaded = new AtomicInteger();
        ComponentEventListener<ChartLoadEvent> chartLoadListener = (event) -> {
            nLoaded.addAndGet(1);
            if (nLoaded.get() == nTotal) {
                UI.getCurrent().getPage().executeJavaScript("$0._chartsLoadedResolve()", this);
            }
        };

        todayCountChart.addChartLoadListener(chartLoadListener);
        deliveriesThisMonthChart.addChartLoadListener(chartLoadListener);
        deliveriesThisYearChart.addChartLoadListener(chartLoadListener);
        yearlySalesGraph.addChartLoadListener(chartLoadListener);
        monthlyProductSplit.addChartLoadListener(chartLoadListener);
    }

    private void initProductSplitMonthlyGraph(Map<Product, Integer> productDeliveries) {
        LocalDate today = LocalDate.now();
        /**/
        // The inner pie
        DataSeries innerSeries = new DataSeries();
        PlotOptionsPie innerPieOptions = new PlotOptionsPie();
        innerPieOptions.setSize("70%");
        innerSeries.setPlotOptions(innerPieOptions);
        /**/
        Configuration conf = monthlyProductSplit.getConfiguration();
        conf.getChart().setType(ChartType.PIE);
        conf.getChart().setBorderRadius(4);
        conf.setTitle("Products delivered in " + FormattingUtils.getFullMonthName(today));
        stingListGenericBean.getList().stream().forEach(System.out::println);
        List<SmsByYearMonth> groupList = smsHourService.groupCarrierByYeMoMeWhMoEqMessageTypeIn(actualYear, actualMonth, Arrays.asList("MT", "MO"), stingListGenericBean.getList());
        groupList.stream().forEach(System.out::println);
        /* Agrupar por Carrier */
        Map<String, List<SmsByYearMonth>> gbc =
                groupList.stream().collect(Collectors.groupingBy(SmsByYearMonth::getSomeCode));
        System.out.println("**************** ");
        DataSeries series = new DataSeries();
        gbc.entrySet().forEach(carrier -> {
                    long totalCarrier = carrier.getValue()
                            .stream()
                            .mapToLong(p -> p.getTotal())
                            .sum();
                    innerSeries.add(new DataSeriesItem(carrier.getKey(), totalCarrier));

                    carrier.getValue().forEach(mt -> {
                        series.add(new DataSeriesItem(mt.getMessageType(), mt.getTotal()));

                    });
                    System.out.println(carrier.getKey() + " " + carrier.getValue());
                }
        );
//        DataSeries deliveriesPerProductSeries = new DataSeries(productDeliveries.entrySet().stream()
//                .map(e -> new DataSeriesItem(e.getKey().getName(), e.getValue())).collect(Collectors.toList()));
        DataLabels dataLabels = new DataLabels();
        dataLabels.setEnabled(true);
        dataLabels
                .setFormatter("'<b>'+ this.point.name +'</b>: '+ this.percentage +' %'");
        PlotOptionsPie plotOptionsPie = new PlotOptionsPie();
        plotOptionsPie.setInnerSize("75%");
        plotOptionsPie.getDataLabels().setEnabled(false);
        plotOptionsPie.getDataLabels().setCrop(false);
//        deliveriesPerProductSeries.setPlotOptions(plotOptionsPie);
//        conf.addSeries(deliveriesPerProductSeries);
        series.setPlotOptions(plotOptionsPie);
        Tooltip tooltip = new Tooltip();
        tooltip.setValueDecimals(0);
        tooltip.setHeaderFormat("<span style=\"font-size: 10px\">{point.key} {point.percentage:%02.2f}%</span><br/>");
//        tooltip.setPointFormat(" <span style=\"color:{series.color}\">{point.key}{series.name}</span>: <b>{point.y}</b> ");
        //        series.getConfiguration().setTooltip(tooltip);
        innerSeries.setName("");
        series.setName("");
        conf.setSeries(innerSeries, series);
        conf.setTooltip(tooltip);
        }

    private void populateOrdersCounts(DeliveryStats deliveryStats) {
        List<OrderSummary> orders = orderService.findAnyMatchingStartingToday();
        /* Buscando totales MT y MO del mes actual para mostrarlo en pantalla principal */
        List<SmsByYearMonth> groupByYearMonth = smsHourService.getGroupSmsByYearMonthMessageType(actualYear, actualMonth, stingListGenericBean.getList());
        long t_mo = 0;
        long t_mt = 0;
        System.out.println("Lista devuelta " + groupByYearMonth.size());
        for (SmsByYearMonth smsByYearMonth : groupByYearMonth) {
            if (smsByYearMonth.getSomeCode().equals("MT")) {
                t_mt = smsByYearMonth.getTotal();
            } else {
                t_mo = smsByYearMonth.getTotal();
            }
        }

        OrdersCountData smsCountDataWithChart = new OrdersCountData("Mensajes Eviados (MT)", OMonths.valueOf(actualMonth).getMonthName(), (int) t_mt);
        mtCounterLabel.setOrdersCountData(smsCountDataWithChart);

        smsCountDataWithChart = new OrdersCountData("Mensajes Recibidos (MO)", OMonths.valueOf(actualMonth).getMonthName(), (int) t_mo);
        moCounterLabel.setOrdersCountData(smsCountDataWithChart);

        smsCountDataWithChart = new OrdersCountData("Total", OMonths.valueOf(actualMonth).getMonthName(), (int) (t_mo + t_mt));
        totalCounterLabel.setOrdersCountData(smsCountDataWithChart);

        /**/
//        OrdersCountDataWithChart todaysOrdersCountData = DashboardUtils
//                .getTodaysOrdersCountData(deliveryStats, orders.iterator());
//        mtCounterLabel.setOrdersCountData(todaysOrdersCountData);
//        initTodayCountSolidgaugeChart(todaysOrdersCountData);
//        notAvailableCount.setOrdersCountData(DashboardUtils.getNotAvailableOrdersCountData(deliveryStats));
//        Order lastOrder = orderService.load(orders.get(orders.size() - 1).getId());
//        moCounterLabel.setOrdersCountData(DashboardUtils.getNewOrdersCountData(deliveryStats, lastOrder));
//        totalCounterLabel.setOrdersCountData(DashboardUtils.getTomorrowOrdersCountData(deliveryStats, orders.iterator()));
    }


    private void initTodayCountSolidgaugeChart(OrdersCountDataWithChart data) {
        Configuration configuration = todayCountChart.getConfiguration();
        configuration.getChart().setType(ChartType.SOLIDGAUGE);
        configuration.setTitle("");
        configuration.getTooltip().setEnabled(false);

        configuration.getyAxis().setMin(0);
        configuration.getyAxis().setMax(1);
        configuration.getyAxis().getLabels().setEnabled(false);

        PlotOptionsSolidgauge opt = new PlotOptionsSolidgauge();
        opt.getDataLabels().setEnabled(false);
        configuration.setPlotOptions(opt);

        DataSeriesItemWithRadius point = new DataSeriesItemWithRadius();
        point.setY(1);
        point.setInnerRadius("100%");
        point.setRadius("110%");
        configuration.setSeries(new DataSeries(point));

        Pane pane = configuration.getPane();
        pane.setStartAngle(0);
        pane.setEndAngle(360);

        Background background = new Background();
        background.setShape(BackgroundShape.ARC);
        background.setInnerRadius("100%");
        background.setOuterRadius("110%");
        pane.setBackground(background);
    }

    private void populateDeliveriesCharts(DashboardData data) {
        LocalDate today = LocalDate.now();

        // init the 'Deliveries in [this year]' chart
        Configuration yearConf = deliveriesThisYearChart.getConfiguration();
        configureColumnChart(yearConf);

        yearConf.setTitle("Mensajes del dia: " + today.getDayOfMonth());
        yearConf.getxAxis().setCategories(MILITARY_HOURS);
        List<SmsByYearMonthDayHour> smsByHour = smsHourService.getGroupSmsByYearMonthDayHourMessageType(actualYear, actualMonth, actualDay, stingListGenericBean.getList());

        List<Number> mtHour = fillHouList(smsByHour, "MT");
        List<Number> moHour = fillHouList(smsByHour, "MO");
        /**/

        ListSeries mtHourListSeries = new ListSeries("MT");
        ListSeries moHourListSeries = new ListSeries("MO");
        mtHourListSeries.setData(mtHour);
        moHourListSeries.setData(moHour);
//        yearConf.addSeries(new ListSeries("per Month", data.getDeliveriesThisYear()));
        Tooltip tooltip = new Tooltip();
        tooltip.setValueDecimals(0);
//        tooltip.setHeaderFormat("<span style=\"font-size: 10px\">Día: {point.x}</span><br/>");
        tooltip.setShared(true);
        yearConf.setSeries(mtHourListSeries, moHourListSeries);
        yearConf.setTooltip(tooltip);
        /**/
        // init the 'Deliveries in [this month]' chart
        Configuration monthConf = deliveriesThisMonthChart.getConfiguration();
        configureColumnChart(monthConf);
        /**/
        List<SmsByYearMonthDay> groupSmsByYearMonthDayMessageType = smsHourService.getGroupSmsByYearMonthDayMessageType(actualYear, actualMonth, stingListGenericBean.getList());
        System.out.println("DEVUELTOS: " + groupSmsByYearMonthDayMessageType.size());
        List<Number> mt = fillDays(groupSmsByYearMonthDayMessageType, "MT");
        List<Number> mo = fillDays(groupSmsByYearMonthDayMessageType, "MO");
//        DataProvider<SmsByYearMonthDay, ?> dataProvider = new ListDataProvider<>(groupSmsByYearMonthDayMessageType);

        /**/
//        List<Number> deliveriesThisMonth = data.getDeliveriesThisMonth();

        String[] deliveriesThisMonthCategories = IntStream.rangeClosed(1, mt.size())
                .mapToObj(String::valueOf).toArray(String[]::new);
//        DataProviderSeries<SmsByYearMonthDay> series = new DataProviderSeries<>(dataProvider, SmsByYearMonthDay::getTotal);
        monthConf.setTitle("Mensajes diarios en " + FormattingUtils.getFullMonthName(today));
        monthConf.getxAxis().setCategories(deliveriesThisMonthCategories);
        monthConf.getxAxis().setCrosshair(new Crosshair());
        ListSeries mtListSeries = new ListSeries("MT");
        ListSeries moListSeries = new ListSeries("MO");
        mtListSeries.setData(mt);
        moListSeries.setData(mo);
        PlotOptionsColumn plotColumn = new PlotOptionsColumn();
        plotColumn.setStacking(Stacking.NONE);
        Tooltip tooltip2 = new Tooltip();
        tooltip2.setValueDecimals(0);
//        tooltip.setHeaderFormat("<span style=\"font-size: 10px\">Día: {point.x}</span><br/>");
        tooltip2.setShared(true);
        monthConf.setSeries(mtListSeries, moListSeries);
        monthConf.setPlotOptions(plotColumn);
        monthConf.setTooltip(tooltip2);
    }

    private void configureColumnChart(Configuration conf) {
        conf.getChart().setType(ChartType.COLUMN);
        conf.getChart().setBorderRadius(4);
        /**/
        conf.getxAxis().setTickInterval(1);
        conf.getxAxis().setMinorTickLength(0);
        conf.getxAxis().setTickLength(0);
        conf.getyAxis().getTitle().setText(null);
        /**/
        conf.getLegend().setEnabled(false);
    }

    private void populateMonthlySmsChart(DashboardData data) {
        Configuration conf = yearlySalesGraph.getConfiguration();
        conf.getChart().setType(ChartType.AREASPLINE);
        conf.getChart().setBorderRadius(4);

        conf.setTitle("SMS Últimos Tres Meses");

//        conf.getxAxis().setVisible(true);

        conf.getyAxis().getTitle().setText(null);
        List<Integer> monthToShowList = monthsIn(2);
        String[] ml = new String[]{OMonths.valueOf(monthToShowList.get(0)).getMonthName(),
                OMonths.valueOf(monthToShowList.get(1)).getMonthName(),
                OMonths.valueOf(monthToShowList.get(2)).getMonthName()};
        conf.getxAxis().setCategories(ml);
        List<SmsByYearMonth> monthToShowDataList = smsHourService.getGroupSmsByYearMonthMessageTypeWhMo(actualYear, monthToShowList, stingListGenericBean.getList());

        for (Integer integer : monthToShowList) {
            boolean thisHasIt = false;
            for (SmsByYearMonth smsByYearMonth : monthToShowDataList) {
                /* Si tengo el Month me salgo del ciclo. */
                if (smsByYearMonth.getGroupBy() == integer) {
                    thisHasIt = true;
                    break;
                }
            }
            /* Agregar el mes faltante a la respuesta. */
            if (!thisHasIt) {
//                System.out.println("TRIMESTRE COLUMN DATA - ADDING MONTH({}) WITH 0 ", integer);
                SmsByYearMonth o = new SmsByYearMonth(0, actualYear, integer, "N/A");
                monthToShowDataList.add(o);
            }
        }
        /**/
        OrderToMonthSeries(monthToShowDataList);
        /**/
        List<DataSeries> list_series1 = findDataSeriesLineBase(monthToShowDataList);
        for (DataSeries list_sery : list_series1) {
            conf.addSeries(list_sery);
        }

        Tooltip tooltip = new Tooltip();
        tooltip.setValueDecimals(0);
        tooltip.setHeaderFormat("<span style=\"font-size: 10px\">{point.x}</span><br/>");
        tooltip.setShared(true);
        conf.setTooltip(tooltip);
    }

    /**
     * Agrega los dias sin data con 0.
     *
     * @param dailyList
     * @param typeOfMessage
     * @return una sublista desde 0 hasta le dia actual.
     */
    private List<Number> fillDays(final List<SmsByYearMonthDay> dailyList, String typeOfMessage) {
        List<Number> numberList = new ArrayList<>(31 + dailyList.size());
        /* Llenar e iniciaizar los dias hasta hoy en cero. */
        for (int fakeDay = 0; fakeDay < 31; fakeDay++) {
            numberList.add(0l);
        }
        /* Sustituir en la lista los dias con valores. Solos los tipo 'typeOfMessage' */
        for (SmsByYearMonthDay smsByYearMonthDay : dailyList) {
            /*Valida que no mostrara data mayor al dia de hoy.*/
            if (smsByYearMonthDay.getDaySms() > 31) {
                continue;
            }
            if (typeOfMessage.equals(smsByYearMonthDay.getSomeCode())) {
                System.out.println(smsByYearMonthDay.getSomeCode() + " - DAY: " + smsByYearMonthDay.getDaySms() + " TOTAL: " + smsByYearMonthDay.getTotal());
                numberList.add(smsByYearMonthDay.getDaySms() - 1, smsByYearMonthDay.getTotal());
            }
        }
        /* Limitar hasta el dia de hoy. */
        numberList = numberList.subList(0, 31);
//        System.out.println("{} Day List of {} day[{}-{}]", getStringLog(), typeOfMessage, 1, actual_day);
        return numberList;
    }

    /**
     * Crea una Lista con valor 0, con los dias que no tienen data.
     *
     * @param HourList
     * @param type
     * @return
     */
    private List<Number> fillHouList(final List<SmsByYearMonthDayHour> HourList, String type) {
        List<Number> numberList = new ArrayList<>(23 + 1);
        /* Llenar e iniciaizar los dias hasta hoy en cero. */
        for (int fakeDay = 0; fakeDay < 23 + 1; fakeDay++) {
//            System.out.println("{} FakeHour {}-{}", getStringLog(), fakeDay, actual_hour);
            numberList.add(0l);
        }
        /**/
        for (SmsByYearMonthDayHour smsByYearMonthDayHour : HourList) {
            if (type.equals(smsByYearMonthDayHour.getSomeCode())) {
//                System.out.println("{} Setting {}", getStringLog(), smsByYearMonthDayHour.getHourSms());
                numberList.add(smsByYearMonthDayHour.getHourSms(), smsByYearMonthDayHour.getTotal());
            }
        }
//        System.out.println("{} Hour List of {}-{}", getStringLog(), type, numberList);
        numberList = numberList.subList(0, 23 + 1);
//        System.out.println("{} Hour Sub List of {}-{}", getStringLog(), type, numberList);
        return numberList;
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

    public List<DataSeries> findDataSeriesLineBase(List<? extends SmsByYearMonth> l) {

        Map<Integer, Map<String, Long>> data_monthly = new HashMap<>();
        List<Integer> monthList = new ArrayList<>(4);

        Map<String, Long> mapMx = new HashMap<>();
        log.info("findDataSeriesLineBase size [{}]", l.size());
        for (SmsByYearMonth smsByYearMonth : l) {
            log.info("findDataSeriesLineBase {}", smsByYearMonth);

            /* Si no esta el Month/day/Hour agregarlos systemid seleccionados en 0l */
            if (!data_monthly.containsKey(smsByYearMonth.getGroupBy())) {
                monthList.add(smsByYearMonth.getGroupBy());
                mapMx = new HashMap<>();

                /* Agragar al map cada uno de los systemid selecionados con 0. */
                for (OMessageType osystemid : OMessageType.values()) {
                    log.info("INIC IN 0l systemid {} - {}", osystemid, mapMx);
                    mapMx.put(osystemid.name(), 0l);
                }
                /* Agregar al Month/day/Hour el map con los 0l*/
                log.info("INIC IN 0l Month/Day/Hour {} - {}", smsByYearMonth.getGroupBy(), mapMx);
                data_monthly.put(smsByYearMonth.getGroupBy(), mapMx);
            }
            /* TODO: Hacer que del repositorio solo vengan las operadoras */
            if (!mapMx.containsKey(smsByYearMonth.getSomeCode())) {
                log.info("SOMECODE NOT FOUND: [{}] IN {}", smsByYearMonth.getSomeCode(), mapMx);
                continue;
            }
            /* Como se agregan meses a mano puede venir en vacio el systemid */
            if (smsByYearMonth.getSomeCode() == null || "".equals(smsByYearMonth.getSomeCode())) {
                log.info("MONTH AUTO GENERATED WITH AL SYSYEMID WITH 0l", smsByYearMonth.getGroupBy());
                continue;
            }
            log.info("SETTING REAL VALUE: SOMECODE[{}] VALUE[{}]", smsByYearMonth.getSomeCode(), smsByYearMonth.getTotal());
            /* Agrego en el Month/day/Hour en valor real */
            mapMx = data_monthly.get(smsByYearMonth.getGroupBy());
            mapMx.put(smsByYearMonth.getSomeCode(), smsByYearMonth.getTotal());
        }

        /*Llenar los Meses con number*/
        Map<String, List<Number>> list = new HashMap<>();
        for (Integer integer : monthList) {
            mapMx = data_monthly.get(integer);
            System.out.println("Month/Day/Hour: " + integer + " " + mapMx);

            /* Agragar al map los carrier selecionados. */
            for (OMessageType ocarrier : OMessageType.values()) {
                if (!list.containsKey(ocarrier.name())) {
                    list.put(ocarrier.name(), new ArrayList<>());
                }
                System.out.println("list.get " + ocarrier.name() + " " + mapMx.get(ocarrier.name()));
                list.get(ocarrier.name()).add(mapMx.get(ocarrier.name()));
            }
        }

        List<Number> list_total = new ArrayList<>();
        list.put("TOTAL", list_total);
        long total = 0;
        for (Integer integer : monthList) {
            mapMx = data_monthly.get(integer);
            for (OMessageType ocarrier : OMessageType.values()) {
                total += mapMx.get(ocarrier.name());
            }
            list_total.add(total);
            total = 0;
        }

        List<DataSeries> list_series = new ArrayList<>();
        PlotOptionsSpline splinePlotOptions = new PlotOptionsSpline();
        for (OMessageType ocarrier : OMessageType.values()) {
            DataSeries series = new DataSeries();
//            series.setPlotOptions(splinePlotOptions);
            series.setName(ocarrier.name());
            System.out.println("list.get(ocarrier.getSystemId()) " + ocarrier.name() + "-" + list.get(ocarrier.name()));
            List<Number> ln = list.get(ocarrier.name());
            if (ln == null) {
                ln = new LinkedList<>();
            }
            series.setData(list.get(ocarrier.name()).toArray(new Number[ln.size()]));
            list_series.add(series);
//            conf.addSeries(series);
        }
        /* Agregaar el TOTAL */
        DataSeries series = new DataSeries();
//        series.setPlotOptions(splinePlotOptions);
        series.setName("TOTAL");
        series.setData(list_total.toArray(new Number[list_total.size()]));
        list_series.add(series);
        return list_series;
    }

    public List<DataSeries> findDataSeriesColumnsBase(List<? extends SmsByYearMonth> l) {
        Map<Integer, Map<String, Long>> data_monthly = new HashMap<>();
        int m = 0;
        System.out.println("[{}] Colum size [{}] " + l.size());
        if (l.isEmpty()) {
            System.out.println("[{}] No data. Return empty DataSeries" + l.size());
            return new ArrayList<>();
        }
        List<Integer> lmonth = new ArrayList<>(4);

        Map<String, Long> mapMx;

        for (SmsByYearMonth smsByYearMonth : l) {
            System.out.println("columns base-> " + smsByYearMonth);

            /* Si no esta el mes */
            if (!data_monthly.containsKey(smsByYearMonth.getGroupBy())) {
                lmonth.add(smsByYearMonth.getGroupBy());
                mapMx = new HashMap<>();
                /* Agregar al Map e inicializar a 0 los carrier selecionados. */
                for (OMessageType selectedItem : OMessageType.values()) {
                    mapMx.put(selectedItem.name(), 0l);
                }
                data_monthly.put(smsByYearMonth.getGroupBy(), mapMx);
            }
            mapMx = data_monthly.get(smsByYearMonth.getGroupBy());
            mapMx.put(smsByYearMonth.getSomeCode(), smsByYearMonth.getTotal());
        }

        /*Llenar los Meses con number*/
        Map<String, List<Number>> list = new HashMap<>();
        for (Integer integer : lmonth) {
            mapMx = data_monthly.get(integer);
            System.out.println("Column Month: " + integer + " " + mapMx);
            /* Agragar al map los Message Type selecionados. */
            for (OMessageType omessage_type : OMessageType.values()) {
                if (!list.containsKey(omessage_type.name())) {
                    list.put(omessage_type.name(), new ArrayList<>());
                }
                list.get(omessage_type.name()).add(mapMx.get(omessage_type.name()));
            }
        }
        List<Number> list_total = new ArrayList<>();
        list.put("TOTAL", list_total);
        long total = 0;
        long max_value_y = 0;
        for (Integer integer : lmonth) {
            mapMx = data_monthly.get(integer);
            for (OMessageType omessage_type : OMessageType.values()) {
                total += mapMx.get(omessage_type.name());
            }
            if (total > max_value_y) {
                max_value_y = total;
            }
            list_total.add(total);
            total = 0;
        }

        List<DataSeries> list_series = new ArrayList<>();
        PlotOptionsColumn splinePlotOptions = new PlotOptionsColumn();
        for (OMessageType omessage_type : OMessageType.values()) {
            DataSeries series = new DataSeries();
            series.setPlotOptions(splinePlotOptions);
            series.setName(omessage_type.name());
            series.setData(list.get(omessage_type.name()).toArray(new Number[list.get(omessage_type.name()).size()]));
            list_series.add(series);
//            conf.addSeries(series);
        }
        /* Vaidar si las columnas so mas de una*/
        if (OMessageType.values().length > 1) {
            DataSeries series = new DataSeries();
            series.setPlotOptions(splinePlotOptions);
            series.setName("TOTAL");
            series.setData(list_total.toArray(new Number[list_total.size()]));
            list_series.add(series);
        }
//        conf.getyAxis().setMax(max_value_y * 1.30);
        return list_series;
    }

    private void OrderToMonthSeries(List<SmsByYearMonth> listToOrder) {
        /* Ordenar la lista por YearMonth */
        Map<String, SmsByYearMonth> forTriOrderedMap = new HashMap<>();
        listToOrder.forEach(smsByYearMonth -> {
            forTriOrderedMap.put(smsByYearMonth.forKey(), smsByYearMonth);
        });
        SortedSet<String> trikeys = new TreeSet<>(forTriOrderedMap.keySet());
        System.out.println("TRIMESTRE DATA - BEFORE CLEARING {} " + listToOrder);
        listToOrder.clear();
        trikeys.forEach(key -> {
            listToOrder.add(forTriOrderedMap.get(key));
        });
        System.out.println("TRIMESTRE DATA - FILLED AND ORDERED: {} " + listToOrder);
    }

    private void setActualDate() {
        Calendar c = Calendar.getInstance();
        actualDay = c.get(Calendar.DAY_OF_MONTH);
        actualMonth = c.get(Calendar.MONTH) + 1;
        actualYear = c.get(Calendar.YEAR);
        log.info("{} HOUR OF DAY {} HOUR {}", getStringLog(), c.get(Calendar.HOUR_OF_DAY), c.get(Calendar.HOUR));
        actualHour = c.get(Calendar.HOUR_OF_DAY);
    }

    private String getStringLog() {
        return "[" + currentUser.getUser().getEmail() + "] [" + UI_CODE + "]";
    }
}
