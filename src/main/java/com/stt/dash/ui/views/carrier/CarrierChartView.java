package com.stt.dash.ui.views.carrier;

import com.stt.dash.app.OMessageType;
import com.stt.dash.app.security.CurrentUser;
import com.stt.dash.app.session.ListGenericBean;
import com.stt.dash.backend.data.SmsByYearMonth;
import com.stt.dash.backend.data.SmsByYearMonthDay;
import com.stt.dash.backend.data.entity.Carrier;
import com.stt.dash.backend.service.CarrierService;
import com.stt.dash.backend.service.SmsHourService;
import com.stt.dash.ui.MainView;
import com.stt.dash.ui.utils.BakeryConst;
import com.vaadin.flow.component.Tag;
import com.vaadin.flow.component.charts.Chart;
import com.vaadin.flow.component.charts.model.*;
import com.vaadin.flow.component.dependency.JsModule;
import com.vaadin.flow.component.polymertemplate.Id;
import com.vaadin.flow.component.polymertemplate.PolymerTemplate;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.templatemodel.TemplateModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.Page;
import org.vaadin.gatanaso.MultiselectComboBox;

import java.util.*;

@Tag("carrier-chart-view")
@JsModule("./src/views/carrier/carrier-chart-view.js")
@Route(value = BakeryConst.PAGE_CARRIER, layout = MainView.class)
@PageTitle(BakeryConst.TITLE_CARRIER)
public class CarrierChartView extends PolymerTemplate<TemplateModel> {

    @Id("deliveriesThisMonth")
    private Chart carrierTriMixChart;

    @Id("carrierTriLineChart")
    private Chart carrierTriLineChart;

    @Id("carrierTriPieChart")
    private Chart carrierTriPieChart;

    //    @Id("deliveriesThisYear")
//    private Chart deliveriesThisYearChart;
//
//    @Id("yearlySalesGraph")
//    private Chart yearlySalesGraph;
//
//    @Id("ordersGrid")
//    private Grid<Order> grid;
//
//    @Id("monthlyProductSplit")
//    private Chart monthlyProductSplit;
    /**/
    Logger log = LoggerFactory.getLogger(CarrierChartView.class);
    private final SmsHourService smsHourService;
    private final ListGenericBean<String> stingListGenericBean;
    private final CurrentUser currentUser;
    /* OPERADORAS */
    private MultiselectComboBox<Carrier> multi_carrier = new MultiselectComboBox<>("Operadoras");

    public CarrierChartView(SmsHourService smsHourService,
                            CarrierService carrierService,
                            @Qualifier("getUserSystemIdString")
                                    ListGenericBean<String> stringListGenericBean,
                            CurrentUser currentUser) {

        this.currentUser = currentUser;
        this.stingListGenericBean = stringListGenericBean;
        this.smsHourService = smsHourService;
        Page<Carrier> carrierPage = carrierService.findAll();
        multi_carrier.setItems(carrierPage.getContent());
        multi_carrier.setValue(new HashSet<>(carrierPage.getContent()));
        /* TRIMESTRAL: SMS
         * Ejem: SmsByYearMonth{total=2775, yearSms=2021, monthSms=3, someCode=MO}*/
        List<SmsByYearMonth> smsGroup = smsHourService.getGroupSmsByYearMonthMessageTypeWhMo(2021, Arrays.asList(3, 4, 5), stringListGenericBean.getSet());
        List<SmsByYearMonth> carrierGroup = smsHourService.getGroupCarrierByYeMoWhMoInMessageTypeIn(2021, Arrays.asList(3, 4, 5), Arrays.asList("MT", "MO"), stringListGenericBean.getSet());
        populateMonthlyChart(smsGroup, carrierGroup);

    }

    private void populateMonthlyChart(List<SmsByYearMonth> smsGroup, List<SmsByYearMonth> carrierGroup) {
        Configuration confIn = carrierTriMixChart.getConfiguration();
        Configuration confOutLine = carrierTriLineChart.getConfiguration();
        /**/
        configureColumnChart(confIn);
        /* Averiguar cuales son los tres meses a calular. */
        List<Integer> monthToShowList = Arrays.asList(3, 4, 5);
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

        List<SmsByYearMonth> l = carrierGroup;
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
            confOutLine.addSeries(list_series2.get(list_series2.size() - 1));
        }
    }

    private void populateDailyChart(SmsByYearMonthDay smsByYearMonthDay) {

    }

    private void populateHourChart(SmsByYearMonthDay smsByYearMonthDay) {

    }

    /**
     * Agrega la columna TOTAL
     *
     * @param smsGroupList
     * @return
     */
    public List<DataSeries> findDataSeriesColumnsBase(List<? extends SmsByYearMonth> smsGroupList) {
        Map<Integer, Map<String, Long>> monthlyDataToShowMap = new HashMap<>();
        int m = 0;
        System.out.println("CarrierEvolution: Columns size() " + smsGroupList.size());
        List<Integer> monthToShowList = new ArrayList<>(4);

        Map<String, Long> messageTypeToShowMap;
        /* Recorre todos los meses para agregar e inicializar
         los MessageType seleccionados en 0.*/
        for (SmsByYearMonth smsByYearMonth : smsGroupList) {
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
            /* Sustituyo ne el map el valor real del MessageType  */
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

    public List<DataSeries> findDataSeriesLineBase(List<? extends SmsByYearMonth> l) {
        log.info("{} [{}]", getStringLog(), l);
        Map<Integer, Map<String, Long>> data_monthly = new HashMap<>();
        List<Integer> lmonth = new ArrayList<>(4);

        Map<String, Long> mapMx = new HashMap<>();
        for (SmsByYearMonth smsByYearMonth : l) {
            System.out.println("findDataSeriesLineBase : " + smsByYearMonth);

            /* Si no esta el Month/day/Hour agregarlos carrier seleccionados en 0l */
            if (!data_monthly.containsKey(smsByYearMonth.getGroupBy())) {
                lmonth.add(smsByYearMonth.getGroupBy());
                mapMx = new HashMap<>();

                /* Agragar al map cada uno de los carrier selecionados con 0. */
                for (Carrier ocarrier : multi_carrier.getSelectedItems()) {
                    mapMx.put(ocarrier.getCarrierCharcode(), 0l);
                }
                /* Agregar al Month/day/Hour el map con los 0l*/
                System.out.println("data_monthly.put " + smsByYearMonth.getGroupBy() + " " + mapMx);
                data_monthly.put(smsByYearMonth.getGroupBy(), mapMx);
            }
            /* TODO: Hacer que del repositorio solo vengan las operadoras */
            if (!mapMx.containsKey(smsByYearMonth.getSomeCode())) {
                continue;
            }
            /* Agrego en el Month/day/Hour en valor real */
            mapMx = data_monthly.get(smsByYearMonth.getGroupBy());
            System.out.println("mapMx.put " + smsByYearMonth.getSomeCode() + " " + smsByYearMonth.getTotal());
            mapMx.put(smsByYearMonth.getSomeCode(), smsByYearMonth.getTotal());
        }

        /*Llenar los Meses con number*/
        Map<String, List<Number>> list = new HashMap<>();
        for (Integer integer : lmonth) {
            mapMx = data_monthly.get(integer);
            System.out.println("Month/Day/Hour: " + integer + " " + mapMx);

            /* Agragar al map los carrier selecionados. */
            for (Carrier ocarrier : multi_carrier.getSelectedItems()) {
                if (!list.containsKey(ocarrier.getCarrierCharcode())) {
                    list.put(ocarrier.getCarrierCharcode(), new ArrayList<>());
                }
                System.out.println("list.get " + ocarrier.getCarrierCharcode() + " " + mapMx.get(ocarrier.getCarrierCharcode()));
                list.get(ocarrier.getCarrierCharcode()).add(mapMx.get(ocarrier.getCarrierCharcode()));
            }
        }

        List<Number> list_total = new ArrayList<>();
        list.put("TOTAL", list_total);
        long total = 0;
        for (Integer integer : lmonth) {
            mapMx = data_monthly.get(integer);
            for (Carrier ocarrier : multi_carrier.getSelectedItems()) {
                total += mapMx.get(ocarrier.getCarrierCharcode());
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

    private List<SmsByYearMonth> fillWithCero(List<SmsByYearMonth> listToFill, List<Integer> monthList) {
        boolean hasToFill = false;
        for (Integer monthLoop : monthList) {
            hasToFill = true;
            for (SmsByYearMonth sms : listToFill) {
                if (sms.getGroupBy() == monthLoop) {
                    hasToFill = false;
                    break;
                }
            }
            if (hasToFill) {
                log.info("TRIMESTRE COLUMN DATA - ADDING MONTH({}) WITH 0 ", monthLoop);
                SmsByYearMonth o = new SmsByYearMonth(0, 2021, monthLoop, "N/A");
                listToFill.add(o);
            }
        }
        return listToFill;
    }

    private List<SmsByYearMonth> orderGroup(List<SmsByYearMonth> listToFill) {
        /* Ordenar la lista por YearMonth */
        Map<String, SmsByYearMonth> forOrderedMap = new HashMap<>();
        listToFill.forEach(smsByYearMonth -> {
            forOrderedMap.put(smsByYearMonth.forKey(), smsByYearMonth);
        });
        SortedSet<String> keys = new TreeSet<>(forOrderedMap.keySet());
        log.info("TRIMESTRE LINE DATA - BEFORE CLEARING {}", listToFill);
        listToFill.clear();
        keys.forEach(key -> {
            listToFill.add(forOrderedMap.get(key));
        });
        return listToFill;
    }
}
