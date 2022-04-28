package com.stt.dash.ui.views.dashboard;

import com.stt.dash.app.OMessageType;
import com.stt.dash.backend.data.AbstractSmsByYearMonth;
import com.vaadin.flow.component.charts.model.ChartType;
import com.vaadin.flow.component.charts.model.Configuration;
import com.vaadin.flow.component.charts.model.ListSeries;
import com.vaadin.flow.component.charts.model.PlotOptionsColumn;
import com.vaadin.flow.component.charts.model.Series;
import com.vaadin.flow.component.polymertemplate.PolymerTemplate;
import com.vaadin.flow.templatemodel.TemplateModel;

import java.time.LocalDate;
import java.time.Month;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public abstract class DashboardBase extends PolymerTemplate<TemplateModel> {

    protected static final String[] MONTH_LABELS = new String[]{"ENERO", "FEBRERO", "MARZO", "ABRIL", "MAYO", "JUNIO", "JULIO", "AGOSTO", "SEPTIEMBRE", "OCTUBRE", "NOVIEMBRE", "DICIEMBRE"};
    public static final String[] MILITARY_HOURS = new String[]{"0:00", "1:00", "2:00", "3:00", "4:00", "5:00", "6:00", "7:00", "8:00", "9:00", "10:00", "11:00", "12:00", "13:00", "14:00", "15:00", "16:00", "17:00", "18:00", "19:00", "20:00", "21:00", "22:00", "23:00"};

    protected int actualYear;
    protected int actualMonth;
    protected int actualDay;
    protected int actualHour;
    protected int actualMaxDayOfMonth;
    protected List<Integer> hourList = new ArrayList<>();
    protected List<Integer> dayList = new ArrayList<>();


    protected void configureColumnChart(Configuration conf) {
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

    /**
     * Devuelve un listado de los meses atras, segun monthback.
     *
     * @param monthback
     * @return
     */
    protected List<Integer> monthsIn(int monthback) {
        List<Integer> lm = new ArrayList<>(monthback);
        Month month = LocalDate.now().getMonth();
        for (int i = monthback; i > 0; i--) {
            lm.add(month.minus(i).getValue());
        }
        lm.add(month.getValue());
        return lm;
    }

    /**
     * Calcula y agrupa los totales del mes por messageType.
     *
     * @param smsList
     * @param monthToShowList
     * @return
     */
    protected List<Series> messageTypeAndMonthlyTotal(Collection<OMessageType> messageTypeSet, List<? extends AbstractSmsByYearMonth> smsList, List<Integer> monthToShowList) {

        if (smsList == null) {
            return new ArrayList<>();
        }

        Map<String, List<Long>> serieToChartMap = new HashMap<>();
        /* Recorre todos los MessageType  */
        messageTypeSet.forEach(actualMessageTypeForeach -> {
            List<Long> totalList = new ArrayList<>();
            /* Recorre los meses a mostrar */
            monthToShowList.forEach(actualMonthInForeach -> {
                /* Recorre toda la lista para Total filtrado por Month y MessageType */
                Long tot = smsList.stream()
                        .filter(sms -> sms.getGroupBy() == actualMonthInForeach && actualMessageTypeForeach.name().equalsIgnoreCase(sms.getSomeCode()))
                        .mapToLong(sms -> sms.getTotal())
                        .sum();
                totalList.add(tot);
            });
            serieToChartMap.put(actualMessageTypeForeach.name(), totalList);
        });

        List<Series> seriesList = convertToSeries(serieToChartMap);


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
        return seriesList;
    }

    protected List<Series> convertToSeries(Map<String, List<Long>> serieToChartMap) {
        PlotOptionsColumn splinePlotOptions = new PlotOptionsColumn();
        List<Series> seriesList = new ArrayList<>();

        serieToChartMap.forEach((seriesName, totalList) -> {
            ListSeries series = new ListSeries();
            series.setName(seriesName);
            series.setPlotOptions(splinePlotOptions);
            totalList.stream().forEach(t -> {
                series.addData(t);
            });
            seriesList.add(series);
        });
        return seriesList;
    }

    /**
     * Crea la lista de dia y hora basado en dia y hora actual.
     */
    public void setActualDate() {
        Calendar c = Calendar.getInstance();
        actualDay = c.get(Calendar.DAY_OF_MONTH);
        actualMonth = c.get(Calendar.MONTH) + 1;
        actualYear = c.get(Calendar.YEAR);
        actualMaxDayOfMonth = LocalDate.now().getMonth().maxLength();
        actualHour = c.get(Calendar.HOUR_OF_DAY);
        for (int i = 0; i <= actualHour; i++) {
            hourList.add(i);
        }
        for (int i = 1; i <= actualDay; i++) {
            dayList.add(i);
        }
    }

}
