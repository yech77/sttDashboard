package com.stt.dash.ui.views.dashboard.main;

import com.stt.dash.app.OMessageType;
import com.stt.dash.app.OMonths;
import com.stt.dash.app.security.CurrentUser;
import com.stt.dash.app.session.ListGenericBean;
import com.stt.dash.backend.data.AbstractSmsByYearMonth;
import com.stt.dash.backend.data.SmsByYearMonth;
import com.stt.dash.backend.data.SmsByYearMonthDay;
import com.stt.dash.backend.data.SmsByYearMonthDayHour;
import com.stt.dash.backend.data.entity.FIlesToSend;
import com.stt.dash.backend.data.entity.OrderSummary;
import com.stt.dash.backend.service.OrderService;
import com.stt.dash.backend.service.SmsHourService;
import com.stt.dash.ui.DailySmsShowGridView;
import com.stt.dash.ui.MainView;
import com.stt.dash.ui.MonthlySmsShowGridView;
import com.stt.dash.ui.SmsShowGridHourlyView;
import com.stt.dash.ui.SmsShowGridViewV2;
import com.stt.dash.ui.dataproviders.FilesToSendGridDataProvider;
import com.stt.dash.ui.popup.MainDashBoardMonthlyPopUpView;
import com.stt.dash.ui.popup.MainDashBoardTrimestralPopUpView;
import com.stt.dash.ui.popup.MainDashboardDailyPopUpView;
import com.stt.dash.ui.popup.MonthlySmsPopupView;
import com.stt.dash.ui.utils.BakeryConst;
import com.stt.dash.ui.views.bulksms.FileToSendCard;
import com.stt.dash.ui.views.dashboard.DashboardBase;
import com.stt.dash.ui.views.storefront.beans.OrdersCountData;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.Tag;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.charts.Chart;
import com.vaadin.flow.component.charts.events.ChartLoadEvent;
import com.vaadin.flow.component.charts.model.AbstractPlotOptions;
import com.vaadin.flow.component.charts.model.ChartType;
import com.vaadin.flow.component.charts.model.Configuration;
import com.vaadin.flow.component.charts.model.Crosshair;
import com.vaadin.flow.component.charts.model.DataLabels;
import com.vaadin.flow.component.charts.model.DataSeries;
import com.vaadin.flow.component.charts.model.DataSeriesItem;
import com.vaadin.flow.component.charts.model.ListSeries;
import com.vaadin.flow.component.charts.model.PlotOptionsColumn;
import com.vaadin.flow.component.charts.model.PlotOptionsLine;
import com.vaadin.flow.component.charts.model.PlotOptionsPie;
import com.vaadin.flow.component.charts.model.Series;
import com.vaadin.flow.component.charts.model.Stacking;
import com.vaadin.flow.component.charts.model.Tooltip;
import com.vaadin.flow.component.dependency.JsModule;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.polymertemplate.Id;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouteAlias;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Tag("main-view")
@JsModule("./src/views/main/main-view.js")
@Route(value = BakeryConst.PAGE_DASHBOARD_MAIN, layout = MainView.class)
@RouteAlias(value = BakeryConst.PAGE_ROOT, layout = MainView.class)
@PageTitle(BakeryConst.TITLE_DASHBOARD_MAIN)
public class MainDashboardView extends DashboardBase {
    private Logger log = LoggerFactory.getLogger(MainDashboardView.class);
    private final OrderService orderService;
    private final SmsHourService smsHourService;
    private final ListGenericBean<String> stingListGenericBean;
    private final CurrentUser currentUser;
    /* Para Graficos y servicios */
    private List<Integer> monthToShowList;
    /*Fechas */
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
    private Chart smsThisMonthChart;

    @Id("deliveriesThisYear")
    private Chart smsThisDayChart;

    @Id("yearlySalesGraph")
    private Chart smsLastThreeMonthChart;

    @Id("ordersGrid")
    private Grid<FIlesToSend> grid;

    @Id("monthlyProductSplit")
    private Chart monthlyProductSplit;

    @Id("todayCountChart")
    private Chart todayCountChart;

    @Autowired
    public MainDashboardView(OrderService orderService, FilesToSendGridDataProvider gridDataProvider, SmsHourService smsHourService, @Qualifier("getUserSystemIdString") ListGenericBean<String> stringListGenericBean, CurrentUser currentUser) {
        super();
        this.orderService = orderService;
        this.smsHourService = smsHourService;
        this.stingListGenericBean = stringListGenericBean;
        this.currentUser = currentUser;
        /**/
        setActualDate();
        /**/
//        grid.addColumn(OrderCard.getTemplate()
//                .withProperty("orderCard", OrderCard::create)
//                .withProperty("header", order -> null)
//                .withEventHandler("cardClick",
//                        order -> UI.getCurrent().navigate(BakeryConst.PAGE_STOREFRONT + "/" + order.getId())));

        grid.addColumn(FileToSendCard.getTemplate().withProperty("orderCard", FileToSendCard::create).withProperty("header", fileToSend -> null).withEventHandler("cardClick", order -> UI.getCurrent().navigate(BakeryConst.PAGE_BULK_STOREFRONT + "/" + order.getId())));


        grid.setSelectionMode(Grid.SelectionMode.NONE);
        grid.setDataProvider(gridDataProvider);

        populateLastThreeMonthChart();
        populateThisDayChart();
        populateOrdersCounts();
        initProductSplitMonthlyGraph();

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
        smsThisMonthChart.addChartLoadListener(chartLoadListener);
        smsThisDayChart.addChartLoadListener(chartLoadListener);
        smsLastThreeMonthChart.addChartLoadListener(chartLoadListener);
        monthlyProductSplit.addChartLoadListener(chartLoadListener);
    }

    private void initProductSplitMonthlyGraph() {
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
        conf.setTitle("Distribución operadora este mes");
        List<SmsByYearMonth> groupList = smsHourService.groupCarrierByYeMoMeWhMoEqMessageTypeIn(actualYear, actualMonth, Arrays.asList("MT", "MO"), stingListGenericBean.getList());
        groupList.stream().forEach(System.out::println);
        /* Agrupar por Carrier */
        Map<String, List<SmsByYearMonth>> gbc = groupList.stream().collect(Collectors.groupingBy(SmsByYearMonth::getSomeCode));
        System.out.println("**************** ");
        DataSeries series = new DataSeries();
        gbc.entrySet().forEach(carrier -> {
            long totalCarrier = carrier.getValue().stream().mapToLong(p -> p.getTotal()).sum();
            innerSeries.add(new DataSeriesItem(carrier.getKey(), totalCarrier));

            carrier.getValue().forEach(smsMonthList -> {
                series.add(new DataSeriesItem(smsMonthList.getMessageType(), smsMonthList.getTotal()));

            });
        });
//        DataSeries deliveriesPerProductSeries = new DataSeries(productDeliveries.entrySet().stream()
//                .map(e -> new DataSeriesItem(e.getKey().getName(), e.getValue())).collect(Collectors.toList()));
        DataLabels dataLabels = new DataLabels();
        dataLabels.setEnabled(true);
        dataLabels.setFormatter("'<b>'+ this.point.name +'</b>: '+ this.percentage +' %'");
        PlotOptionsPie plotOptionsPie = new PlotOptionsPie();
        plotOptionsPie.setInnerSize("80%");
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
        innerSeries.setName("sms");
        series.setName("sms");
        conf.setSeries(innerSeries, series);
        conf.setTooltip(tooltip);
    }

    private void populateOrdersCounts() {
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

        OrdersCountData smsCountDataWithChart = new OrdersCountData("Mensajes Eviados (MT)", "", (int) t_mt);
        mtCounterLabel.setOrdersCountData(smsCountDataWithChart);

        smsCountDataWithChart = new OrdersCountData("Mensajes Recibidos (MO)", "", (int) t_mo);
        moCounterLabel.setOrdersCountData(smsCountDataWithChart);

        smsCountDataWithChart = new OrdersCountData("Total", "", (int) (t_mo + t_mt));
        totalCounterLabel.setOrdersCountData(smsCountDataWithChart);
    }

    private void populateThisDayChart() {
        smsThisDayChart.addPointClickListener(click -> {
            int seriesItemIndex = click.getItemIndex();
            Dialog d = new Dialog();
            d.setWidth("75%");
            Button closeButton = new Button("Cerrar");
            closeButton.addClickListener(c -> {
                d.close();
            });
            MainDashboardDailyPopUpView view = new MainDashboardDailyPopUpView(smsHourService, actualYear, actualMonth, actualDay, seriesItemIndex, stingListGenericBean.getList());
            view.setTitles("Gráfico: Enviados Hoy", LocalDate.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
            d.add(view);
            d.open();
            view.setConsumer((s) -> d.close());
        });
        smsThisDayChart.addChartClickListener(click -> {
            Dialog d = new Dialog();
            d.setWidth("75%");
            MainDashboardDailyPopUpView view = new MainDashboardDailyPopUpView(smsHourService, actualYear, actualMonth, actualDay, stingListGenericBean.getList());
            view.setTitles("Gráfico: Enviados Hoy", LocalDate.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
            d.add(view);
            d.open();
            view.setConsumer((s) -> d.close());
        });

        // init the 'Deliveries in [this year]' chart
        /**/
        List<SmsByYearMonthDayHour> smsHourList = smsHourService.groupSmsYeMoDaHoTyWhYeMoDaSyIn(actualYear, actualMonth, actualDay, stingListGenericBean.getList());
        /**/
        List<Number> mtHourCompletedWithCeroList = fillHouList(smsHourList, "MT");
        List<Number> moHourCompletedWithCeroList = fillHouList(smsHourList, "MO");
        /**/
        ListSeries mtHourListSeries = new ListSeries("MT");
        ListSeries moHourListSeries = new ListSeries("MO");
        /**/
        mtHourListSeries.setData(mtHourCompletedWithCeroList);
        moHourListSeries.setData(moHourCompletedWithCeroList);
        /**/
        Tooltip tooltip = new Tooltip();
        tooltip.setValueDecimals(0);
        tooltip.setShared(true);
        /**/
        Configuration thisDayChartConf = smsThisDayChart.getConfiguration();
        configureColumnChart(thisDayChartConf);
        thisDayChartConf.setTitle("Enviados hoy");
        thisDayChartConf.setExporting(true);
        thisDayChartConf.getxAxis().setCategories(MILITARY_HOURS);
        thisDayChartConf.setSeries(mtHourListSeries, moHourListSeries);
        thisDayChartConf.setTooltip(tooltip);
        /**/
        // init the 'Deliveries in [this month]' chart
        smsThisMonthChart.addPointClickListener(click -> {
            /* El dia comienza en 1. */
            int seriesItemIndex = click.getItemIndex() + 1;
            MainDashBoardMonthlyPopUpView view = new MainDashBoardMonthlyPopUpView(smsHourService, actualYear, actualMonth, seriesItemIndex, stingListGenericBean.getList());
            popup(view);
        });
        smsThisMonthChart.addChartClickListener(click -> {
            MainDashBoardMonthlyPopUpView view = new MainDashBoardMonthlyPopUpView(smsHourService, actualYear, actualMonth, stingListGenericBean.getList());
            popup(view);
        });
        Configuration monthConf = smsThisMonthChart.getConfiguration();
        configureColumnChart(monthConf);
        /**/
        List<SmsByYearMonthDay> smsDailyList = smsHourService.groupSmsByYeMoDaTyWhYeMoSyIn(actualYear, actualMonth, stingListGenericBean.getList());
        System.out.println("DEVUELTOS: " + smsDailyList.size());
        List<Number> mt = fillDays(smsDailyList, "MT");
        List<Number> mo = fillDays(smsDailyList, "MO");
//        DataProvider<SmsByYearMonthDay, ?> dataProvider = new ListDataProvider<>(smsDailyList);

        /**/
//        List<Number> deliveriesThisMonth = data.getDeliveriesThisMonth();

        String[] deliveriesThisMonthCategories = IntStream.rangeClosed(1, mt.size()).mapToObj(String::valueOf).toArray(String[]::new);
//        DataProviderSeries<SmsByYearMonthDay> series = new DataProviderSeries<>(dataProvider, SmsByYearMonthDay::getTotal);
        monthConf.setTitle("Mensajes este mes");
        monthConf.setExporting(true);
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

    private void popup(MainDashBoardMonthlyPopUpView view) {
        Dialog d = new Dialog();
        d.setWidth("75%");
        d.add(view);
        d.open();
        view.setConsumer((s) -> d.close());
    }

    private void popup(MainDashBoardTrimestralPopUpView view) {
        Dialog d = new Dialog();
        d.setWidth("75%");
        d.add(view);
        d.open();
        view.setConsumer((s) -> d.close());
    }

    /**
     *
     */
    private void populateLastThreeMonthChart() {
        smsLastThreeMonthChart.addPointClickListener(click -> {
            List<Integer> integers = monthsIn(2);
            Integer month = integers.get(click.getItemIndex());
            MainDashBoardTrimestralPopUpView view = new MainDashBoardTrimestralPopUpView(smsHourService, actualYear, month, stingListGenericBean.getList());
            popup(view);
        });
        smsLastThreeMonthChart.addChartClickListener(click -> {
            MainDashBoardTrimestralPopUpView view = new MainDashBoardTrimestralPopUpView(smsHourService, actualYear, monthsIn(2), stingListGenericBean.getList());
            popup(view);
        });
        Configuration conf = smsLastThreeMonthChart.getConfiguration();
        conf.getChart().setType(ChartType.AREASPLINE);
        conf.getChart().setBorderRadius(4);
        conf.setTitle("Enviados en los Últimos tres Meses");
        conf.setExporting(true);
        Tooltip tooltip = new Tooltip();
        tooltip.setValueDecimals(0);
        tooltip.setHeaderFormat("<span style=\"font-size: 10px\">{point.x}</span><br/>");
        tooltip.setShared(true);
        conf.setTooltip(tooltip);
//        conf.getxAxis().setVisible(true);

        conf.getyAxis().getTitle().setText(null);
        List<Integer> monthToShowList = monthsIn(2);
        String[] ml = new String[]{OMonths.valueOf(monthToShowList.get(0)).getMonthName(), OMonths.valueOf(monthToShowList.get(1)).getMonthName(), OMonths.valueOf(monthToShowList.get(2)).getMonthName()};
        conf.getxAxis().setCategories(ml);
        List<? extends AbstractSmsByYearMonth> monthToShowDataList = smsHourService.groupSmsMessageTypeByYeMoWhYeMoInSyIn(actualYear, monthToShowList, stingListGenericBean.getList());
        List<Series> lineDateSeriesList = messageTypeAndMonthlyTotal(Arrays.asList(OMessageType.values()), monthToShowDataList, monthsIn(2));
        PlotOptionsLine plotLine = new PlotOptionsLine();
        addToChart(conf, lineDateSeriesList, plotLine);
    }

    private void addToChart(Configuration configuration, List<Series> seriesList, AbstractPlotOptions plot) {
        if (seriesList == null || seriesList.size() == 0) {
            log.info("{} NO DATA FOR CARRIER CHART LINE");
        } else {
            for (int i = 0; i < seriesList.size(); i++) {
                Series series = seriesList.get(i);
                series.setPlotOptions(plot);
                configuration.addSeries(series);
            }
        }
    }

    /**
     * Agrega los dias sin data con 0.
     *
     * @param smsList
     * @param typeOfMessage
     * @return una sublista desde 0 hasta le dia actual.
     */
    private List<Number> fillDays(final List<? extends AbstractSmsByYearMonth> smsList, String typeOfMessage) {
        List<Number> numberList = new ArrayList<>(actualMaxDayOfMonth);
        /* Llenar e iniciaizar los dias hasta hoy en cero. */
        for (int fakeDay = 0; fakeDay < actualMaxDayOfMonth + 1; fakeDay++) {
            numberList.add(0l);
        }
        /* Sustituir en la lista los dias con valores. Solos los tipo 'typeOfMessage' */
        for (AbstractSmsByYearMonth smsByYearMonth : smsList) {
            if (typeOfMessage.equals(smsByYearMonth.getSomeCode())) {
                numberList.add(smsByYearMonth.getGroupBy() - 1, smsByYearMonth.getTotal());
            }
        }
        numberList = numberList.subList(0, actualDay);
        return numberList;
    }

    /**
     * Completa con 0 las horas faltantes en la lista segun el tipo de mensaje.
     *
     * @param smsList
     * @param typeOfMessage
     * @return
     */
    private List<Number> fillHouList(final List<? extends AbstractSmsByYearMonth> smsList, String typeOfMessage) {
        List<Number> numberList = new ArrayList<>(23 + 1);

        /* Llenar e iniciaizar las 24 horas del dia en cero. */
        for (int fakeDay = 0; fakeDay < 23 + 1; fakeDay++) {
            numberList.add(0l);
        }
        /* recorre toda la lista */
        for (AbstractSmsByYearMonth smsByYearMonth : smsList) {
            if (typeOfMessage.equalsIgnoreCase(smsByYearMonth.getSomeCode())) {
                numberList.add(smsByYearMonth.getGroupBy(), smsByYearMonth.getTotal());
            }
        }

        /* Se limita la lista a la hora actual. */
        numberList = numberList.subList(0, actualHour + 1);
        return numberList;
    }
}
