package com.stt.dash.ui.views.bulksms;

import com.stt.dash.app.security.CurrentUser;
import com.stt.dash.backend.data.entity.Agenda;
import com.stt.dash.backend.service.FilterableCrudService;
import com.stt.dash.ui.crud.AbstractBakeryCrudView;
import com.vaadin.flow.component.crud.CrudEditor;
import com.vaadin.flow.component.grid.Grid;

public class BulkSmsSchedulerView extends AbstractBakeryCrudView<Agenda> {

    public BulkSmsSchedulerView(Class<Agenda> beanType, FilterableCrudService<Agenda> service, Grid<Agenda> grid, CrudEditor<Agenda> editor, CurrentUser currentUser) {
        super(beanType, service, grid, editor, currentUser);
    }

    @Override
    protected String getBasePage() {
        return null;
    }

    @Override
    protected void setupGrid(Grid<Agenda> grid) {

    }
}
