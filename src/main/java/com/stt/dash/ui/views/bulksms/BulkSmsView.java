package com.stt.dash.ui.views.bulksms;

import com.stt.dash.app.security.CurrentUser;
import com.stt.dash.backend.data.Role;
import com.stt.dash.backend.data.entity.Agenda;
import com.stt.dash.backend.service.AgendaService;
import com.stt.dash.ui.MainView;
import com.stt.dash.ui.crud.AbstractBakeryCrudView;
import com.stt.dash.ui.utils.BakeryConst;
import com.vaadin.flow.component.crud.BinderCrudEditor;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;

@Route(value = BakeryConst.PAGE_BULKSMS, layout = MainView.class)
@PageTitle(BakeryConst.TITLE_BULKSMS)
@Secured({Role.ADMIN, "UI_USER"})
public class BulkSmsView extends AbstractBakeryCrudView<Agenda> {
    @Autowired
    public BulkSmsView(AgendaService service, CurrentUser currentUser) {
        super(Agenda.class, service, new Grid<>(), createForm(), currentUser);
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

    private static BinderCrudEditor<Agenda> createForm() {
        BulkSmsForm form = new BulkSmsForm();
        return new BinderCrudEditor<Agenda>(form.getBinder(), form);
    }
}