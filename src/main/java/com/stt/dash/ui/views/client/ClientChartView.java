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
import com.stt.dash.ui.DailySmsShowGridView;
import com.stt.dash.ui.MainView;
import com.stt.dash.ui.MonthlySmsShowGridView;
import com.stt.dash.ui.SmsShowGridAllView;
import com.stt.dash.ui.SmsShowGridHourlyView;
import com.stt.dash.ui.SmsShowGridViewV2;
import com.stt.dash.ui.popup.ClientDailyPopupView;
import com.stt.dash.ui.popup.ClientMonthlyPopupView;
import com.stt.dash.ui.popup.ClientTrimestralPopUpView;
import com.stt.dash.ui.popup.MainDashBoardTrimestralPopUpView;
import com.stt.dash.ui.utils.BakeryConst;
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
    private Button filterButton;
    /**/
    Logger log = LoggerFactory.getLogger(ClientChartView.class);
    private final SmsHourService smsHourService;
    private final AbstractSmsService abstractSmsService;
    private final ListGenericBean<String> allUserStringSystemId;
    private final CurrentUser currentUser;
    /* CLIENTE */
//    private ComboBox<Client> clientCombobox = new ComboBox<>("Clientes");
//    private MultiComboBox<SystemId> systemIdMultiCombo = new MultiComboBox<>("Credenciales");
    //    private final CheckboxGroup<OMessageType> checkboxMessageType = new CheckboxGroup<>();
    /* Para Graficos y servicios */
    private List<Integer> monthToShowList;
    private List<String> selectedSystemIdList;
    private String[] ml;
    /* Button */
//    private Button filterButton = new Button("Actualizar");
    private List<Carrier> carrierList;

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
        checkboxMessageType.setLabel("Tipo de Mensajes");
        checkboxMessageType.setItems(OMessageType.values());
        checkboxMessageType.setValue(new HashSet<OMessageType>(Arrays.asList(OMessageType.values())));
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
        systemIdMultiCombo.setLabel("Credenciales");
        systemIdMultiCombo.setItemLabelGenerator(SystemId::getSystemId);
        /* ******* */
        /* ******* Client */
        clientCombobox.setLabel("Cliente");
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
        op = getCurrentSessionAttributeAndNullIt(CLIENT_VIEW_SELECTED_CLIENT);
        op.ifPresent(o -> {
            clientCombobox.setValue((Client) o);
        });
        /* ******* */
        /* HEADER */
//        divHeader.add(new HorizontalLayout(clientCombobox, checkboxMessageType),
//                new HorizontalLayout(systemIdMultiCombo), filterButton);
        clientCombobox.setWidthFull();
        checkboxMessageType.setWidthFull();
        systemIdMultiCombo.setWidthFull();
        updateCharts();
        filterButton.setEnabled(isValidSearch());
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
        Optional<Object> op = Optional.ofNullable(o);
        return op;
    }

    private void addValueChangeListener() {
        clientCombobox.addValueChangeListener(clientListener -> {
            systemIdMultiCombo.setItems(clientListener.getValue().getSystemids());
            Optional<Object> op = getCurrentSessionAttributeAndNullIt(CLIENT_VIEW_SELECTED_SYSTEMID);
            if (op.isPresent()) {
                systemIdMultiCombo.setValue((Set<SystemId>) op.get());
            } else {
                systemIdMultiCombo.setValue(new HashSet<>(clientListener.getValue().getSystemids()));
            }
        });

        clientCombobox.addBlurListener(blur -> {
            filterButton.setEnabled(isValidSearch());
        });

        systemIdMultiCombo.addBlurListener(blur -> {
            filterButton.setEnabled(isValidSearch());
        });

        checkboxMessageType.addValueChangeListener(value -> {
            filterButton.setEnabled(isValidSearch());
        });

        filterButton.addClickListener(clickEvent -> {
            filterButton.setEnabled(false);
            /* TODO: Validar si tien todos los datos */
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
        confHourlyChart.setSubTitle("por operadoras");
        Tooltip tooltip = new Tooltip();
        tooltip.setValueDecimals(0);
        tooltip.setHeaderFormat("<span style=\"font-size: 10px\">{point.key} {point.percentage:%02.2f}%</span><br/>");
        confHourlyChart.setTooltip(tooltip);
        /* Column Chart*/
        List<SmsByYearMonthDayHour> l = smsHourService.getGroupSystemIdByYeMoDaHoCaWhYeMoDayEqMessageTypeIn(LocalDate.now().getYear(), actualMonth, actualDay, checkboxMessageType.getSelectedItems(), allUserStringSystemId.getList());
        List<DataSeries> LineDateSeriesList = paEntenderPie(l, Arrays.asList(actualDay));
        addToPieChart(confHourlyChart, LineDateSeriesList, innerPieOptions);
    }

    private void updateMonthlyPie() {
        Configuration confMonthlyChart = smsThisMonthPieChart.getConfiguration();
        PlotOptionsPie innerPieOptions = new PlotOptionsPie();

        confMonthlyChart.setTitle("Este Mes");
        confMonthlyChart.setSubTitle("por operadoras");
        Tooltip tooltip = new Tooltip();
        tooltip.setValueDecimals(0);
        tooltip.setHeaderFormat("<span style=\"font-size: 10px\">{point.key} {point.percentage:%02.2f}%</span><br/>");
        confMonthlyChart.setTooltip(tooltip);
        /* Column Chart*/
        List<SmsByYearMonth> l = smsHourService.getGroupSystemIdByYeMoCaWhMoInMessageTypeIn(LocalDate.now().getYear(), Arrays.asList(actualMonth), checkboxMessageType.getSelectedItems(), allUserStringSystemId.getList());
        List<DataSeries> LineDateSeriesList = paEntenderPie(l, Arrays.asList(actualMonth));
        addToPieChart(confMonthlyChart, LineDateSeriesList, innerPieOptions);
    }

    private void updateTrimestrePie() {
        Configuration confHourlyChart = smsLastMonthsPieChart.getConfiguration();
        PlotOptionsPie innerPieOptions = new PlotOptionsPie();
//        innerPieOptions.setSize("70%")
//
        confHourlyChart.setTitle("Trimestral");
        confHourlyChart.setSubTitle("por operadoras");
        Tooltip tooltip = new Tooltip();
        tooltip.setValueDecimals(0);
        tooltip.setHeaderFormat("<span style=\"font-size: 10px\">{point.key} {point.percentage:%02.2f}%</span><br/>");
        confHourlyChart.setTooltip(tooltip);
        ;
        /* Column Chart*/
        List<SmsByYearMonth> l = smsHourService.getGroupSystemIdByYeMoCaWhMoInMessageTypeIn(LocalDate.now().getYear(), monthsIn(2), checkboxMessageType.getSelectedItems(), allUserStringSystemId.getList());
        List<DataSeries> LineDateSeriesList = paEntenderPie(l, monthsIn(2));
        addToPieChart(confHourlyChart, LineDateSeriesList, innerPieOptions);
    }

    private void updateHourlyChart() {
        Configuration confHourlyChart = smsThisDayChart.getConfiguration();

        smsThisDayChart.addPointClickListener(listener -> {
            int seriesItemIndex = listener.getItemIndex();
            Dialog d = new Dialog();
            d.setWidth("75%");
            /* Convertir Set<SystemId> seleccionados en un List<String>*/
            List<String> selectedSystemIdList = systemIdMultiCombo.getValue().stream().map(SystemId::getSystemId).collect(Collectors.toList());
            ClientDailyPopupView view = new ClientDailyPopupView(smsHourService, actualYear, actualMonth, actualDay, seriesItemIndex, selectedSystemIdList);
            d.add(view);
            d.open();
            view.setConsumer((s) -> d.close());
        });

        smsThisDayChart.addChartClickListener(listener -> {
            Dialog d = new Dialog();
            d.setWidth("75%");
            /* Convertir Set<SystemId> seleccionados en un List<String>*/
            List<String> selectedSystemIdList = systemIdMultiCombo.getValue().stream().map(SystemId::getSystemId).collect(Collectors.toList());
            ClientDailyPopupView view = new ClientDailyPopupView(smsHourService, actualYear, actualMonth, actualDay, selectedSystemIdList);
            d.add(view);
            d.open();
            view.setConsumer((s) -> d.close());
        });
        confHourlyChart.setTitle(OMonths.valueOf(actualMonth).getMonthName() + " - dia de hoy");
        confHourlyChart.setSubTitle("por hora");
        confHourlyChart.getyAxis().setTitle("SMS");
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
            showNotification("Por favor seleccione Credenciales.");
            return;
        }
        /* Column Chart*/
        List<SmsByYearMonthDayHour> smsList = smsHourService.groupSmsYeMoDaHoTyWhYeMoDaSyIn(LocalDate.now().getYear(), actualMonth, actualDay, allUserStringSystemId.getList());
        if (smsList == null || smsList.isEmpty()) {
            log.info("Hourly Chart Without data to show");
            return;
        }
        List<Series> LineDateSeriesList = messageTypeAndMonthlyTotal(checkboxMessageType.getSelectedItems(), smsList, hourList);
        addToChart(confHourlyChart, LineDateSeriesList, plotColum);
        /* Convertir Set<SystemId> seleccionados en un List<String>*/
        List<String> selectedSystemIdList = systemIdMultiCombo.getValue().stream().map(SystemId::getSystemId).collect(Collectors.toList());
        /* Line Chart */
        smsList = smsHourService.getGroupSystemIdByYeMoDaHoWhYeMoDayEqMessageTypeIn(LocalDate.now().getYear(), actualMonth, actualDay, checkboxMessageType.getSelectedItems(), selectedSystemIdList);
        PlotOptionsLine plotLine = new PlotOptionsLine();
        LineDateSeriesList = systemidAndTimeTotal(systemIdMultiCombo.getValue(), smsList, hourList);
        addToChart(confHourlyChart, LineDateSeriesList, plotLine);
    }

    /**
     *
     */
    private void updateMonthlyLineChart() {
        Configuration confMonthlyLineChart = smsThisMonthChart.getConfiguration();
        /**/
        smsThisMonthChart.addPointClickListener(click -> {
            /* El dia comienza en 1. */
            int seriesItemIndex = click.getItemIndex() + 1;
            /* Convertir Set<SystemId> seleccionados en un List<String>*/
            List<String> selectedSystemIdList = systemIdMultiCombo
                    .getValue()
                    .stream()
                    .map(SystemId::getSystemId)
                    .collect(Collectors.toList());

            Dialog d = new Dialog();
            d.setWidth("75%");
            ClientMonthlyPopupView view = new ClientMonthlyPopupView(smsHourService, actualYear, actualMonth, seriesItemIndex, selectedSystemIdList, checkboxMessageType.getSelectedItems());
            view.setTitles("Gráfico - Mensajes este mes", clientCombobox.getValue() == null ? "Cliente" : clientCombobox.getValue().getClientCod());
            d.add(view);
            d.open();
            view.setConsumer((s) -> d.close());
        });
        smsThisMonthChart.addChartClickListener(click -> {
            /* Convertir Set<SystemId> seleccionados en un List<String>*/
            List<String> selectedSystemIdList = systemIdMultiCombo.getValue().stream().map(SystemId::getSystemId).collect(Collectors.toList());
            Dialog d = new Dialog();
            d.setWidth("75%");
            ClientMonthlyPopupView view = new ClientMonthlyPopupView(smsHourService, actualYear, actualMonth, selectedSystemIdList, checkboxMessageType.getSelectedItems());
            view.setTitles("Gráfico - Mensajes este mes", clientCombobox.getValue() == null ? "Cliente" : clientCombobox.getValue().getClientCod());
            d.add(view);
            d.open();
            view.setConsumer((s) -> d.close());
        });
        /**/
        confMonthlyLineChart.getyAxis().setTitle("SMS");
        confMonthlyLineChart.setSubTitle("por dia");
        confMonthlyLineChart.setTitle(OMonths.valueOf(actualMonth).getMonthName() + " - " + actualYear);
        /**/
        String[] da = new String[LocalDate.now().getMonth().maxLength()];
        for (int i = 1; i <= LocalDate.now().getMonth().maxLength(); i++) {
            da[i - 1] = i + "";
        }
        confMonthlyLineChart.getxAxis().setCategories(da);
        /**/
        PlotOptionsLine plotColum = new PlotOptionsLine();
        /**/
        Tooltip tooltip = new Tooltip();
        tooltip.setValueDecimals(0);
        tooltip.setShared(true);
        tooltip.setHeaderFormat("<span style=\"font-size: 10px\">Dia: {point.key}</span><br/>");
        confMonthlyLineChart.setTooltip(tooltip);
        /* Column Chart*/
        List<SmsByYearMonthDay> smsByYearMonthDayList = smsHourService.groupSmsByYeMoDaTyWhYeMoSyIn(LocalDate.now().getYear(), actualMonth, allUserStringSystemId.getList());
        List<Series> LineDateSeriesList = messageTypeAndMonthlyTotal(checkboxMessageType.getSelectedItems(), smsByYearMonthDayList, dayList);
        addToChart(confMonthlyLineChart, LineDateSeriesList, plotColum);
        /* Line Chart */
        smsByYearMonthDayList = smsHourService.groupSmsByYeMoDaSyWhYeMoSyIn_TyIn(LocalDate.now().getYear(), actualMonth, checkboxMessageType.getSelectedItems(), allUserStringSystemId.getList());
        PlotOptionsLine plotLine = new PlotOptionsLine();
        LineDateSeriesList = systemidAndTimeTotal(systemIdMultiCombo.getValue(), smsByYearMonthDayList, dayList);
        addToChart(confMonthlyLineChart, LineDateSeriesList, plotLine);
    }

    private void updateTriMixChart() {
        /* Convertir Set<SystemId> seleccionados en un List<String>*/
        systemIdMultiCombo
                .getOptionalValue()
                .ifPresent(value ->
                        selectedSystemIdList = value.stream().map(SystemId::getSystemId).collect(Collectors.toList())
                );
        /**/
        Configuration confTriMixChart = smsLastThreeMonthChart.getConfiguration();
        smsLastThreeMonthChart.addPointClickListener(click -> {
            Dialog d = new Dialog();
            d.setWidth("75%");
            List<Integer> month = monthsIn(2);
            Integer integer = month.get(click.getItemIndex());
            List<String> messageTypeList = checkboxMessageType.getSelectedItems().stream().map(OMessageType::name).collect(Collectors.toList());
            ClientTrimestralPopUpView view = new ClientTrimestralPopUpView(smsHourService, actualYear, integer, selectedSystemIdList, messageTypeList);
            d.add(view);
            d.open();
            view.setConsumer((s) -> d.close());
        });
        smsLastThreeMonthChart.addChartClickListener(click -> {
            Dialog d = new Dialog();
            d.setWidth("75%");
            List<String> messageTypeList = checkboxMessageType.getSelectedItems().stream().map(OMessageType::name).collect(Collectors.toList());
            ClientTrimestralPopUpView view = new ClientTrimestralPopUpView(smsHourService, actualYear, monthsIn(2), selectedSystemIdList, messageTypeList);
            d.add(view);
            d.open();
            view.setConsumer((s) -> d.close());
        });
        confTriMixChart.getyAxis().setTitle("SMS");
        confTriMixChart.setTitle("Trimestral - " + LocalDate.now().getYear());
        PlotOptionsColumn plotColum = new PlotOptionsColumn();
        /* Averiguar cuales son los tres meses a calular. */
        /**/
        confTriMixChart.getxAxis().setCategories(ml);
        Tooltip tooltip = new Tooltip();
        tooltip.setValueDecimals(0);
        tooltip.setShared(true);
        confTriMixChart.setTooltip(tooltip);

        /* Buscar con todos los systemids del usuario. */
        List<SmsByYearMonth> l = smsHourService.groupSmsMessageTypeByYeMoWhYeMoInSyIn(actualYear, monthsIn(2), allUserStringSystemId.getList());
        List<Series> LineDateSeriesList = messageTypeAndMonthlyTotal(checkboxMessageType.getSelectedItems(), l, monthsIn(2));
        addToChart(confTriMixChart, LineDateSeriesList, plotColum);
        /* LINE CHART */
        l = smsHourService.
                getGroupSystemIdByYeMoWhMoInMessageTypeIn(LocalDate.now().getYear(), monthsIn(2), checkboxMessageType.getSelectedItems(), selectedSystemIdList);

        PlotOptionsLine plotLine = new PlotOptionsLine();
        LineDateSeriesList = systemidAndTimeTotal(systemIdMultiCombo.getValue(), l, monthsIn(2));
        addToChart(confTriMixChart, LineDateSeriesList, plotLine);
    }

    private void addToChart(Configuration configuration, List<Series> LineDateSeriesList, AbstractPlotOptions plot) {
        if (LineDateSeriesList == null || LineDateSeriesList.size() == 0) {
            log.info("{} NO DATA FOR CARRIER CHART LINE");
        } else {
            for (int i = 0; i < LineDateSeriesList.size(); i++) {
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
                System.out.println("ADDING LINE PIE********" + LineDateSeriesList.get(i).getName());
                Series series = LineDateSeriesList.get(i);
//                series.setPlotOptions(plot);
                configuration.addSeries(series);
            }
        }
    }

    public List<DataSeries> paEntenderPie(List<? extends AbstractSmsByYearMonth> smsList, List<Integer> integerList) {
        List<DataSeries> dataSeriesList = new ArrayList<>();
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
                    .mapToLong(sms -> sms.getTotal())
                    .sum();
            pieSeries.add(new DataSeriesItem(actualCarrierForeach, carrierTot), false, false);
            /* Total por S*/
            systemIdMultiCombo.getOptionalValue()
                    .ifPresent(systemIdSet ->
                            systemIdSet.forEach(actualSystemIdForeach -> {
                                Long totSid = smsList.parallelStream()
                                        .filter(sms -> sms.getMessageType().equalsIgnoreCase(actualCarrierForeach)
                                                && actualSystemIdForeach.getSystemId().equalsIgnoreCase(sms.getSomeCode()))
                                        .mapToLong(sms -> sms.getTotal())
                                        .sum();
                                donutSeries.add(new DataSeriesItem(actualSystemIdForeach.getSystemId(), totSid));
                            }));
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
        List<Series> dataSeriesList = new ArrayList<>();
        /* Recorre los carrier seleccionados. */
        systemidSet.forEach(actualSystemIdForeach -> {
            List<Long> totalList = new ArrayList<>();
            /* Recorre los meses del trimestre */
            integerList.forEach(actualMonthForeach -> {
                /* Total por Month y Carrier*/
                Long tot = smsList.stream()
                        .filter(sms -> sms.getGroupBy() == actualMonthForeach
                                && actualSystemIdForeach.getSystemId().equalsIgnoreCase(sms.getSomeCode()))
                        .mapToLong(sms -> sms.getTotal())
                        .sum();
                totalList.add(tot);
            });
            serieToChartMap.put(actualSystemIdForeach.getSystemId(), totalList);

        });
        return convertToSeries(serieToChartMap);
    }

    private String getStringLog() {
        return "[" + currentUser.getUser().getEmail() + "]";
    }
}
