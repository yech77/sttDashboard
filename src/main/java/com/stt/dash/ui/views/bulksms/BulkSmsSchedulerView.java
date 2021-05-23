package com.stt.dash.ui.views.bulksms;

import com.stt.dash.app.security.CurrentUser;
import com.stt.dash.app.session.ListGenericBean;
import com.stt.dash.app.session.SetGenericBean;
import com.stt.dash.backend.data.Role;
import com.stt.dash.backend.data.entity.Agenda;
import com.stt.dash.backend.data.entity.FIlesToSend;
import com.stt.dash.backend.data.entity.SystemId;
import com.stt.dash.backend.data.entity.User;
import com.stt.dash.backend.service.AgendaService;
import com.stt.dash.backend.service.FilesToSendService;
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
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.access.annotation.Secured;

import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Route(value = BakeryConst.PAGE_BULKSMS_SCHEDULER, layout = MainView.class)
@PageTitle(BakeryConst.TITLE_BULKSMS_SCHEDULER)
@Secured({Role.ADMIN, "UI_USER"})
public class BulkSmsSchedulerView extends AbstractBakeryCrudView<FIlesToSend> {

    public BulkSmsSchedulerView(AgendaService agendaService,
                                FilesToSendService service,
                                CurrentUser currentUser,
                                @Qualifier("getUserMeAndChildren") ListGenericBean<User> userChildrenList,
                                SetGenericBean<SystemId> userSystemIdSet) {
        super(FIlesToSend.class, service, new Grid<FIlesToSend>(), createForm(currentUser, agendaService, userSystemIdSet, userChildrenList), currentUser);
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
    private static BinderCrudEditor<FIlesToSend> createForm(CurrentUser currentUser,
                                                            AgendaService agendaService,
                                                            SetGenericBean<SystemId> userSystemIdSet,
                                                            ListGenericBean userChildren) {
        List<Agenda> agendaList = agendaService.getAllValidAgendasInFamily(userChildren.getSet());
        BulkSmsSchedulerForm form = new BulkSmsSchedulerForm(agendaList, userSystemIdSet.getSet(), currentUser);
        return new BinderCrudEditor<FIlesToSend>(form.getBinder(), form);
    }
}
