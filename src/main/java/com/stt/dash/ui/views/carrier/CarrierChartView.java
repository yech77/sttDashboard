package com.stt.dash.ui.views.carrier;

import com.stt.dash.app.OMessageType;
import com.stt.dash.app.OMonths;
import com.stt.dash.app.security.CurrentUser;
import com.stt.dash.app.session.ListGenericBean;
import com.stt.dash.backend.data.AbstractSmsByYearMonth;
import com.stt.dash.backend.data.Role;
import com.stt.dash.backend.data.SmsByYearMonth;
import com.stt.dash.backend.data.SmsByYearMonthDay;
import com.stt.dash.backend.data.SmsByYearMonthDayHour;
import com.stt.dash.backend.data.entity.Carrier;
import com.stt.dash.backend.data.entity.Client;
import com.stt.dash.backend.data.entity.SystemId;
import com.stt.dash.backend.data.entity.User;
import com.stt.dash.backend.service.CarrierService;
import com.stt.dash.backend.service.SmsHourService;
import com.stt.dash.ui.MainView;
import com.stt.dash.ui.popup.CarrierDailyPopupView;
import com.stt.dash.ui.popup.CarrierMonthlyPopupView;
import com.stt.dash.ui.popup.CarrierTrimestralPopUpView;
import com.stt.dash.ui.utils.BakeryConst;
import com.stt.dash.ui.utils.I18nUtils;
import com.stt.dash.ui.views.HasNotifications;
import com.stt.dash.ui.views.dashboard.DashboardBase;
import com.vaadin.componentfactory.multiselect.MultiComboBox;
import com.vaadin.flow.component.Tag;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.charts.Chart;
import com.vaadin.flow.component.charts.model.*;
import com.vaadin.flow.component.checkbox.CheckboxGroup;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.dependency.JsModule;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.polymertemplate.Id;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.VaadinSession;
import org.apache.commons.collections4.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.Page;
import org.springframework.security.access.annotation.Secured;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Tag("carrier-chart-view")
@JsModule("./src/views/carrier/carrier-chart-view.js")
@Route(value = BakeryConst.PAGE_CARRIER, layout = MainView.class)
@PageTitle(BakeryConst.TITLE_CARRIER)
@Secured({Role.ADMIN, "UI_EVOLUTION_CARRIER"})
public class CarrierChartView extends DashboardBase implements HasNotifications {
    @Id("deliveriesThisMonth")
    private Chart smsLastThreeMonthChart;

    @Id("smsThisDayChart")
    private Chart smsThisDayChart;

    @Id("carrierDailyChart")
    private Chart smsThisMonthChart;

    @Id("carrierTriPieChart")
    private Chart smsLastMonthsPieChart;
    @Id("carrierMonthlyPieChart")
    private Chart smsThisMonthPieChart;
    @Id("carrierHourlyPieChart")
    private Chart smsHourPieChart;
    @Id("beanComboBox")
    private ComboBox<Client> clientCombobox;
    @Id("beanMultiComboBox")
    private MultiComboBox<Carrier> carrierMultiComboBox;
    @Id("beanCheckboxGroup")
    private CheckboxGroup<OMessageType> checkboxMessageType;
    @Id("filterButton")
    private Button filterButton;
    /**/
    private static String CARRIER_VIEW_SELECTED_CARRIER = "carrier_view_selected_carrier";
    private static String CARRIER_VIEW_SELECTED_MESSAGETYPE = "carrier_view_selected_messageType";
    private static String CARRIER_VIEW_SELECTED_CLIENT = "carrier_view_selected_client";
    /**/
    private static Logger log = LoggerFactory.getLogger(CarrierChartView.class);
    private final SmsHourService smsHourService;
//    private final ListGenericBean<String> userSystemIdList;
    /* CLIENTE */
//    private ComboBox<Client> clientCombobox = new ComboBox<>("Clientes");

    /* OPERADORAS */
//    private MultiComboBox<Carrier> carrierMultiComboBox = new MultiComboBox<>("Operadoras");
    /* Tipo de Mensaje */
//    private final CheckboxGroup<OMessageType> checkboxMessageType = new CheckboxGroup<>();
    /* Para Graficos y servicios */
    private List<Integer> monthToShowList;
    private String[] ml;
    /* Button */
//    private Button filterButton = new Button("Actualizar");
    List<String> clientSystemIdStringList;

    public CarrierChartView(SmsHourService smsHourService,
                            CarrierService carrierService,
                            @Qualifier("getUserSystemIdString")
                            ListGenericBean<String> stringListGenericBean,
                            CurrentUser currentUser) {
        super();
//        this.userSystemIdList = stringListGenericBean;
        this.smsHourService = smsHourService;
        setActualDate();
        /* ******* */
        /* ******* Client */
        clientCombobox.setLabel("Cliente");
        clientCombobox.setItemLabelGenerator(Client::getClientName);
        /* Client & Systemids*/
        if (currentUser.getUser().getUserTypeOrd() == User.OUSER_TYPE_ORDINAL.COMERCIAL) {
            clientCombobox.setItems(currentUser.getUser().getClients());
        } else {
            /* Solo usearios Comerciales tendran Clientes. El resto tendrán Credenciales asignadas. */
            /*TODO: Enviar un mensaje dado que el cliente puede no tener credenciales .*/
            Client client = currentUser.getUser().getSystemids().stream().findFirst().get().getClient();
            clientCombobox.setItems(client);
            clientCombobox.setValue(client);
        }

        if (VaadinSession.getCurrent().getAttribute(CARRIER_VIEW_SELECTED_CLIENT) != null) {
            clientCombobox.setValue((Client) VaadinSession.getCurrent().getAttribute(CARRIER_VIEW_SELECTED_CLIENT));
            VaadinSession.getCurrent().setAttribute(CARRIER_VIEW_SELECTED_CLIENT, null);
        } else {
//            clientCombobox.setValue((Client) VaadinSession.getCurrent().getAttribute(CARRIER_VIEW_SELECTED_CLIENT));
        }
        /* Nombre de los Meses */
        monthToShowList = monthsIn(2);
        ml = new String[]{OMonths.valueOf(monthToShowList.get(0)).getMonthName(),
                OMonths.valueOf(monthToShowList.get(1)).getMonthName(),
                OMonths.valueOf(monthToShowList.get(2)).getMonthName()};
        /* Message type */
        checkboxMessageType.setItems(new HashSet<>(Arrays.asList(OMessageType.values())));
        if (VaadinSession.getCurrent().getAttribute(CARRIER_VIEW_SELECTED_MESSAGETYPE) != null) {
            checkboxMessageType.setValue((Set<OMessageType>) VaadinSession.getCurrent().getAttribute(CARRIER_VIEW_SELECTED_MESSAGETYPE));
            VaadinSession.getCurrent().setAttribute(CARRIER_VIEW_SELECTED_MESSAGETYPE, null);
        } else {
            checkboxMessageType.setValue(new HashSet<>(Arrays.asList(OMessageType.values())));
        }
        checkboxMessageType.setItemLabelGenerator(OMessageType::name);
        /* Carrier */
        Page<Carrier> carrierPage = carrierService.findAll();
        carrierMultiComboBox.setI18n(I18nUtils.getMulticomboI18n());
        carrierMultiComboBox.setLabel("Operadora");
        carrierMultiComboBox.setItems(carrierPage.getContent());
        carrierMultiComboBox.setItemLabelGenerator(Carrier::getCarrierCharcode);
        if (VaadinSession.getCurrent().getAttribute(CARRIER_VIEW_SELECTED_CARRIER) != null) {
            carrierMultiComboBox.setValue((Set<Carrier>) VaadinSession.getCurrent().getAttribute(CARRIER_VIEW_SELECTED_CARRIER));
            VaadinSession.getCurrent().setAttribute(CARRIER_VIEW_SELECTED_CARRIER, null);
        } else {
            carrierMultiComboBox.setValue(new HashSet<>(carrierPage.getContent()));
        }
        /* ******* */
        /* HEADER */
//        divHeader.add(new HorizontalLayout(clientCombobox, checkboxMessageType),
//                new HorizontalLayout(carrierMultiComboBox), filterButton);
        clientCombobox.setWidthFull();
        checkboxMessageType.setWidthFull();
        carrierMultiComboBox.setWidthFull();
        checkboxMessageType.setErrorMessage("seleccionar al menos un tipo de sms");

        /* ******* Message type */
        checkboxMessageType.setLabel("Tipo de Mensajes");
        checkboxMessageType.setItems(OMessageType.values());
        checkboxMessageType.setValue(new HashSet<OMessageType>(Arrays.asList(OMessageType.values())));
        checkboxMessageType.setRequired(true);

        try {
            if (currentUser.getUser().getUserTypeOrd() == User.OUSER_TYPE_ORDINAL.COMERCIAL) {
                clientSystemIdStringList = clientCombobox.getValue().getSystemids()
                        .stream()
                        .map(SystemId::getSystemId)
                        .collect(Collectors.toList());
            } else {
                clientSystemIdStringList = currentUser.getUser().getSystemids()
                        .stream()
                        .map(SystemId::getSystemId)
                        .collect(Collectors.toList());
            }
        } catch (Exception e) {
            clientSystemIdStringList = new ArrayList<>();
        }
        /*Actualiza trimestra al entrar en la pantalla. */
        updateTrimestral(clientSystemIdStringList);
        updateMonthlyChart(clientSystemIdStringList);
        updateHourlyChart(clientSystemIdStringList);
        filterButton.addClickListener(click -> {
            filterButton.setEnabled(false);
            VaadinSession.getCurrent().setAttribute(CARRIER_VIEW_SELECTED_CARRIER, carrierMultiComboBox.getValue());
            VaadinSession.getCurrent().setAttribute(CARRIER_VIEW_SELECTED_MESSAGETYPE, checkboxMessageType.getSelectedItems());
            VaadinSession.getCurrent().setAttribute(CARRIER_VIEW_SELECTED_CLIENT, clientCombobox.getValue());
            UI.getCurrent().getPage().reload();
        });
        filterButton.setEnabled(isValidSearch());

        clientCombobox.addValueChangeListener(blur -> {
            filterButton.setEnabled(isValidSearch());
        });

        carrierMultiComboBox.addValueChangeListener(blur -> {
            filterButton.setEnabled(isValidSearch());
        });

        checkboxMessageType.addValueChangeListener(value -> {
            filterButton.setEnabled(isValidSearch());
        });
    }

    /**
     * Por Hora
     *
     * @param sids
     */
    private void updateHourlyChart(List<String> sids) {
        Configuration confHourlyChart = smsThisDayChart.getConfiguration();

        smsThisDayChart.addPointClickListener(listener -> {
            int seriesItemIndex = listener.getItemIndex();
            Dialog d = new Dialog();
            d.setWidth("75%");
            /* Convertir Set<SystemId> seleccionados en un List<String>*/
            List<String> selectedCarrierList = carrierMultiComboBox.getValue().stream().map(Carrier::getCarrierCharcode).collect(Collectors.toList());
            List<String> selectedMessageTypeList = checkboxMessageType.getValue().stream().map(OMessageType::name).collect(Collectors.toList());
            CarrierDailyPopupView view = new CarrierDailyPopupView(smsHourService, actualYear, actualMonth, actualDay, seriesItemIndex, selectedCarrierList, selectedMessageTypeList, clientSystemIdStringList);
            d.add(view);
            d.open();
            view.setConsumer((s) -> d.close());
        });

        smsThisDayChart.addChartClickListener(listener -> {
            Dialog d = new Dialog();
            d.setWidth("75%");
            List<String> selectedCarrierList = carrierMultiComboBox.getValue().stream().map(Carrier::getCarrierCharcode).collect(Collectors.toList());
            List<String> selectedMessageTypeList = checkboxMessageType.getValue().stream().map(OMessageType::name).collect(Collectors.toList());
            CarrierDailyPopupView view = new CarrierDailyPopupView(smsHourService, actualYear, actualMonth, actualDay, selectedCarrierList, selectedMessageTypeList, clientSystemIdStringList);
            d.add(view);
            d.open();
            view.setConsumer((s) -> d.close());
        });


        confHourlyChart.setTitle("Trafico del día");
        confHourlyChart.setSubTitle("Por hora");
        confHourlyChart.setExporting(true);
        confHourlyChart.getyAxis().setTitle("SMS");
        PlotOptionsColumn plotColum = new PlotOptionsColumn();
        /**/
        confHourlyChart.getxAxis().setTitle("Hora");
        /**/
        String[] da = new String[LocalDateTime.now().getHour() + 1];
        for (int i = 0; i <= LocalDateTime.now().getHour(); i++) {
            da[i] = i + ":";
        }
        confHourlyChart.getxAxis().setCategories(da);
        /**/
        Tooltip tooltip = new Tooltip();
        tooltip.setValueDecimals(0);
        tooltip.setShared(true);
        tooltip.setHeaderFormat("<span style=\"font-size: 10px\">Hora: {point.key}</span><br/>");
        confHourlyChart.setTooltip(tooltip);
        /**/
        if (carrierMultiComboBox.getValue() == null) {
            showNotificationInformation("Por favor seleccione una Operadora");
            return;
        }
        /* Convertir Set<Carrier> seleccionados en un List<String>*/
        List<String> carrier_list = carrierMultiComboBox.getValue().stream().map(Carrier::getCarrierCharcode).collect(Collectors.toList());
        /* Column Chart*/
        List<SmsByYearMonthDayHour> smsByYearMonthDayHourList = smsHourService.groupSmsYeMoDaHoTyWhYeMoDaSyIn(actualYear, actualMonth, actualDay, sids);
        List<Series> LineDateSeriesList = messageTypeAndMonthlyTotal(checkboxMessageType.getSelectedItems(), smsByYearMonthDayHourList, hourList);
        addToChart(confHourlyChart, LineDateSeriesList, plotColum);
        /* Line Chart */
        smsByYearMonthDayHourList = smsHourService.groupSmsCarrierAndMessageTypeByYeMoDaHoWhYeMoDaSyIn_CarrierTyIn(actualYear,
                actualMonth,
                actualDay,
                carrier_list,
                checkboxMessageType.getSelectedItems(), sids);
        List<SmsByYearMonthDayHour> l2 = new ArrayList<>(smsByYearMonthDayHourList);
        PlotOptionsLine plotLine = new PlotOptionsLine();
        LineDateSeriesList = paEntenderLine(smsByYearMonthDayHourList, hourList);
        addToChart(confHourlyChart, LineDateSeriesList, plotLine);
        /* DAY PIE*/
        populateHourPieChart(l2);
    }

    private void addToChart(Configuration configuration, List<Series> LineDateSeriesList, AbstractPlotOptions plot) {
        if (LineDateSeriesList == null || LineDateSeriesList.size() == 0) {
            log.info("NO DATA FOR CARRIER CHART LINE");
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

    public List<Series> paEntenderLine(List<? extends AbstractSmsByYearMonth> smsList,
                                       List<Integer> integerList) {
        if (CollectionUtils.isEmpty(smsList)) {
            return Collections.emptyList();
        }

        List<Series> dataSeriesList = new ArrayList<>();
        /*TODO nullpointer*/
        /* Recorre los Carrier seleccionados. */
        carrierMultiComboBox.getValue().forEach(actualCarrierInForeach -> {
            ListSeries series = new ListSeries();
            series.setName(actualCarrierInForeach.getCarrierCharcode());
            /* Recorre los meses del trimestre */
            integerList.forEach(actualMonthInForeach -> {
                /* Total por Month y Carrier*/
                Long tot = smsList.stream()
                        .filter(sms -> sms.getGroupBy() == actualMonthInForeach
                                && actualCarrierInForeach.getCarrierCharcode().equalsIgnoreCase(sms.getSomeCode()))
                        .mapToLong(sms -> sms.getTotal())
                        .sum();
                series.addData(tot);
            });
            dataSeriesList.add(series);
        });
        return dataSeriesList;
    }

    private void updateMonthlyChart(List<String> sids) {
        List<String> carrier_list = carrierMultiComboBox.getValue().stream().map(Carrier::getCarrierCharcode).collect(Collectors.toList());
        /* --------------DIARIO */
        List<SmsByYearMonthDay> smsByDayList = smsHourService.groupSmsByYeMoDaTyWhYeMoSyInFillingNoDataDay(actualYear, actualMonth, sids);
        List<SmsByYearMonthDay> smsByCarrierAndTypeList = smsHourService.groupSmsCarrierMessageTypeByYeMoDaWhYeMoSyIn_CarrierTyIn(actualYear,
                actualMonth,
                carrier_list,
                checkboxMessageType.getSelectedItems(),
                sids);
        populateMonthChart(smsByDayList, new ArrayList<>(smsByCarrierAndTypeList));
        /* PIE */
        populateMonthlyPieChart(smsByCarrierAndTypeList);
    }

    private void updateTrimestral(List<String> sids) {
        /* ------------- TRIMESTRAL: SMS
         * Ejem: SmsByYearMonth{total=2775, yearSms=2021, monthSms=3, someCode=MO}*/
        List<SmsByYearMonth> smsGroup = smsHourService.groupSmsMessageTypeByYeMoWhYeMoInSyIn(actualYear, monthsIn(2), sids);

        List<SmsByYearMonth> carrierGroup = smsHourService.groupSmsCarrierAndMessageTypeByYeMoWhYeMoSyIn_CarrierInTyIn(actualYear,
                monthsIn(2),
                carrierMultiComboBox.getValue(),
                checkboxMessageType.getSelectedItems(),
                sids);
        populateTriChart(smsGroup, new ArrayList<>(carrierGroup));
        /* PIE */
        populatePieTriChart(carrierGroup);
    }

    private void populateHourPieChart(List<? extends SmsByYearMonth> carrierHourGroup) {
        Configuration confOut = smsHourPieChart.getConfiguration();
        populatePieHelper(carrierHourGroup, confOut, "Hoy");
    }

    private void populateMonthlyPieChart(List<? extends SmsByYearMonth> carrierDayGroup) {
        Configuration confOut = smsThisMonthPieChart.getConfiguration();
        populatePieHelper(carrierDayGroup, confOut, OMonths.valueOf(actualMonth).getMonthName());
    }

    private void populatePieHelper(List<? extends SmsByYearMonth> smsList, Configuration confOut, String chartTitle) {
        confOut.setTitle(chartTitle);
        if (CollectionUtils.isEmpty(smsList)) {
            return;
        }
        /**/
        Tooltip tooltip = new Tooltip();
        tooltip.setValueDecimals(0);
        tooltip.setHeaderFormat("<span style=\"font-size: 10px\">{point.key} {point.percentage:%02.2f}%</span><br/>");
        confOut.setTooltip(tooltip);
        /**/
        PlotOptionsPie innerPieOptions = new PlotOptionsPie();
        innerPieOptions.setSize("70%");
        List<DataSeries> list_series = findDataSeriesPieBase(smsList,
                "Total",
                innerPieOptions);
        for (DataSeries list_sery : list_series) {
            confOut.addSeries(list_sery);
        }
    }


    /**
     * @param smsGroup     Agrupado por Year-Month-MessageType
     * @param carrierGroup Data agrupada por Year-Month-Carrier-MessageType
     */
    private void populateTriChart(List<? extends AbstractSmsByYearMonth> smsGroup, List<SmsByYearMonth> carrierGroup) {
        Configuration confTriChart = smsLastThreeMonthChart.getConfiguration();

        Configuration confTriMixChart = smsLastThreeMonthChart.getConfiguration();
        smsLastThreeMonthChart.addPointClickListener(click -> {
            Dialog d = new Dialog();
            d.setWidth("75%");
            List<Integer> month = monthsIn(2);
            Integer selectedMonth = month.get(click.getItemIndex());
            CarrierTrimestralPopUpView view = new CarrierTrimestralPopUpView(
                    smsHourService,
                    actualYear,
                    selectedMonth,
                    clientSystemIdStringList,
                    checkboxMessageType.getSelectedItems(),
                    carrierMultiComboBox.getValue());
            d.add(view);
            d.open();
            view.setConsumer((s) -> d.close());
        });
        smsLastThreeMonthChart.addChartClickListener(click -> {
            Dialog d = new Dialog();
            d.setWidth("75%");
            CarrierTrimestralPopUpView view = new CarrierTrimestralPopUpView(
                    smsHourService,
                    actualYear,
                    monthsIn(2),
                    clientSystemIdStringList,
                    checkboxMessageType.getSelectedItems(),
                    carrierMultiComboBox.getValue());
            d.add(view);
            d.open();
            view.setConsumer((s) -> d.close());
        });
        /* Column */
        Tooltip tooltip = new Tooltip();
        tooltip.setShared(true);
        tooltip.setValueDecimals(0);
        tooltip.setHeaderFormat("<span style=\"font-size: 10px\">{point.key} {point.percentage:%02.2f}%</span><br/>");
        confTriChart.setTitle("Trafico a tres meses");
        confTriChart.setExporting(true);
        confTriChart.getExporting().setFilename("carrier-trafico-a-tres-meses");
        confTriChart.setTooltip(tooltip);
        /* Averiguar cuales son los tres meses a calular. */
        confTriChart.getxAxis().setCategories(ml);
        PlotOptionsColumn plotColum = new PlotOptionsColumn();
        List<Series> LineDateSeriesList = messageTypeAndMonthlyTotal(checkboxMessageType.getSelectedItems(), smsGroup, monthToShowList);
        addToChart(confTriChart, LineDateSeriesList, plotColum);
        /**/
        PlotOptionsLine plotLine = new PlotOptionsLine();
        LineDateSeriesList = paEntenderLine(carrierGroup, monthToShowList);
        addToChart(confTriChart, LineDateSeriesList, plotLine);
    }

    private void populatePieTriChart(List<SmsByYearMonth> smsByYearMonth) {
        Configuration conf = smsLastMonthsPieChart.getConfiguration();
        conf.setTitle("Trafico a tres meses");
        if (CollectionUtils.isEmpty(smsByYearMonth)) {
            return;
        }
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
        conf.setTitle("Trafico a tres meses");
    }

    /**
     * Grafico operadora Mensual
     *
     * @param smsByDayList
     * @param smsByCarrierAndTypeList
     */
    private void populateMonthChart(List<? extends AbstractSmsByYearMonth> smsByDayList, List<SmsByYearMonthDay> smsByCarrierAndTypeList) {
        Configuration confThisMonth = smsThisMonthChart.getConfiguration();
        /**/
        smsThisMonthChart.addChartClickListener(click -> {
            Dialog d = new Dialog();
            d.setWidth("75%");
            List<String> selectedCarrierList = carrierMultiComboBox.getValue().stream().map(Carrier::getCarrierCharcode).collect(Collectors.toList());
            List<String> selectedMessageTypeList = checkboxMessageType.getValue().stream().map(OMessageType::name).collect(Collectors.toList());
            CarrierMonthlyPopupView view = new CarrierMonthlyPopupView(smsHourService, actualYear, actualMonth, selectedCarrierList, selectedMessageTypeList, clientSystemIdStringList);
            view.setTitles("Gráfico - Tráfico del mes", clientCombobox.getValue() == null ? "Cliente" : clientCombobox.getValue().getClientCod());
            d.add(view);
            d.open();
            view.setConsumer((s) -> d.close());
        });

        smsThisMonthChart.addPointClickListener(click -> {
            /* El dia comienza en 1. */
            int seriesItemIndex = click.getItemIndex() + 1;
            Dialog d = new Dialog();
            d.setWidth("75%");
            List<String> selectedCarrierList = carrierMultiComboBox.getValue().stream().map(Carrier::getCarrierCharcode).collect(Collectors.toList());
            List<String> selectedMessageTypeList = checkboxMessageType.getValue().stream().map(OMessageType::name).collect(Collectors.toList());
            CarrierMonthlyPopupView view = new CarrierMonthlyPopupView(smsHourService, actualYear, actualMonth, seriesItemIndex, selectedCarrierList, selectedMessageTypeList, clientSystemIdStringList);
            view.setTitles("Gráfico - Tráfico del mes", clientCombobox.getValue() == null ? "Cliente" : clientCombobox.getValue().getClientCod());
            d.add(view);
            d.open();
            view.setConsumer((s) -> d.close());
        });
        /**/
        confThisMonth.getyAxis().setTitle("SMS");
        confThisMonth.setSubTitle("Por día");
        confThisMonth.setExporting(true);
        confThisMonth.setTitle("Tráfico del mes");
        /* OMonths.valueOf(actualMonth).getMonthName() + " - " + actualYear */
        String[] da = new String[LocalDate.now().getMonth().maxLength()];
        for (int i = 1; i <= LocalDate.now().getMonth().maxLength(); i++) {
            da[i - 1] = i + "";
        }
        confThisMonth.getxAxis().setCategories(da);
        /**/
        /**/
        Tooltip tooltip = new Tooltip();
        tooltip.setValueDecimals(0);
        tooltip.setShared(true);
        tooltip.setHeaderFormat("<span style=\"font-size: 10px\">{point.key} {point.percentage:%02.2f}%</span><br/>");
        confThisMonth.setTooltip(tooltip);
        /**/
        configureColumnChart(confThisMonth);
        confThisMonth.getChart().setType(ChartType.AREA);
        /*crear un List de Integer de nombre daysofMonth, hasta el dia de la fecha actual*/
        List<Integer> daysofMonth = new ArrayList<>();
        for (int i = 1; i <= LocalDate.now().getDayOfMonth(); i++) {
            daysofMonth.add(i);
        }

        List<? extends AbstractSmsByYearMonth> l0 = smsByDayList;
        log.info("MONTH CHART COLUMN: {}", l0);
        l0 = orderGroup(smsByDayList);
        /**/
        List<DataSeries> list_series1 = findDataSeriesColumnsBase(l0);
        for (DataSeries list_sery : list_series1) {
            confThisMonth.addSeries(list_sery);
        }
//        DataProvider<SmsByYearMonth, ?> dataProvider = new ListDataProvider<>(smsGroup);
//
//        DataProviderSeries<SmsByYearMonth> series = new DataProviderSeries<>(dataProvider, SmsByYearMonth::getTotal);
//        confIn.addSeries(series);

        List<? extends AbstractSmsByYearMonth> l = smsByCarrierAndTypeList;
        log.info("MONTH CHART: {}", l);
        /* Averiguar cuales son los tres meses a calular. */
        List<Integer> monthList = monthToShowList;
        /* TODO: revisar no trae los MO*/
//        l = orderGroup(fillWithCero(l, monthToShowList));
        l = orderGroup(smsByCarrierAndTypeList);
        List<DataSeries> list_series2 = findDataSeriesLineBase(l);
        if (list_series2 == null || list_series2.size() == 0) {
            log.info("{} NO DATA FOR CARRIER CHART LINE", getStringLog());
        } else {
            for (int i = 0; i < list_series2.size() - 1; i++) {
                confThisMonth.addSeries(list_series2.get(i));
            }
        }
    }


    private boolean isValidSearch() {
        return clientCombobox.getValue() != null &&
                !carrierMultiComboBox.getValue().isEmpty() &&
                !checkboxMessageType.isInvalid();
    }

    /**
     * Agrega la columna TOTAL
     *
     * @param smsGroupList
     * @return
     */
    public List<DataSeries> findDataSeriesColumnsBase(List<? extends AbstractSmsByYearMonth> smsGroupList) {

        if (CollectionUtils.isEmpty(smsGroupList)) {
            log.info("No data to Show");
            return Collections.emptyList();
        }

        Map<Integer, Map<String, Long>> monthlyDataToShowMap = new HashMap<>();
        int m = 0;
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
            System.out.println("Column Day: " + integer + " " + messageTypeToShowMap);
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

//    public List<Series> paEntender(List<? extends AbstractSmsByYearMonth> l, List<Integer> integerList) {
//
//        List<Series> dataSeriesList = new ArrayList<>();
//        /*TODO nullpointer*/
//        /* Recorre los Carrier seleccionados. */
//        PlotOptionsColumn splinePlotOptions = new PlotOptionsColumn();
//        checkboxMessageType.getSelectedItems().forEach(messageType -> {
//            ListSeries series = new ListSeries();
//            series.setName(messageType.name());
//            /* Recorre los meses del trimestre */
//            integerList.forEach(month -> {
//                /* Total por Month y Carrier*/
//                Long tot = l.stream()
//                        .filter(sms -> sms.getGroupBy() == month
//                                && messageType.name().equalsIgnoreCase(sms.getSomeCode()))
//                        .mapToLong(sms -> sms.getTotal()).sum();
//                System.out.println("MONTH-> " + month + ". Message Type: " + messageType + " - TOTAL: " + tot);
//                series.addData(tot);
//                series.setPlotOptions(splinePlotOptions);
//            });
//            dataSeriesList.add(series);
//        });
////        ListSeries digitelSerie = new ListSeries("DIGITel", 100,200,300);
////        ListSeries movilnetSerie = new ListSeries("MOVILnet", 200,300,400);
////        ListSeries movistarSerie = new ListSeries("MOVistar", 300,400,500);
////        List<ListSeries> dataSeriesList = new ArrayList<>();
////        dataSeriesList.add(digitelSerie);
////        dataSeriesList.add(movilnetSerie);
////        dataSeriesList.add(movistarSerie);
//        /* FORMA 2 */
////        List<DataSeries> dataSeriesList = new ArrayList<>();
////        DataSeries series = new DataSeries();
////        series.setName("DIGITEL");
////        series.setData(1427, 11383, 0);
////        dataSeriesList.add(series);
////        series = new DataSeries();
////        series.setName("MOVILNET");
////        series.setData(2710, 23030, 0);
////        dataSeriesList.add(series);
////        series = new DataSeries();
////        series.setName("MOVISTAR");
////        series.setData(2795, 22520, 0);
////        dataSeriesList.add(series);
//        return dataSeriesList;
//    }


    public List<DataSeries> findDataSeriesLineBase(List<? extends AbstractSmsByYearMonth> smsList) {
        if (CollectionUtils.isEmpty(smsList)) {
            log.info("No data to Show");
            return Collections.emptyList();
        }
        Map<Integer, Map<String, Long>> data_monthly = new HashMap<>();
        List<Integer> lmonth = new ArrayList<>(4);
        /* TODO: Parece ser aca que el total es mal calculado y solo suma los MT*/
        Map<String, Long> carrierTotalMap = new HashMap<>();
        /* Agrega 0 a todos los carrier en toda la agrupacion (MONTH/DAY) */
        for (AbstractSmsByYearMonth smsByYearMonth : smsList) {
            /* Si no esta el Month/day/Hour agregarlos carrier seleccionados en 0l */
            if (!data_monthly.containsKey(smsByYearMonth.getGroupBy())) {
                lmonth.add(smsByYearMonth.getGroupBy());
                carrierTotalMap = new HashMap<>();
                /* Agragar al map cada uno de los carrier selecionados con 0. */
                for (Carrier ocarrier : carrierMultiComboBox.getValue()) {
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
            for (Carrier ocarrier : carrierMultiComboBox.getValue()) {
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
            for (Carrier ocarrier : carrierMultiComboBox.getValue()) {
                total += carrierTotalMap.get(ocarrier.getCarrierCharcode());
            }
            list_total.add(total);
            total = 0;
        }

        List<DataSeries> list_series = new ArrayList<>();
        PlotOptionsSpline splinePlotOptions = new PlotOptionsSpline();
        for (Carrier ocarrier : carrierMultiComboBox.getValue()) {
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

    private String getStringLog() {
        return "";
//        return "[" + Application.getAPP_NAME() + "] [" + ouser_session.getUserEmail() + "]"
//                + " [" + UI_CODE + "]";
    }

    private List<AbstractSmsByYearMonth> fillWithCero(List<? extends AbstractSmsByYearMonth> listToFill, List<Integer> monthList) {
        boolean hasToFill = false;
        List<AbstractSmsByYearMonth> l = new ArrayList<>(listToFill);
        if (listToFill == null || listToFill.size() == 0) {
            log.info("Nothing to Fill");
            return l;
        }
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
                AbstractSmsByYearMonth o = listToFill.get(0).getObject(0, actualYear, monthLoop, "N/A");
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

    private List<DataSeries> findDataSeriesPieBase(List<? extends SmsByYearMonth> smsList,
                                                   String serieName,
                                                   PlotOptionsPie plotOptionsPie) {
        if (CollectionUtils.isEmpty(smsList)) {
            return Collections.emptyList();
        }
        /* Puede venir con data pero con puros ceros porque fue completado con ceros: Ejem Dias.*/
        if (smsList.stream().mapToLong(sms -> sms.getTotal()).sum() == 0) {
            return Collections.emptyList();
        }

        Map<String, Long> mapMx = new HashMap<>();

        /* Agregar al Map e inicializar a 0 los carrier selecionados. */
        carrierMultiComboBox.getValue().forEach(oCarrier -> {
            mapMx.put(oCarrier.getCarrierCharcode(), 0l);
        });
        DataSeries pieSeries = new DataSeries();

        System.out.println("********************//1///**************************");
        smsList.stream().forEach(System.out::println);
        /* Agrupar por Carrier */
        Map<String, List<SmsByYearMonth>> groupByCarrier =
                smsList.stream().collect(Collectors.groupingBy(s -> s.getSomeCode()));
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
                }
        );
        System.out.println("SE TARDO: " + (System.currentTimeMillis() - startTime));
        pieSeries.setPlotOptions(plotOptionsPie);
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
}
