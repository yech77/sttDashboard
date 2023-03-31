package com.stt.dash.ui.views.bulksms;

import com.stt.dash.app.OProperties;
import com.stt.dash.app.security.CurrentUser;
import com.stt.dash.backend.data.Role;
import com.stt.dash.backend.data.entity.Agenda;
import com.stt.dash.backend.service.AgendaService;
import com.stt.dash.backend.thread.AgendaParserRunnable;
import com.stt.dash.backend.util.AgendaFileUtils;
import com.stt.dash.ui.MainView;
import com.stt.dash.ui.crud.AbstractBakeryCrudView;
import com.stt.dash.ui.crud.STTBinderCrudEditor;
import com.stt.dash.ui.utils.BakeryConst;
import com.stt.dash.ui.utils.BeforeSavingResponse;
import com.stt.dash.ui.views.agenda.EditAgendaView;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.crud.BinderCrudEditor;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.StreamResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.security.access.annotation.Secured;
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

    @Autowired
    public BulkSmsView(AgendaService service, CurrentUser currentUser, OProperties properties) {
        super(Agenda.class, service, new Grid<>(), createForm(currentUser, properties), currentUser);
        this.service = service;
        this.currentUser = currentUser;
        AgendaFileUtils.setBaseDir(properties.getAgendaFilePathUpload());
    }

    @Override
    protected String getBasePage() {
        return BakeryConst.PAGE_BULKSMS;
    }

    @Override
    protected void setupGrid(Grid<Agenda> grid) {
        grid.addColumn(Agenda::getName).setWidth("200px").setHeader("Nombre").setFlexGrow(5);
        grid.addColumn(Agenda::getDescription).setHeader("Descripción").setWidth("230px").setFlexGrow(5);
        grid.addColumn(role -> {
            return role.getStringStatus();
        }).setHeader("Status").setWidth("150px");
        grid.addComponentColumn(item -> {
            Button b = createDownloadButton(grid, item);
            return b;
        }).setHeader("Descargar");
    }

    private static STTBinderCrudEditor<Agenda> createForm(CurrentUser currentUser, OProperties properties) {
        BulkSmsForm form = new BulkSmsForm(currentUser, properties);
        return new STTBinderCrudEditor<Agenda>(form.getBinder(), form);
    }

    @Override
    @Async
    protected void afterSaving(long idBeforeSave, Agenda agenda) {
        if (idBeforeSave == 0l) {
            AgendaParserRunnable parser = new AgendaParserRunnable(agenda, service, agenda.getCreatorEmail());
            parser.run();
        }
    }

    @Override
    protected BeforeSavingResponse beforeSaving(long idBeforeSave, Agenda entity) {
        BeforeSavingResponse bsr = new BeforeSavingResponse();
        bsr.setSuccess(true);
        if (idBeforeSave == 0l) {
            entity.setCreator(currentUser.getUser());
        }
        return bsr;
    }

    private Button createDownloadButton(Grid<Agenda> grid, Agenda agenda) {
        @SuppressWarnings("unchecked")
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
        });
        button.addThemeVariants(ButtonVariant.LUMO_ICON, ButtonVariant.LUMO_TERTIARY);
        return button;
    }
}