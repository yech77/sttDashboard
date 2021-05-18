package com.stt.dash.ui.views.bulksms;

import com.stt.dash.app.security.CurrentUser;
import com.stt.dash.backend.data.Role;
import com.stt.dash.backend.data.entity.Agenda;
import com.stt.dash.backend.data.entity.FIlesToSend;
import com.stt.dash.backend.service.FilterableCrudService;
import com.stt.dash.ui.MainView;
import com.stt.dash.ui.crud.AbstractBakeryCrudView;
import com.stt.dash.ui.utils.BakeryConst;
import com.vaadin.flow.component.crud.BinderCrudEditor;
import com.vaadin.flow.component.crud.CrudEditor;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.data.renderer.LocalDateTimeRenderer;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import org.springframework.security.access.annotation.Secured;

import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

//@Route(value = BakeryConst.PAGE_BULKSMS_SCHEDULER, layout = MainView.class)
//@PageTitle(BakeryConst.TITLE_BULKSMS_SCHEDULER)
//@Secured({Role.ADMIN, "UI_USER"})
public class BulkSmsSchedulerView extends AbstractBakeryCrudView<FIlesToSend> {

    public BulkSmsSchedulerView(Class<FIlesToSend> beanType, FilterableCrudService<FIlesToSend> service,
                                Grid<FIlesToSend> grid, CrudEditor<FIlesToSend> editor, CurrentUser currentUser) {
        super(beanType, service, grid, editor, currentUser);
    }

    @Override
    protected String getBasePage() {
        return BakeryConst.PAGE_BULKSMS_SCHEDULER;
    }

    @Override
    protected void setupGrid(Grid<FIlesToSend> grid) {
        grid.addColumn(FIlesToSend::getOrderName).setWidth("250px").setHeader("Nombre").setFlexGrow(5);
        grid.addColumn(FIlesToSend::getOrderDescription).setHeader("Descripción").setWidth("180px").setFlexGrow(5);
        grid.addColumn(new LocalDateTimeRenderer<>(
                        order -> order.getDateToSend().toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime(),
                        DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm")))
                .setComparator(order -> order.getDateToSend()).setHeader("Fecha para enviar")
                .setAutoWidth(true);
        grid.addColumn(role -> role.getStatus()).setHeader("Status").setWidth("150px");
    }
    private static BinderCrudEditor<FIlesToSend> createForm() {
//        BulkSmsSchedulerForm form = new BulkSmsSchedulerForm();
//        return new BinderCrudEditor<FIlesToSend>(form.getBinder(), form);
    return null;
    }
}
