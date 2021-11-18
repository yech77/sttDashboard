/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.stt.dash.ui.views.agenda;

import com.stt.dash.backend.data.OUserSession;
import com.stt.dash.backend.data.entity.Agenda;
import com.stt.dash.backend.repositories.OUserRepository;
import com.stt.dash.backend.service.AgendaService;
import com.stt.dash.backend.util.AgendaFileUtils;
import com.stt.dash.backend.util.SessionObjectUtils;
import com.stt.dash.ui.MainView;
import com.stt.dash.ui.utils.ODateUitls;
import com.stt.dash.ui.views.dashboard.WrapperCard;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.board.Board;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.dependency.JsModule;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.NumberField;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.StreamResource;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.vaadin.olli.FileDownloadWrapper;

/**
 * @author Enrique
 */
@PageTitle("Detalles Agenda")
@Route(value = "editAgenda", layout = MainView.class)
//@CssImport(value = "./styles/views/agenda/edit-agenda-view.css", include = "lumo-badge")
@JsModule("@vaadin/vaadin-lumo-styles/badge.js")
public class EditAgendaView extends VerticalLayout {

    private Board board;
    private Agenda agenda;

    private WrapperCard titleCard;

    private WrapperCard infoCard;
    private Span infoSpaceTitle;
    private Span statusSpaceTitle;
    private Button openAdvanced;
    private Button closeAdvanced;
    private HorizontalLayout mainSpace;
    private VerticalLayout infoSpace;
    private Span nameTitle;
    private Span nameBox;
    private Span descTitle;
    private Span descBox;
    private Span dateTitle;
    private Span dateBox;
    private Span creatorTitle;
    private Span creatorBox;
    private Span fileNameTitle;
    private Span fileNameBox;
    private Button editInfoButton;
    private VerticalLayout statusSpace;
    private Span statusTitle;
    private Span statusBox;
    private Span itemCountTitle;
    private Span itemCountBox;
    private Span validItemCountTitle;
    private Span validItemCountBox;
    private Span invalidItemCountTitle;
    private Span invalidItemCountBox;
    private Span parameterCountTitle;
    private Span parameterCountBox;
    private Button trialSmsButton;
    private Span trialSmsTitle;
    private TextArea trialSmsBox;
    private Span trialSmsResult;

    private WrapperCard logCard;
    private Span logCardTitle;
    private HorizontalLayout logButtonsSpace;
    private Button printLogButton;
    private Button downloadLogButton;
    private VerticalLayout logCage;

    private WrapperCard linesCard;
    private Span linesCardTitle;
    private HorizontalLayout linesButtonsSpace;
    private NumberField minLine;
    private NumberField maxLine;
    private Button printLinesButton;
    private Button downloadLinesButton;
    private VerticalLayout linesCage;

    private WrapperCard bottomButtonsCard;
    private HorizontalLayout bottomButtons;

    private final OUserSession ouser_session;
    private SessionObjectUtils session_utils;
    private Button deleteButton;
    private Button returnButton;
    private ODateUitls date_utils;
    private AgendaService agenda_service;
    private OUserRepository ouser_repo;

    public EditAgendaView(OUserSession ouser_session,
                          AgendaService agenda_service,
                          OUserRepository ouser_repo) {

        this.ouser_session = ouser_session;
        this.agenda_service = agenda_service;
        this.session_utils = new SessionObjectUtils(ouser_session);
        this.ouser_repo = ouser_repo;

        date_utils = new ODateUitls();
        setId("edit-agenda-view");
        addClassName("edit-agenda-view");

        agenda = (Agenda) this.ouser_session.getData("selectedAgenda");
        if (agenda == null) {
            System.out.println("selected agenda was null; redirecting to Agenda View");
            getUI().get().navigate("agenda");
        }
        board = new Board();

        initTitleCard();
        initInfoCard();
        initLogCard();
        initLinesCard();
        initBottomButtonsCard();

        board.add(titleCard);
        board.add(infoCard);
        board.addRow(logCard, linesCard);
        board.add(bottomButtonsCard);

        add(board);
    }

    public void initTitleCard() {
        Span titleSpan = new Span("Detalles de Agenda");
        titleSpan.addClassName("title-text");
        titleCard = new WrapperCard("wrapper",
                new Component[]{titleSpan}, "card", "space-m");
    }

    public void initInfoCard() {

        // INFO SPACE
        infoSpace = new VerticalLayout();
        infoSpaceTitle = new Span("Características");
        infoSpaceTitle.addClassName("title-text");
        nameTitle = new Span("Nombre Agenda:");
        nameTitle.addClassName("item-name-text");
        nameBox = new Span(agenda.getName());
        nameBox.addClassName("item-text");
        descTitle = new Span("Descripción:");
        descTitle.addClassName("item-name-text");
        descBox = new Span(agenda.getDescription());
        descBox.addClassName("item-text");
        dateTitle = new Span("Fecha Creada:");
        dateTitle.addClassName("item-name-text");
        dateBox = new Span("" + agenda.getDateCreated().toString().substring(0, 16));
        dateBox.addClassName("item-text");
        creatorTitle = new Span("Creado por:");
        creatorTitle.addClassName("item-name-text");
        creatorBox = new Span(agenda.getCreatorEmail());
        creatorBox.addClassName("item-text");
        fileNameTitle = new Span("Nombre del Archivo:");
        fileNameTitle.addClassName("item-name-text");
        fileNameBox = new Span(agenda.getFileName());
        fileNameBox.addClassName("item-text");
        editInfoButton = new Button("Editar", click -> editInfo());
        infoSpace.add(infoSpaceTitle, nameTitle, nameBox, descTitle, descBox,
                dateTitle, dateBox, creatorTitle, creatorBox, fileNameTitle,
                fileNameBox, editInfoButton);

        // STATUS SPACE
        statusSpace = new VerticalLayout();
        statusSpaceTitle = new Span("Estatus y Datos");
        statusSpaceTitle.addClassName("title-text");
        statusTitle = new Span("Estatus:");
        statusTitle.addClassName("item-name-text");
        statusBox = new Span(agenda.getStringStatus());
        statusBox.addClassName("item-text");
        itemCountTitle = new Span("Total Registros:");
        itemCountTitle.addClassName("item-name-text");
        int totalCount = agenda.getItemCount();
        int invalidCount = agenda.getInvalidItemCount();
        int validCount = totalCount - invalidCount;
        itemCountBox = new Span("" + totalCount + " registros");
        itemCountBox.addClassName("item-text");
        validItemCountTitle = new Span("Registros Válidos:");
        validItemCountTitle.addClassName("item-name-text");
        int validPart = (int) (1000 * ((double) validCount / totalCount));
        validItemCountBox = new Span("" + validCount + " (" + (((double) validPart) / 10) + "%)");
        validItemCountBox.addClassName("item-text");
        invalidItemCountTitle = new Span("Registros Inválidos:");
        invalidItemCountTitle.addClassName("item-name-text");
        int invalidPart = (int) (1000 * ((double) invalidCount / totalCount));
        invalidItemCountBox = new Span("" + invalidCount + " (" + (((double) invalidPart) / 10) + "%)");
        invalidItemCountBox.addClassName("item-text");
        parameterCountTitle = new Span("Cantidad de Parámetros:");
        parameterCountTitle.addClassName("item-name-text");
        parameterCountBox = new Span("" + (agenda.getFirstLine().split(",").length - 1));
        parameterCountBox.addClassName("item-text");
        trialSmsButton = new Button("Generar SMS de ejemplo", click -> createTrialSms());
        trialSmsTitle = new Span("Mensaje Resultante:");
        trialSmsTitle.addClassName("item-name-text");
        trialSmsTitle.setVisible(false);
        trialSmsResult = new Span("");
        trialSmsResult.addClassName("item-text");
        trialSmsResult.setVisible(false);
        trialSmsBox = new TextArea("Texto SMS");
        trialSmsBox.setPlaceholder("Introduzca un texto de ejemplo...");
        trialSmsBox.setVisible(false);
        trialSmsBox.setValueChangeMode(ValueChangeMode.EAGER);
        trialSmsBox.addInputListener(event -> {
            int varCount = 0;
            String[] vars = agenda.getFirstLine().split(",");
            int agendaVarCount = (vars.length - 1);
            while (trialSmsBox.getValue().contains("$" + varCount)) {
                varCount++;
            }
            String ln = trialSmsBox.getValue();
            for (int i = 0; i < varCount && i < agendaVarCount; i++) {
                ln = ln.replace("$" + i, vars[i + 1]);
            }
            trialSmsResult.setText(ln);
        });
        statusSpace.add(statusSpaceTitle, statusTitle, statusBox, itemCountTitle,
                itemCountBox, validItemCountTitle, validItemCountBox, invalidItemCountTitle,
                invalidItemCountBox, parameterCountTitle, parameterCountBox, trialSmsButton,
                trialSmsBox, trialSmsTitle, trialSmsResult);

        mainSpace = new HorizontalLayout();
        mainSpace.setAlignItems(Alignment.CENTER);
        mainSpace.setSizeFull();
        infoSpace.setSizeFull();
        statusSpace.setSizeFull();
        mainSpace.add(infoSpace, statusSpace);

        openAdvanced = new Button("Abrir controles Avanzados", click -> openAdvanced());
        closeAdvanced = new Button("Cerrar controles Avanzados", click -> closeAdvanced());
        closeAdvanced.setVisible(false);

        infoCard = new WrapperCard("wrapper",
                new Component[]{mainSpace, openAdvanced, closeAdvanced}, "card", "space-m");

    }

    public void initLogCard() {

        logCardTitle = new Span("Reporte de Validación");
        logCardTitle.addClassName("title-text");
        logButtonsSpace = new HorizontalLayout();
        printLogButton = new Button("Imprimir Reporte", click -> printLog());
        downloadLogButton = new Button("Descargar Reporte", click -> downloadLog());

        logCage = new VerticalLayout();
        logCage.getStyle().set("overflow", "auto");
        logCage.getStyle().set("border", "1px solid");
        logCage.setHeight("400px");
        logCage.addClassName("no-margin");

        logButtonsSpace.add(printLogButton, downloadLogButton);
        logCard = new WrapperCard("wrapper",
                new Component[]{logCardTitle, logButtonsSpace, logCage}, "card", "space-m");
        logCard.setVisible(false);
    }

    public void initLinesCard() {

        linesCardTitle = new Span("Vista Detallada Archivo");
        linesCardTitle.addClassName("title-text");
        linesButtonsSpace = new HorizontalLayout();
        minLine = new NumberField();
        minLine.setMin(1);
        minLine.setMax(agenda.getItemCount());
        minLine.setStep(1);
        minLine.setValue(1.0);
        minLine.setHasControls(true);
        minLine.addValueChangeListener(event -> updateRanges());
        maxLine = new NumberField();
        maxLine.setMin(1);
        maxLine.setMax(agenda.getItemCount());
        maxLine.setStep(1);
        maxLine.setValue(1.0);
        maxLine.setValueChangeMode(ValueChangeMode.EAGER);
        maxLine.setHasControls(true);
        maxLine.addValueChangeListener(event -> updateRanges());
        printLinesButton = new Button("Imprimir Detalles", click -> printLines());
        downloadLinesButton = new Button("Descargar Archivo", click -> downloadFile());
        printLinesButton.setEnabled(false);
        linesCage = new VerticalLayout();
        linesCage.getStyle().set("overflow", "auto");
        linesCage.getStyle().set("border", "1px solid");
        linesCage.setHeight("400px");
        linesCage.addClassName("no-margin");

        linesButtonsSpace.add(minLine, maxLine, printLinesButton, downloadLinesButton);
        linesCard = new WrapperCard("wrapper",
                new Component[]{linesCardTitle, linesButtonsSpace, linesCage}, "card", "space-m");
        linesCard.setVisible(false);
    }

    public void initBottomButtonsCard() {

        bottomButtons = new HorizontalLayout();
        deleteButton = new Button("Borrar Agenda", click -> deleteConfirm());
        returnButton = new Button("Regresar", click -> {
            getUI().get().navigate("agenda");
        });
        deleteButton.addThemeVariants(ButtonVariant.LUMO_ERROR);
        returnButton.addThemeVariants(ButtonVariant.LUMO_CONTRAST);
        bottomButtons.add(deleteButton, returnButton);
        bottomButtonsCard = new WrapperCard("wrapper",
                new Component[]{bottomButtons}, "card", "space-m");
    }

    public void updateRanges() {
        printLinesButton.setEnabled(true);
    }

    public void editInfo() {
        Dialog dialog = new Dialog();
        TextArea name = new TextArea("Nombre");
        name.setValue(agenda.getName());
        TextArea desc = new TextArea("Descripción");
        desc.setValue(agenda.getDescription());
        dialog.setCloseOnEsc(true);
        dialog.setCloseOnOutsideClick(true);

        Button confirmButton = new Button("Guardar Cambios", event -> {
            if (name.getValue() == null || name.getValue().length() < 1) {
                Notification notification = new Notification("El nombre no puede estar vacío", 2500, Notification.Position.BOTTOM_START);
                notification.addThemeVariants(NotificationVariant.LUMO_ERROR);
                notification.open();
            } else {
                agenda.setName(name.getValue());
                agenda.setDescription(desc.getValue());
                agenda_service.save(agenda);
                nameBox.setText(name.getValue());
                descBox.setText(desc.getValue());
                dialog.close();
                Notification notification = new Notification("Se han Guardado los cambios", 2500, Notification.Position.BOTTOM_START);
                notification.open();
            }

        });
        Button cancelButton = new Button("Cancelar", event -> {
            dialog.close();
        });

        Span question = new Span("Editar Detalles");

        HorizontalLayout hl = new HorizontalLayout();
        HorizontalLayout hl2 = new HorizontalLayout();
        VerticalLayout vl = new VerticalLayout();
        vl.add(question, hl2, hl);

        hl2.add(name, desc);
        hl.add(confirmButton, cancelButton);
        dialog.add(vl);
        dialog.open();
    }

    public void openAdvanced() {
        openAdvanced.setVisible(false);
        closeAdvanced.setVisible(true);
        logCard.setVisible(true);
        linesCard.setVisible(true);
    }

    public void closeAdvanced() {
        openAdvanced.setVisible(true);
        closeAdvanced.setVisible(false);
        logCard.setVisible(false);
        linesCard.setVisible(false);
    }

    public void createTrialSms() {
        if (trialSmsTitle.isVisible()) {
            trialSmsTitle.setVisible(false);
            trialSmsBox.setVisible(false);
            trialSmsResult.setVisible(false);
        } else {
            trialSmsTitle.setVisible(true);
            trialSmsBox.setVisible(true);
            trialSmsResult.setVisible(true);
        }
    }

    public void printLog() {
        InputStream stream = AgendaFileUtils.getStreamValidationLog(agenda.getFileName());
        BufferedReader br = new BufferedReader(new InputStreamReader(stream, StandardCharsets.UTF_8));
        logCage.removeAll();
        try {
            int count = 1;
            String line = br.readLine();
            String className = line.contains("No hay problemas.") ? "bold" : "log-text";
            while (line != null && count <= 200) {
                Span tex = new Span(line);
                tex.addClassName(className);
                Div cont = new Div();
                cont.setWidthFull();
                cont.addClassName("no-margin");
                if (count % 2 == 0) {
                    cont.addClassName("alternate-background");
                }
                cont.add(tex);
                logCage.add(cont);
                line = br.readLine();
                count++;
            }
            if (line != null) {
                Span tex = new Span("y mas...");
                tex.addClassName(className);
                Div cont = new Div();
                cont.setWidthFull();
                cont.add(tex);
                cont.addClassName("no-margin");
                if (count % 2 == 0) {
                    cont.addClassName("alternate-background");
                }
                logCage.add(cont);
            }
            br.close();

        } catch (IOException ex) {
            Logger.getLogger(EditAgendaView.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    public void downloadLog() {

        // Crea dialogo
        Dialog dialog = new Dialog();
        dialog.setCloseOnEsc(false);
        dialog.setCloseOnOutsideClick(false);

        // Genera String 
        InputStream stream = AgendaFileUtils.getStreamValidationLog(agenda.getFileName());
        BufferedReader br = new BufferedReader(new InputStreamReader(stream, StandardCharsets.UTF_8));
        StringBuilder sb = new StringBuilder();
        try {
            String line = br.readLine();
            while (line != null) {
                sb.append(line).append("\n");
                line = br.readLine();
            }
            br.close();
        } catch (IOException ex) {
            Logger.getLogger(EditAgendaView.class.getName()).log(Level.SEVERE, null, ex);
        }

        // Crea Boton de Descarga
        String fileName = agenda.getName() + "-error_log.txt";
        Button confirmButton = new Button("Descargar Log", event -> dialog.close());
        FileDownloadWrapper buttonWrapper = new FileDownloadWrapper(
                new StreamResource(fileName, () -> new ByteArrayInputStream(sb.toString().getBytes())));
        buttonWrapper.wrapComponent(confirmButton);

        // Llena Dialogo con texto y botones
        Button cancelButton = new Button("Cancelar", event -> {
            dialog.close();
        });
        Span question = new Span("Por favor confirme para completar la descarga:");
        HorizontalLayout hl = new HorizontalLayout();
        VerticalLayout vl = new VerticalLayout();
        vl.add(question, hl);
        hl.add(buttonWrapper, cancelButton);
        dialog.add(vl);
        dialog.open();
    }

    public void printLines() {

        if (minLine.getValue() < 1) {
            minLine.setValue(1.0);
        }
        if (maxLine.getValue() < 1) {
            maxLine.setValue(1.0);
        }
        if (minLine.getValue() > agenda.getItemCount()) {
            minLine.setValue(1.0 * agenda.getItemCount());
        }
        if (maxLine.getValue() > agenda.getItemCount()) {
            maxLine.setValue(1.0 * agenda.getItemCount());
        }

        if (minLine.getValue() > maxLine.getValue()) {
            maxLine.setValue(minLine.getValue());
        }
        if (maxLine.getValue() - minLine.getValue() > 200) {
            maxLine.setValue(minLine.getValue() + 200);
        }

        printLinesButton.setEnabled(false);
        InputStream stream = AgendaFileUtils.getFileAsStream(agenda.getFileName());
        BufferedReader br = new BufferedReader(new InputStreamReader(stream, StandardCharsets.UTF_8));
        linesCage.removeAll();
        try {
            int count = (int) minLine.getValue().doubleValue();
            int startCount = 1;
            String line = br.readLine();
            while (line != null && startCount < count) {
                line = br.readLine();
                startCount++;
            }
            while (line != null && count <= maxLine.getValue()) {
                Span tex = new Span("" + count + ": " + line);
                tex.addClassName("lines-text");
                Div cont = new Div();
                cont.setWidthFull();
                cont.addClassName("no-margin");
                if (count % 2 == 0) {
                    cont.addClassName("alternate-background");
                }
                cont.add(tex);
                linesCage.add(cont);
                line = br.readLine();
                count++;
            }
            br.close();

        } catch (IOException ex) {
            Logger.getLogger(EditAgendaView.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    public void downloadFile() {
        // Crea dialogo
        Dialog dialog = new Dialog();
        dialog.setCloseOnEsc(false);
        dialog.setCloseOnOutsideClick(false);

        // Genera String 
        InputStream stream = AgendaFileUtils.getFileAsStream(agenda.getFileName());
        BufferedReader br = new BufferedReader(new InputStreamReader(stream, StandardCharsets.UTF_8));
        StringBuilder sb = new StringBuilder();
        try {
            String line = br.readLine();
            while (line != null) {
                sb.append(line).append("\n");
                line = br.readLine();
            }
            br.close();
        } catch (IOException ex) {
            Logger.getLogger(EditAgendaView.class.getName()).log(Level.SEVERE, null, ex);
        }

        // Crea Boton de Descarga
        String fileName = agenda.getFileName();
        Button confirmButton = new Button("Descargar Agenda", event -> dialog.close());
        FileDownloadWrapper buttonWrapper = new FileDownloadWrapper(
                new StreamResource(fileName, () -> new ByteArrayInputStream(sb.toString().getBytes())));
        buttonWrapper.wrapComponent(confirmButton);

        // Llena Dialogo con texto y botones
        Button cancelButton = new Button("Cancelar", event -> {
            dialog.close();
        });
        Span question = new Span("Por favor confirme para completar la descarga:");
        HorizontalLayout hl = new HorizontalLayout();
        VerticalLayout vl = new VerticalLayout();
        vl.add(question, hl);
        hl.add(buttonWrapper, cancelButton);
        dialog.add(vl);
        dialog.open();
    }

    private void deleteConfirm() {
        Dialog dialog = new Dialog();

        dialog.setCloseOnEsc(false);
        dialog.setCloseOnOutsideClick(false);

        Button confirmButton = new Button("Confirmar", event -> {
            // delete files and entity
            AgendaFileUtils.deleteFile(agenda.getFileName());
            agenda_service.delete(agenda);
            dialog.close();
            Notification notification = new Notification("Se ha borrado la Agenda de Contactos", 2500, Notification.Position.BOTTOM_START);
            notification.open();
            getUI().get().navigate("agenda");

        });
        Button cancelButton = new Button("Cancelar", event -> {
            dialog.close();
        });

        Span question = new Span("Estás seguro que quieres borrar esta Agenda?");

        HorizontalLayout hl = new HorizontalLayout();
        VerticalLayout vl = new VerticalLayout();
        vl.add(question, hl);
        hl.add(confirmButton, cancelButton);
        dialog.add(vl);
        dialog.open();
    }
}
