package com.stt.dash.ui.views.bulksms;

import com.stt.dash.app.OProperties;
import com.stt.dash.app.security.CurrentUser;
import com.stt.dash.backend.data.Role;
import com.stt.dash.backend.data.entity.Agenda;
import com.stt.dash.backend.service.AgendaService;
import com.stt.dash.backend.thread.AgendaParserRunnable;
import com.stt.dash.ui.MainView;
import com.stt.dash.ui.crud.AbstractBakeryCrudView;
import com.stt.dash.ui.crud.CrudEntityPresenter;
import com.stt.dash.ui.utils.BakeryConst;
import com.vaadin.flow.component.crud.BinderCrudEditor;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.security.access.annotation.Secured;

@Route(value = BakeryConst.PAGE_BULKSMS, layout = MainView.class)
@PageTitle(BakeryConst.TITLE_BULKSMS)
@Secured({Role.ADMIN, "UI_USER"})
public class BulkSmsView extends AbstractBakeryCrudView<Agenda> {
    private final AgendaService service;
    private final CurrentUser currentUser;

    @Autowired
    public BulkSmsView(AgendaService service, CurrentUser currentUser, OProperties properties) {
        super(Agenda.class, service, new Grid<>(), createForm(currentUser, properties), currentUser);
        this.service = service;
        this.currentUser = currentUser;
    }

    @Override
    protected String getBasePage() {
        return BakeryConst.PAGE_BULKSMS;
    }

    @Override
    protected void setupGrid(Grid<Agenda> grid) {
        grid.addColumn(Agenda::getName).setWidth("250px").setHeader("Nombre").setFlexGrow(5);
        grid.addColumn(Agenda::getDescription).setHeader("Descripción").setWidth("180px").setFlexGrow(5);
        grid.addColumn(role -> {
            return role.getStringStatus();
        }).setHeader("Status").setWidth("150px");
    }

    private static BinderCrudEditor<Agenda> createForm(CurrentUser currentUser, OProperties properties) {
        BulkSmsForm form = new BulkSmsForm(currentUser, properties);
        return new BinderCrudEditor<Agenda>(form.getBinder(), form);
    }

    @Override
    @Async
    protected void afterSaving(long idBeforeSave, Agenda agenda) {
        if (idBeforeSave==0l) {
            AgendaParserRunnable parser = new AgendaParserRunnable(agenda, service, agenda.getCreatorEmail());
            parser.run();
        }
//        Thread t = new Thread(parser);
//        t.start();

    }

    @Override
    protected void beforeSaving(long idBeforeSave, Agenda entity) {
        if (idBeforeSave==0l) {
            entity.setCreator(currentUser.getUser());
        }
    }
}