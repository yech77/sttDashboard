package com.stt.dash.ui.views.client;

import com.stt.dash.app.OMessageType;
import com.stt.dash.app.OMonths;
import com.stt.dash.app.security.CurrentUser;
import com.stt.dash.app.session.ListGenericBean;
import com.stt.dash.backend.data.*;
import com.stt.dash.backend.data.entity.Carrier;
import com.stt.dash.backend.data.entity.Client;
import com.stt.dash.backend.data.entity.SystemId;
import com.stt.dash.backend.data.entity.User;
import com.stt.dash.backend.service.AbstractSmsService;
import com.stt.dash.backend.service.CarrierService;
import com.stt.dash.backend.service.SmsHourService;
import com.stt.dash.ui.MainView;
import com.stt.dash.ui.popup.ClientDailyPopupView;
import com.stt.dash.ui.popup.ClientMonthlyPopupView;
import com.stt.dash.ui.popup.ClientTrimestralPopUpView;
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
import org.apache.commons.lang3.ObjectUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.access.annotation.Secured;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Tag("carrier-chart-view")
@JsModule("./src/views/carrier/carrier-chart-view.js")
@Route(value = BakeryConst.PAGE_CLIENT, layout = MainView.class)
@PageTitle(BakeryConst.TITLE_CLIENT)
@Secured({Role.ADMIN, "UI_EVOLUTION_CLIENT"})
public class ClientChartView extends DashboardBase implements HasNotifications {
    /**/
    private static Logger log = LoggerFactory.getLogger(ClientChartView.class);

    private static final String CLIENT_VIEW_SELECTED_SYSTEMID = "client_view_selected_systemid";
    private static final String CLIENT_VIEW_SELECTED_MESSAGETYPE = "client_view_selected_messageType";
    private static final String CLIENT_VIEW_SELECTED_CLIENT = "client_view_selected_client";
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
    private MultiComboBox<SystemId> systemIdMultiCombo;
    @Id("beanCheckboxGroup")
    private CheckboxGroup<OMessageType> checkboxMessageType;
    @Id("filterButton")
    private Button searchButton;
    private final SmsHourService smsHourService;
    private final AbstractSmsService abstractSmsService;
    private final ListGenericBean<String> allUserStringSystemId;
    private final CurrentUser currentUser;
    /* Para Graficos y servicios */
    private List<Integer> monthToShowList;
    private String[] ml;
    private List<Carrier> carrierList;
    private List<String> slist;

    public ClientChartView(AbstractSmsService abstractSmsService,
                           SmsHourService smsHourService,
                           CarrierService carrierService,
                           @Qualifier("getUserSystemIdString") ListGenericBean<String> allUserStringSystemId,
                           CurrentUser currentUser) {
        super();
        this.abstractSmsService = abstractSmsService;
        this.smsHourService = smsHourService;
        this.allUserStringSystemId = allUserStringSystemId;
        this.currentUser = currentUser;
        setActualDate();
        this.carrierList = carrierService.findAll().toList();
        /* Nombre de los Meses */
        monthToShowList = monthsIn(2);
        ml = new String[]{OMonths.valueOf(monthToShowList.get(0)).getMonthName(),
                OMonths.valueOf(monthToShowList.get(1)).getMonthName(),
                OMonths.valueOf(monthToShowList.get(2)).getMonthName()};
        /* Listener */
        addValueChangeListener();
        /* ******* Message type */
        checkboxMessageType.setLabel("Tipo de mensajes");
        checkboxMessageType.setItems(OMessageType.values());
        checkboxMessageType.setValue(new HashSet<>(Arrays.asList(OMessageType.values())));
        checkboxMessageType.setRequired(true);
        checkboxMessageType.setErrorMessage("seleccionar al menos un tipo de sms");
        Optional<Object> op = getCurrentSessionAttributeAndNullIt(CLIENT_VIEW_SELECTED_MESSAGETYPE);
        if (op.isPresent()) {
            checkboxMessageType.setValue((Set<OMessageType>) op.get());
        } else {
            checkboxMessageType.setValue(new HashSet<>(Arrays.asList(OMessageType.values())));
        }
        checkboxMessageType.addValueChangeListener(change -> {
            if (checkboxMessageType.getValue().size() == 0) {
                checkboxMessageType.setInvalid(true);
            } else {
                checkboxMessageType.setInvalid(false);
            }
//            searchButton.setEnabled(isValidSearch());
        });
        /* ******* */
        /* ******* SystemId */
        systemIdMultiCombo.setI18n(I18nUtils.getMulticomboI18n());
        systemIdMultiCombo.setLabel("Credenciales");
        systemIdMultiCombo.setItemLabelGenerator(SystemId::getSystemId);
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
        op = getCurrentSessionAttributeAndNullIt(CLIENT_VIEW_SELECTED_CLIENT);
        op.ifPresent(o -> clientCombobox.setValue((Client) o));
        /* ******* */
        /* HEADER */
        clientCombobox.setWidthFull();
        checkboxMessageType.setWidthFull();
        systemIdMultiCombo.setWidthFull();
        updateCharts();
        searchButton.setEnabled(isValidSearch());
    }

    /**
     * Devuelve el valor  del atributo de la session actual y coloca el atributo en la sesion como null;
     *
     * @param attributeName
     * @return
     */
    private Optional<Object> getCurrentSessionAttributeAndNullIt(String attributeName) {
        Object o = VaadinSession.getCurrent().getAttribute(attributeName);
        VaadinSession.getCurrent().setAttribute(attributeName, null);
        return Optional.ofNullable(o);
    }

    private void addValueChangeListener() {
        clientCombobox.addValueChangeListener(clientListener -> {
            if (ObjectUtils.isEmpty(clientListener.getValue())) {
                systemIdMultiCombo.clear();
                return;
            }
            if (currentUser.getUser().getUserTypeOrd() == User.OUSER_TYPE_ORDINAL.COMERCIAL) {
                /* Solo los comerciales tienen todos los systemids del cliente */
                systemIdMultiCombo.setItems(clientListener.getValue().getSystemids());
            } else {
                systemIdMultiCombo.setItems(currentUser.getUser().getSystemids());
            }
            Optional<Object> op = getCurrentSessionAttributeAndNullIt(CLIENT_VIEW_SELECTED_SYSTEMID);
            if (op.isPresent()) {
                systemIdMultiCombo.setValue((Set<SystemId>) op.get());
                return;
            }
        });

        clientCombobox.addValueChangeListener(value -> searchButton.setEnabled(isValidSearch()));

        systemIdMultiCombo.addValueChangeListener(blur -> {
            searchButton.setEnabled(isValidSearch());
            /* Convertir Set<SystemId> seleccionados en un List<String>*/
            slist = systemIdMultiCombo.getValue().stream().map(SystemId::getSystemId).collect(Collectors.toList());
        });

        checkboxMessageType.addValueChangeListener(value -> {
            searchButton.setEnabled(isValidSearch());
        });

        searchButton.addClickListener(clickEvent -> {
            searchButton.setEnabled(false);
            keepParametersInSession();
            UI.getCurrent().getPage().reload();
        });
    }

    private void keepParametersInSession() {
        VaadinSession.getCurrent().setAttribute(CLIENT_VIEW_SELECTED_CLIENT, clientCombobox.getValue());
        VaadinSession.getCurrent().setAttribute(CLIENT_VIEW_SELECTED_SYSTEMID, systemIdMultiCombo.getValue());
        VaadinSession.getCurrent().setAttribute(CLIENT_VIEW_SELECTED_MESSAGETYPE, checkboxMessageType.getSelectedItems());
    }

    private void updateCharts() {
        updateTriMixChart();
        updateMonthlyLineChart();
        updateHourlyChart();
        /**/
        updateTrimestrePie();
        updateMonthlyPie();
        updateDailyPie();
    }

    private boolean isValidSearch() {
        return clientCombobox.getValue() != null &&
                !systemIdMultiCombo.getValue().isEmpty() &&
                !checkboxMessageType.isInvalid();
    }

    private void updateDailyPie() {
        Configuration confHourlyChart = smsHourPieChart.getConfiguration();
        PlotOptionsPie innerPieOptions = new PlotOptionsPie();
        confHourlyChart.setTitle("Hoy");
        confHourlyChart.setSubTitle("Por operadoras");
        Tooltip tooltip = new Tooltip();
        tooltip.setValueDecimals(0);
        tooltip.setHeaderFormat("<span style=\"font-size: 10px\">{point.key} {point.percentage:%02.2f}%</span><br/>");
        confHourlyChart.setTooltip(tooltip);
        /* Column Chart*/
        List<SmsByYearMonthDayHour> l = smsHourService.getGroupSystemIdByYeMoDaHoCaWhYeMoDayEqMessageTypeIn(LocalDate.now().getYear(), actualMonth, actualDay, checkboxMessageType.getSelectedItems(), slist);
        List<DataSeries> lineDateSeriesList = paEntenderPie(l, Arrays.asList(actualDay));
        addToPieChart(confHourlyChart, lineDateSeriesList, innerPieOptions);
    }

    private void updateMonthlyPie() {
        Configuration confMonthlyChart = smsThisMonthPieChart.getConfiguration();
        PlotOptionsPie innerPieOptions = new PlotOptionsPie();

        confMonthlyChart.setTitle("Este Mes");
        confMonthlyChart.setSubTitle("Por operadoras");
        Tooltip tooltip = new Tooltip();
        tooltip.setValueDecimals(0);
        tooltip.setHeaderFormat("<span style=\"font-size: 10px\">{point.key} {point.percentage:%02.2f}%</span><br/>");
        confMonthlyChart.setTooltip(tooltip);
        /* Column Chart*/
        List<SmsByYearMonth> l = smsHourService.getGroupSystemIdByYeMoCaWhMoInMessageTypeIn(LocalDate.now().getYear(), Arrays.asList(actualMonth), checkboxMessageType.getSelectedItems(), slist);
        List<DataSeries> lineDateSeriesList = paEntenderPie(l, Arrays.asList(actualMonth));
        addToPieChart(confMonthlyChart, lineDateSeriesList, innerPieOptions);
    }

    private void updateTrimestrePie() {
        Configuration confHourlyChart = smsLastMonthsPieChart.getConfiguration();
        PlotOptionsPie innerPieOptions = new PlotOptionsPie();
//        innerPieOptions.setSize("70%")
//
        confHourlyChart.setTitle("Trimestral");
        confHourlyChart.setSubTitle("Por operadoras");
        Tooltip tooltip = new Tooltip();
        tooltip.setValueDecimals(0);
        tooltip.setHeaderFormat("<span style=\"font-size: 10px\">{point.key} {point.percentage:%02.2f}%</span><br/>");
        confHourlyChart.setTooltip(tooltip);
        /* Column Chart*/
        List<SmsByYearMonth> l = smsHourService.getGroupSystemIdByYeMoCaWhMoInMessageTypeIn(LocalDate.now().getYear(), monthsIn(2), checkboxMessageType.getSelectedItems(), slist);
        List<DataSeries> lineDateSeriesList = paEntenderPie(l, monthsIn(2));
        addToPieChart(confHourlyChart, lineDateSeriesList, innerPieOptions);
    }

    private void updateHourlyChart() {
        Configuration confHourlyChart = smsThisDayChart.getConfiguration();

        smsThisDayChart.addPointClickListener(listener -> {
            int seriesItemIndex = listener.getItemIndex();
            Dialog d = new Dialog();
            d.setWidth("75%");
            ClientDailyPopupView view = new ClientDailyPopupView(smsHourService, actualYear, actualMonth, actualDay, seriesItemIndex, slist);
            d.add(view);
            d.open();
            view.setConsumer(s -> d.close());
        });

        smsThisDayChart.addChartClickListener(listener -> {
            Dialog d = new Dialog();
            d.setWidth("75%");
            ClientDailyPopupView view = new ClientDailyPopupView(smsHourService, actualYear, actualMonth, actualDay, slist);
            d.add(view);
            d.open();
            view.setConsumer(s -> d.close());
        });
        confHourlyChart.setTitle("Trafico del día");
        confHourlyChart.setSubTitle("Por hora");
        confHourlyChart.getyAxis().setTitle("SMS");
        confHourlyChart.setExporting(true);
        confHourlyChart.getExporting().setFilename("client-Trafico-del-día");
        PlotOptionsColumn plotColum = new PlotOptionsColumn();
        /**/
        confHourlyChart.getxAxis().setTitle("Hora");
        /**/
        Tooltip tooltip = new Tooltip();
        tooltip.setValueDecimals(0);
        tooltip.setShared(true);
        tooltip.setHeaderFormat("<span style=\"font-size: 10px\">Hora: {point.key}</span><br/>");
        confHourlyChart.setTooltip(tooltip);
        /**/
        if (systemIdMultiCombo.getValue() == null) {
            showNotificationInformation("Por favor seleccione Credenciales.");
            return;
        }
        /* Column Chart*/
        List<SmsByYearMonthDayHour> smsList = smsHourService.groupSmsYeMoDaHoTyWhYeMoDaSyIn(LocalDate.now().getYear(), actualMonth, actualDay, slist);
        if (smsList == null || smsList.isEmpty()) {
            log.info("Hourly Chart Without data to show");
            return;
        }
        List<Series> lineDateSeriesList = messageTypeAndMonthlyTotal(checkboxMessageType.getSelectedItems(), smsList, hourList);
        addToChart(confHourlyChart, lineDateSeriesList, plotColum);
        /* Line Chart */
        smsList = smsHourService.getGroupSystemIdByYeMoDaHoWhYeMoDayEqMessageTypeIn(LocalDate.now().getYear(), actualMonth, actualDay, checkboxMessageType.getSelectedItems(), slist);
        PlotOptionsLine plotLine = new PlotOptionsLine();
        lineDateSeriesList = systemidAndTimeTotal(systemIdMultiCombo.getValue(), smsList, hourList);
        addToChart(confHourlyChart, lineDateSeriesList, plotLine);
    }

    /**
     *
     */
    private void updateMonthlyLineChart() {
        Configuration confThisMonth = smsThisMonthChart.getConfiguration();
        /**/
        smsThisMonthChart.addPointClickListener(click -> {
            /* El dia comienza en 1. */
            int seriesItemIndex = click.getItemIndex() + 1;
            Dialog d = new Dialog();
            d.setWidth("75%");
            ClientMonthlyPopupView view = new ClientMonthlyPopupView(smsHourService, actualYear, actualMonth, seriesItemIndex, slist, checkboxMessageType.getSelectedItems());
            view.setTitles("Gráfico - Tráfico del mes", clientCombobox.getValue() == null ? "Cliente" : clientCombobox.getValue().getClientCod());
            d.add(view);
            d.open();
            view.setConsumer(s -> d.close());
        });
        smsThisMonthChart.addChartClickListener(click -> {
            /* Convertir Set<SystemId> seleccionados en un List<String>*/
            Dialog d = new Dialog();
            d.setWidth("75%");
            ClientMonthlyPopupView view = new ClientMonthlyPopupView(smsHourService, actualYear, actualMonth, slist, checkboxMessageType.getSelectedItems());
            view.setTitles("Gráfico - Tráfico del mes", clientCombobox.getValue() == null ? "Cliente" : clientCombobox.getValue().getClientCod());
            d.add(view);
            d.open();
            view.setConsumer(s -> d.close());
        });
        /**/
        confThisMonth.getyAxis().setTitle("SMS");
        confThisMonth.setSubTitle("Por día");
        confThisMonth.setExporting(true);
        confThisMonth.getExporting().setFilename("client-Trafico-por-dia");
        confThisMonth.setTitle("Tráfico del mes");
        /* OMonths.valueOf(actualMonth).getMonthName() + " - " + actualYear  */
        String[] da = new String[LocalDate.now().getMonth().maxLength()];
        for (int i = 1; i <= LocalDate.now().getMonth().maxLength(); i++) {
            da[i - 1] = i + "";
        }
        confThisMonth.getxAxis().setCategories(da);
        /**/
        PlotOptionsLine plotColum = new PlotOptionsLine();
        /**/
        Tooltip tooltip = new Tooltip();
        tooltip.setValueDecimals(0);
        tooltip.setShared(true);
        tooltip.setHeaderFormat("<span style=\"font-size: 10px\">Dia: {point.key}</span><br/>");
        confThisMonth.setTooltip(tooltip);
        /**/
        configureColumnChart(confThisMonth);
        /* Column Chart*/
        List<SmsByYearMonthDay> smsByYearMonthDayList = smsHourService.groupSmsByYeMoDaTyWhYeMoSyIn(LocalDate.now().getYear(), actualMonth, slist);
        List<Series> lineDateSeriesList = messageTypeAndMonthlyTotal(checkboxMessageType.getSelectedItems(), smsByYearMonthDayList, dayList);
        addToChart(confThisMonth, lineDateSeriesList, plotColum);
        /* Line Chart */
        smsByYearMonthDayList = smsHourService.groupSmsByYeMoDaSyWhYeMoSyIn_TyIn(LocalDate.now().getYear(), actualMonth, checkboxMessageType.getSelectedItems(), slist);
        PlotOptionsLine plotLine = new PlotOptionsLine();
        lineDateSeriesList = systemidAndTimeTotal(systemIdMultiCombo.getValue(), smsByYearMonthDayList, dayList);
        addToChart(confThisMonth, lineDateSeriesList, plotLine);
    }

    private void updateTriMixChart() {
        Configuration conf = smsLastThreeMonthChart.getConfiguration();
        smsLastThreeMonthChart.addPointClickListener(click -> {
            List<Integer> month = monthsIn(2);
            Integer integer = month.get(click.getItemIndex());
            List<String> messageTypeList = checkboxMessageType.getSelectedItems().stream().map(OMessageType::name).collect(Collectors.toList());
            ClientTrimestralPopUpView view = new ClientTrimestralPopUpView(smsHourService, actualYear, integer, slist, messageTypeList);
            popup(view);
        });
        smsLastThreeMonthChart.addChartClickListener(click -> {
            List<String> messageTypeList = checkboxMessageType.getSelectedItems().stream().map(OMessageType::name).collect(Collectors.toList());
            ClientTrimestralPopUpView view = new ClientTrimestralPopUpView(smsHourService, actualYear, monthsIn(2), slist, messageTypeList);
            popup(view);
        });
        conf.getyAxis().setTitle("SMS");
        conf.setTitle("Trafico a tres meses");
        conf.setExporting(true);
        conf.getExporting().setFilename("client-Trafico-a-tres-meses");
        PlotOptionsColumn plotColum = new PlotOptionsColumn();
        /* Averiguar cuales son los tres meses a calular. */
        /**/
        conf.getxAxis().setCategories(ml);
        Tooltip tooltip = new Tooltip();
        tooltip.setValueDecimals(0);
        tooltip.setShared(true);
        conf.setTooltip(tooltip);

        /* Buscar con todos los systemids del usuario. */
        List<SmsByYearMonth> l = smsHourService.groupSmsMessageTypeByYeMoWhYeMoInSyIn(actualYear, monthsIn(2), slist);
        List<Series> lineDateSeriesList = messageTypeAndMonthlyTotal(checkboxMessageType.getSelectedItems(), l, monthsIn(2));
        addToChart(conf, lineDateSeriesList, plotColum);
        /* LINE CHART */
        l = smsHourService.
                getGroupSystemIdByYeMoWhMoInMessageTypeIn(LocalDate.now().getYear(), monthsIn(2), checkboxMessageType.getSelectedItems(), slist);

        PlotOptionsLine plotLine = new PlotOptionsLine();
        lineDateSeriesList = systemidAndTimeTotal(systemIdMultiCombo.getValue(), l, monthsIn(2));
        addToChart(conf, lineDateSeriesList, plotLine);
    }

    private void addToChart(Configuration configuration, List<Series> lineDateSeriesList, AbstractPlotOptions plot) {
        if (lineDateSeriesList == null || lineDateSeriesList.isEmpty()) {
            log.info("{} NO DATA FOR CARRIER CHART LINE");
        } else {
            for (int i = 0; i < lineDateSeriesList.size(); i++) {
                Series series = lineDateSeriesList.get(i);
                series.setPlotOptions(plot);
                configuration.addSeries(series);
            }
        }
    }

    private void addToPieChart(Configuration configuration, List<DataSeries> lineDateSeriesList, AbstractPlotOptions plot) {
        if (lineDateSeriesList == null || lineDateSeriesList.isEmpty()) {
            log.info("NO DATA FOR CARRIER CHART LINE");
        } else {
            for (int i = 0; i < lineDateSeriesList.size(); i++) {
                System.out.println("ADDING LINE PIE********" + lineDateSeriesList.get(i).getName());
                Series series = lineDateSeriesList.get(i);
//                series.setPlotOptions(plot);
                configuration.addSeries(series);
            }
        }
    }

    public List<DataSeries> paEntenderPie(List<? extends AbstractSmsByYearMonth> smsList, List<Integer> integerList) {
        List<DataSeries> dataSeriesList = new ArrayList<>();
        if (CollectionUtils.isEmpty(smsList)) {
            return dataSeriesList;
        }
        /*TODO nullpointer*/
        /* Recorre los Carrier seleccionados. */
        List<String> carriers =
                carrierList.stream().map(Carrier::getCarrierCharcode).collect(Collectors.toList());
        DataSeries pieSeries = new DataSeries();
        DataSeries donutSeries = new DataSeries();
        carriers.forEach(actualCarrierForeach -> {
            /* Total por Carrier */
            Long carrierTot = smsList.parallelStream()
                    .filter(sms -> actualCarrierForeach.equalsIgnoreCase(sms.getMessageType()))
                    .mapToLong(AbstractSmsByYearMonth::getTotal)
                    .sum();
            pieSeries.add(new DataSeriesItem(actualCarrierForeach, carrierTot), false, false);
            /* Total por S*/
            systemIdMultiCombo.getOptionalValue()
                    .ifPresent(systemIdSet ->
                            systemIdSet.forEach(actualSystemIdForeach -> {
                                Long totSid = smsList.parallelStream()
                                        .filter(sms -> sms.getMessageType().equalsIgnoreCase(actualCarrierForeach)
                                                && actualSystemIdForeach.getSystemId().equalsIgnoreCase(sms.getSomeCode()))
                                        .mapToLong(AbstractSmsByYearMonth::getTotal)
                                        .sum();
                                donutSeries.add(new DataSeriesItem(actualSystemIdForeach.getSystemId(), totSid));
                            }));
        });
        PlotOptionsPie plotPie = new PlotOptionsPie();
        plotPie.setSize("70%");
        plotPie.setClassName("pie-chart");
        PlotOptionsPie plotDonut = new PlotOptionsPie();
        plotDonut.setInnerSize("75%");
        plotDonut.getDataLabels().setEnabled(false);
        /**/
        pieSeries.setPlotOptions(plotPie);
        donutSeries.setPlotOptions(plotDonut);
        /**/
        dataSeriesList.add(pieSeries);
//        dataSeriesList.add(donutSeries);
        /**/
        pieSeries.setName("SMS");
        donutSeries.setName("SMS");
        return dataSeriesList;
    }

    public List<Series> systemidAndTimeTotal(Collection<SystemId> systemidSet, List<? extends AbstractSmsByYearMonth> smsList,
                                             List<Integer> integerList) {
        if (smsList == null || systemidSet == null) {
            return new ArrayList<>();
        }
        Map<String, List<Long>> serieToChartMap = new HashMap<>();
        /* Recorre los carrier seleccionados. */
        systemidSet.forEach(actualSystemIdForeach -> {
            List<Long> totalList = new ArrayList<>();
            /* Recorre los meses del trimestre */
            integerList.forEach(actualMonthForeach -> {
                /* Total por Month y Carrier*/
                Long tot = smsList.stream()
                        .filter(sms -> sms.getGroupBy() == actualMonthForeach
                                && actualSystemIdForeach.getSystemId().equalsIgnoreCase(sms.getSomeCode()))
                        .mapToLong(AbstractSmsByYearMonth::getTotal)
                        .sum();
                totalList.add(tot);
            });
            serieToChartMap.put(actualSystemIdForeach.getSystemId(), totalList);

        });
        return convertToSeries(serieToChartMap);
    }

    private void popup(ClientTrimestralPopUpView view) {
        Dialog d = new Dialog();
        d.setWidth("75%");
        d.add(view);
        d.open();
        view.setConsumer(s -> d.close());
    }

    private String getStringLog() {
        return "[" + currentUser.getUser().getEmail() + "]";
    }
}
