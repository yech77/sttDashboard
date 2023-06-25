package com.stt.dash.ui.views.bulksms;

import com.stt.dash.app.OProperties;
import com.stt.dash.app.security.CurrentUser;
import com.stt.dash.backend.data.Role;
import com.stt.dash.backend.data.entity.Agenda;
import com.stt.dash.backend.data.entity.User;
import com.stt.dash.backend.service.AgendaService;
import com.stt.dash.backend.thread.AgendaParserRunnable;
import com.stt.dash.backend.util.AgendaFileUtils;
import com.stt.dash.ui.MainView;
import com.stt.dash.ui.crud.AbstractBakeryCrudView;
import com.stt.dash.ui.crud.STTBinderCrudEditor;
import com.stt.dash.ui.utils.BakeryConst;
import com.stt.dash.ui.utils.BeforeSavingResponse;
import com.stt.dash.ui.views.agenda.EditAgendaView;
import com.stt.dash.uiv2.components.Badge;
import com.stt.dash.uiv2.util.css.lumo.BadgeColor;
import com.stt.dash.uiv2.util.css.lumo.BadgeShape;
import com.stt.dash.uiv2.util.css.lumo.BadgeSize;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.grid.ColumnTextAlign;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.data.renderer.TemplateRenderer;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.StreamResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.reactive.function.client.WebClient;
import org.vaadin.olli.FileDownloadWrapper;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.logging.Level;
import java.util.logging.Logger;

@Route(value = BakeryConst.PAGE_BULKSMS, layout = MainView.class)
@PageTitle(BakeryConst.TITLE_BULKSMS)
@Secured({Role.ADMIN, "UI_AGENDA_SMS"})
public class BulkSmsView extends AbstractBakeryCrudView<Agenda> {
    private final AgendaService service;
    private final CurrentUser currentUser;
    private final WebClient webClient;
    private final OProperties properties;

    @Autowired
    public BulkSmsView(AgendaService service, CurrentUser currentUser, OProperties properties, WebClient webClient) {
        super(Agenda.class, service, new Grid<>(), createForm(currentUser, properties), currentUser);
        this.service = service;
        this.currentUser = currentUser;
        this.webClient = webClient;
        this.properties = properties;
        AgendaFileUtils.setBaseDir(properties.getAgendaFilePathUpload());
    }

    private static STTBinderCrudEditor<Agenda> createForm(CurrentUser currentUser, OProperties properties) {
        BulkSmsForm form = new BulkSmsForm(currentUser, properties);
        return new STTBinderCrudEditor<Agenda>(form.getBinder(), form);
    }

    @Override
    protected String getBasePage() {
        return BakeryConst.PAGE_BULKSMS;
    }

    @Override
    protected void setupGrid(Grid<Agenda> grid) {
        grid.addColumn(Agenda::getName).setWidth("200px").setHeader("Nombre").setFlexGrow(5);
        grid.addColumn(createNameRenderer()).setHeader("Descripción").setWidth("230px").setFlexGrow(5);
        grid.addComponentColumn(this::getIcon).setHeader("Status")
                .setTextAlign(ColumnTextAlign.CENTER)
                .setWidth("150px");
        grid.addComponentColumn(item -> {
            Button b = createDownloadButton(grid, item);
            return b;
        }).setHeader("Descargar");
    }

    private static TemplateRenderer<Agenda> createNameRenderer() {
        return TemplateRenderer.<Agenda>of("<div>[[item.userType]]<br><small><span style=\"font-size: var(--lumo-font-size-xxs); color: var(--lumo-secondary-text-color);\">[[item.userTypeOrd]]</span></small></div>")
                .withProperty("userType", Agenda::getDescription)
                .withProperty("userTypeOrd", Agenda::getFileNameOriginal);
    }

    private Icon getIcon(Agenda role) {
        String theme;
        VaadinIcon check = null;
        switch (role.getStringStatus().toLowerCase()) {
            case "In progress":
                theme = "badge primary";
                break;
            case "válido":
                check = VaadinIcon.CHECK_CIRCLE_O;
                theme = "badge success primary";
                break;
            case "con errores":
                check = VaadinIcon.CLOSE_CIRCLE_O;
                theme = "badge error primary";
                break;
            default:
                check = VaadinIcon.QUESTION_CIRCLE_O;
                theme = "badge contrast primary";
                break;
        }
        Icon confirmed = createIcon(check, role.getStatus().name());
        confirmed.getElement().getThemeList().add(theme);
        return confirmed;
    }

    private Icon createIcon(VaadinIcon vaadinIcon, String label) {
        Icon icon = vaadinIcon.create();
        icon.getStyle().set("padding", "var(--lumo-space-xs");
        // Accessible label
        icon.getElement().setAttribute("aria-label", label);
        // Tooltip
        icon.getElement().setAttribute("title", label);
        return icon;
    }

    private Span createStatusBadgeo(String status) {
        String theme;
        switch (status.toLowerCase()) {
            case "In progress":
                theme = "badge primary";
                break;
            case "Completed":
                theme = "badge success primary";
                break;
            case "con errores":
                theme = "badge error primary";
                break;
            default:
                theme = "badge contrast primary";
                break;
        }
        Span badge = new Span(status);
        badge.getElement().getThemeList().add(theme);
        return badge;
    }

    private Span createStatusBadge(String status) {
        BadgeColor theme;
        switch (status.toLowerCase()) {
            case "In progress":
                theme = BadgeColor.NORMAL;
                break;
            case "Completed":
                theme = BadgeColor.SUCCESS;
                break;
            case "con errores":
                theme = BadgeColor.ERROR_PRIMARY;
                break;
            default:
                theme = BadgeColor.CONTRAST;
                break;
        }
        return new Badge(status, theme, BadgeSize.M, BadgeShape.NORMAL);
    }

    @Override
    @Async
    protected void afterSaving(long idBeforeSave, Agenda agenda) {
        if (idBeforeSave == 0L) {
            AgendaParserRunnable parser = new AgendaParserRunnable(agenda, service, agenda.getCreatorEmail(), webClient, properties);
            parser.run();
        }
    }

    @Override
    protected BeforeSavingResponse beforeSaving(long idBeforeSave, Agenda entity) {
        BeforeSavingResponse bsr = new BeforeSavingResponse();
        bsr.setSuccess(true);
        if (idBeforeSave == 0L) {
            entity.setCreator(currentUser.getUser());
        }
        return bsr;
    }

    private Button createDownloadButton(Grid<Agenda> grid, Agenda agenda) {
        Button button = new Button(new Icon(VaadinIcon.DOWNLOAD_ALT), clickEvent -> {
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
            FileDownloadWrapper buttonWrapper = new FileDownloadWrapper(new StreamResource(fileName, () -> new ByteArrayInputStream(sb.toString().getBytes())));
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
        });
        button.addThemeVariants(ButtonVariant.LUMO_ICON, ButtonVariant.LUMO_TERTIARY);
        return button;
    }
}