package com.stt.dash.ui.views.bulksms;

import com.stt.dash.app.OProperties;
import com.stt.dash.app.security.CurrentUser;
import com.stt.dash.app.session.ListGenericBean;
import com.stt.dash.app.session.SetGenericBean;
import com.stt.dash.backend.data.Role;
import com.stt.dash.backend.data.entity.Agenda;
import com.stt.dash.backend.data.entity.FIlesToSend;
import com.stt.dash.backend.data.entity.SystemId;
import com.stt.dash.backend.data.entity.User;
import com.stt.dash.backend.repositories.SystemIdRepository;
import com.stt.dash.backend.service.AgendaService;
import com.stt.dash.backend.service.FilesToSendService;
import com.stt.dash.backend.thread.SmsGeneratorParserRunnable;
import com.stt.dash.backend.util.AgendaFileUtils;
import com.stt.dash.ui.MainView;
import com.stt.dash.ui.crud.AbstractBakeryCrudView;
import com.stt.dash.ui.utils.BakeryConst;
import com.stt.dash.ui.utils.ODateUitls;
import com.vaadin.flow.component.crud.BinderCrudEditor;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.data.renderer.LocalDateTimeRenderer;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.concurrent.ConcurrentTaskScheduler;
import org.springframework.security.access.annotation.Secured;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

@Route(value = BakeryConst.PAGE_BULKSMS_SCHEDULER, layout = MainView.class)
@PageTitle(BakeryConst.TITLE_BULKSMS_SCHEDULER)
@Secured({Role.ADMIN, "UI_USER"})
public class BulkSmsSchedulerView extends AbstractBakeryCrudView<FIlesToSend> {

    private static BulkSmsSchedulerForm form;
    private SystemIdRepository systemid_repo;
    private final OProperties properties;
    private final FilesToSendService files_service;
    private final String userEmail;
    public BulkSmsSchedulerView(
            OProperties properties, SystemIdRepository systemid_repo,
            AgendaService agendaService,
            FilesToSendService service,
            CurrentUser currentUser,
            @Qualifier("getUserMeAndChildren") ListGenericBean<User> userChildrenList,
            SetGenericBean<SystemId> userSystemIdSet) {
        super(FIlesToSend.class, null, new Grid<FIlesToSend>(), createForm(currentUser, agendaService, userSystemIdSet, userChildrenList), currentUser);
        this.files_service = service;
        this.properties=properties;
        this.userEmail = currentUser.getUser().getEmail();
        this.systemid_repo=systemid_repo;
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
        List<Agenda> agendaList = agendaService.getAllValidAgendasInFamily(userChildren.getList());
        form = new BulkSmsSchedulerForm(agendaList, userSystemIdSet.getSet(), currentUser);
        return new BinderCrudEditor<FIlesToSend>(form.getBinder(), form);
    }

    @Override
    protected boolean beforeSaving(long idBeforeSave, FIlesToSend entity) {
        if (idBeforeSave==0){
            entity.setFileName(form.agendaCombo.getValue().getFileName());
        }
        return true;
    }

    @Override
    protected void afterSaving(long idBeforeSave, FIlesToSend entity) {

        // Crea Nuevo hilo
        ScheduledExecutorService localExecutor = Executors.newSingleThreadScheduledExecutor();
        TaskScheduler scheduler = new ConcurrentTaskScheduler(localExecutor);
        /**/
        List<SystemId> ids = systemid_repo.findBySystemId(form.systemIdCombo.getValue());
        if (ids.size() < 1) {
            System.out.println("SysId not found!!!");
        } else {
            String clientCod = ids.get(0).getClient().getClientCod();
            // Valida y Genera mensajes
            System.out.println("Generando mensajes en 5 segundos...");
            AgendaFileUtils.setBaseDir(properties.getAgendaFilePathUpload());
//            FIlesToSend fIlesToSend = new FIlesToSend(form.orderName.getValue(), form.orderDescription.getValue(), form.agendaCombo.getValue().getFileName(),
//                    entity.getDateToSend(), form.systemIdCombo.getValue());
            scheduler.schedule(new SmsGeneratorParserRunnable(properties,
                            this.files_service,
                            entity, form.agendaCombo.getValue(), form.systemIdCombo.getValue(),
                            form.messageBox.getValue(), clientCod, userEmail),
                    ODateUitls.localDateTimeToDate(LocalDateTime.now().plusSeconds(5)));

            getUI().get().navigate("smsOrderView");
        }
    }

}
