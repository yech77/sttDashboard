package com.stt.dash.ui.views.dashboard.main;

import com.stt.dash.backend.data.DashboardData;
import com.stt.dash.backend.data.DeliveryStats;
import com.stt.dash.backend.data.SmsByYearMonthDay;
import com.stt.dash.backend.data.SmsByYearMonthDayHour;
import com.stt.dash.backend.data.entity.Order;
import com.stt.dash.backend.data.entity.OrderSummary;
import com.stt.dash.backend.data.entity.Product;
import com.stt.dash.backend.service.OrderService;
import com.stt.dash.backend.service.SmsHourService;
import com.stt.dash.ui.MainView;
import com.stt.dash.ui.dataproviders.OrdersGridDataProvider;
import com.stt.dash.ui.utils.BakeryConst;
import com.stt.dash.ui.utils.FormattingUtils;
import com.stt.dash.ui.views.dashboard.DashboardCounterLabel;
import com.stt.dash.ui.views.dashboard.DashboardUtils;
import com.stt.dash.ui.views.dashboard.DataSeriesItemWithRadius;
import com.stt.dash.ui.views.storefront.OrderCard;
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
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDate;
import java.time.MonthDay;
import java.time.Year;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Tag("dashboard-view")
@JsModule("./src/views/dashboard/dashboard-view.js")
@Route(value = BakeryConst.PAGE_DASHBOARD_MAIN, layout = MainView.class)
@PageTitle(BakeryConst.TITLE_DASHBOARD_MAIN)
public class MainDashboardView extends PolymerTemplate<TemplateModel> {

    private static final String[] MONTH_LABELS = new String[]{"Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul",
            "Aug", "Sep", "Oct", "Nov", "Dec"};

    private static final String[] MILITARY_HOURS = new String[]{"0:00", "1:00", "2:00", "3:00", "4:00", "5:00", "6:00", "7:00", "8:00", "9:00", "10:00", "11:00", "12:00", "13:00", "14:00", "15:00", "16:00", "17:00", "18:00", "19:00", "20:00", "21:00", "22:00", "23:00"};

    private final OrderService orderService;
    private final SmsHourService smsHourService;

    @Id("todayCount")
    private DashboardCounterLabel todayCount;

    @Id("notAvailableCount")
    private DashboardCounterLabel notAvailableCount;

    @Id("newCount")
    private DashboardCounterLabel newCount;

    @Id("tomorrowCount")
    private DashboardCounterLabel tomorrowCount;

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
    public MainDashboardView(OrderService orderService, OrdersGridDataProvider orderDataProvider, SmsHourService smsHourService) {
        this.orderService = orderService;
        this.smsHourService = smsHourService;

        grid.addColumn(OrderCard.getTemplate()
                .withProperty("orderCard", OrderCard::create)
                .withProperty("header", order -> null)
                .withEventHandler("cardClick",
                        order -> UI.getCurrent().navigate(BakeryConst.PAGE_STOREFRONT + "/" + order.getId())));

        grid.setSelectionMode(Grid.SelectionMode.NONE);
        grid.setDataProvider(orderDataProvider);

        DashboardData data = orderService.getDashboardData(MonthDay.now().getMonthValue(), Year.now().getValue());
        populateYearlySalesChart(data);
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

        Configuration conf = monthlyProductSplit.getConfiguration();
        conf.getChart().setType(ChartType.PIE);
        conf.getChart().setBorderRadius(4);
        conf.setTitle("Products delivered in " + FormattingUtils.getFullMonthName(today));
        DataSeries deliveriesPerProductSeries = new DataSeries(productDeliveries.entrySet().stream()
                .map(e -> new DataSeriesItem(e.getKey().getName(), e.getValue())).collect(Collectors.toList()));
        PlotOptionsPie plotOptionsPie = new PlotOptionsPie();
        plotOptionsPie.setInnerSize("60%");
        plotOptionsPie.getDataLabels().setCrop(false);
        deliveriesPerProductSeries.setPlotOptions(plotOptionsPie);
        conf.addSeries(deliveriesPerProductSeries);
    }

    private void populateOrdersCounts(DeliveryStats deliveryStats) {
        List<OrderSummary> orders = orderService.findAnyMatchingStartingToday();

        OrdersCountDataWithChart todaysOrdersCountData = DashboardUtils
                .getTodaysOrdersCountData(deliveryStats, orders.iterator());
        todayCount.setOrdersCountData(todaysOrdersCountData);
        initTodayCountSolidgaugeChart(todaysOrdersCountData);
        notAvailableCount.setOrdersCountData(DashboardUtils.getNotAvailableOrdersCountData(deliveryStats));
        Order lastOrder = orderService.load(orders.get(orders.size() - 1).getId());
        newCount.setOrdersCountData(DashboardUtils.getNewOrdersCountData(deliveryStats, lastOrder));
        tomorrowCount.setOrdersCountData(DashboardUtils.getTomorrowOrdersCountData(deliveryStats, orders.iterator()));
    }


    private void initTodayCountSolidgaugeChart(OrdersCountDataWithChart data) {
        Configuration configuration = todayCountChart.getConfiguration();
        configuration.getChart().setType(ChartType.SOLIDGAUGE);
        configuration.setTitle("");
        configuration.getTooltip().setEnabled(false);

        configuration.getyAxis().setMin(0);
        configuration.getyAxis().setMax(data.getOverall());
        configuration.getyAxis().getLabels().setEnabled(false);

        PlotOptionsSolidgauge opt = new PlotOptionsSolidgauge();
        opt.getDataLabels().setEnabled(false);
        configuration.setPlotOptions(opt);

        DataSeriesItemWithRadius point = new DataSeriesItemWithRadius();
        point.setY(data.getCount());
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
        List<SmsByYearMonthDayHour> smsByHour = smsHourService.getGroupSmsByYearMonthDayHourMessageType(2021, 5, 9, Arrays.asList("C0001", "C000102"));

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
        List<SmsByYearMonthDay> groupSmsByYearMonthDayMessageType = smsHourService.getGroupSmsByYearMonthDayMessageType(2021, 5, Arrays.asList("C0001", "C000102"));
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
        Tooltip tooltip2 = new Tooltip();
        tooltip2.setValueDecimals(0);
//        tooltip.setHeaderFormat("<span style=\"font-size: 10px\">Día: {point.x}</span><br/>");
        tooltip2.setShared(true);
        monthConf.setSeries(mtListSeries, moListSeries);
        monthConf.setTooltip(tooltip2);
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

    private void populateYearlySalesChart(DashboardData data) {
        Configuration conf = yearlySalesGraph.getConfiguration();
        conf.getChart().setType(ChartType.AREASPLINE);
        conf.getChart().setBorderRadius(4);

        conf.setTitle("Sales last years");

        conf.getxAxis().setVisible(false);
        conf.getxAxis().setCategories(MONTH_LABELS);

        conf.getyAxis().getTitle().setText(null);

        int year = Year.now().getValue();
        for (int i = 0; i < 3; i++) {
            conf.addSeries(new ListSeries(Integer.toString(year - i), data.getSalesPerMonth(i)));
        }
    }

    /**
     * Agrega los dias sin data con 0.
     *
     * @param dailyList
     * @param type
     * @return una sublista desde 0 hasta le dia actual.
     */
    private List<Number> fillDays(final List<SmsByYearMonthDay> dailyList, String type) {
        List<Number> numberList = new ArrayList<>(31 + dailyList.size());
        /* Llenar e iniciaizar los dias hasta hoy en cero. */
        for (int fakeDay = 0; fakeDay < 31; fakeDay++) {
            numberList.add(0l);
        }
        /* Sustituir en la lista los dias con valores. Solos los tipo 'type' */
        for (SmsByYearMonthDay smsByYearMonthDay : dailyList) {
            /*Valida que no mostrara data mayor al dia de hoy.*/
            if (smsByYearMonthDay.getDaySms() > 31) {
                continue;
            }
            if (type.equals(smsByYearMonthDay.getSomeCode())) {
                System.out.println(smsByYearMonthDay.getSomeCode() + " - DAY: " + smsByYearMonthDay.getDaySms() + " TOTAL: " + smsByYearMonthDay.getTotal());
                numberList.add(smsByYearMonthDay.getDaySms() - 1, smsByYearMonthDay.getTotal());
            }
        }
        /* Limitar hasta el dia de hoy. */
        numberList = numberList.subList(0, 31);
//        log.info("{} Day List of {} day[{}-{}]", getStringLog(), type, 1, actual_day);
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
//            log.info("{} FakeHour {}-{}", getStringLog(), fakeDay, actual_hour);
            numberList.add(0l);
        }
        /**/
        for (SmsByYearMonthDayHour smsByYearMonthDayHour : HourList) {
            if (type.equals(smsByYearMonthDayHour.getSomeCode())) {
//                log.info("{} Setting {}", getStringLog(), smsByYearMonthDayHour.getHourSms());
                numberList.add(smsByYearMonthDayHour.getHourSms(), smsByYearMonthDayHour.getTotal());
            }
        }
//        log.info("{} Hour List of {}-{}", getStringLog(), type, numberList);
        numberList = numberList.subList(0, 23 + 1);
//        log.info("{} Hour Sub List of {}-{}", getStringLog(), type, numberList);
        return numberList;
    }
}
