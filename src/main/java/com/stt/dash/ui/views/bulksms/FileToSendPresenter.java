package com.stt.dash.ui.views.bulksms;

import com.stt.dash.app.OProperties;
import com.stt.dash.app.security.CurrentUser;
import com.stt.dash.backend.data.Status;
import com.stt.dash.backend.data.entity.FIlesToSend;
import com.stt.dash.backend.data.entity.SystemId;
import com.stt.dash.backend.service.FilesToSendService;
import com.stt.dash.backend.service.SystemIdService;
import com.stt.dash.backend.thread.SmsGeneratorParserRunnable;
import com.stt.dash.backend.util.AgendaFileUtils;
import com.stt.dash.ui.crud.EntityPresenter;
import com.stt.dash.ui.dataproviders.FilesToSendGridDataProvider;
import com.stt.dash.ui.utils.ODateUitls;
import com.stt.dash.ui.views.storefront.beans.OrderCardHeader;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.spring.annotation.SpringComponent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.concurrent.ConcurrentTaskScheduler;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

import static com.stt.dash.ui.utils.BakeryConst.PAGE_BULK_STOREFRONT_ORDER_EDIT;

@SpringComponent
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class FileToSendPresenter {

    private static final String DELETE_DENIED_INCORRECT_STATUS = "No se puede borrar. Programacion ya enviada.";
    private FileToSendCardHeaderGenerator headersGenerator;
    private FileToSendFrontView view;

    private final EntityPresenter<FIlesToSend, FileToSendFrontView> entityPresenter;
    private final FilesToSendGridDataProvider dataProvider;
    private final CurrentUser currentUser;
    private final FilesToSendService service;
    private final SystemIdService systemIdService;
    private final OProperties properties;

    @Autowired
    FileToSendPresenter(FilesToSendService service,
                        SystemIdService systemIdService,
                        OProperties properties,
                        FilesToSendGridDataProvider dataProvider,
                        EntityPresenter<FIlesToSend, FileToSendFrontView> entityPresenter, CurrentUser currentUser) {
        this.entityPresenter = entityPresenter;
        this.dataProvider = dataProvider;
        this.currentUser = currentUser;
        this.service = service;
        this.systemIdService = systemIdService;
        headersGenerator = new FileToSendCardHeaderGenerator();
        headersGenerator.resetHeaderChain(false);
        dataProvider.setPageObserver(p -> headersGenerator.filesToSendRead(p.getContent()));
        this.properties = properties;
    }

    void init(FileToSendFrontView view) {
        this.entityPresenter.setView(view);
        this.view = view;
        view.getGrid().setDataProvider(dataProvider);
        view.getOpenedOrderEditor().setCurrentUser(currentUser.getUser());
        view.getOpenedOrderEditor().addCancelListener(e -> cancel());
        view.getOpenedOrderEditor().addReviewListener(e -> review());
        view.getOpenedOrderDetails().addSaveListenter(e -> save());
        view.getOpenedOrderDetails().addCancelListener(e -> cancel());
        view.getOpenedOrderDetails().addBackListener(e -> back());
        view.getOpenedOrderDetails().addEditListener(e -> edit());
        view.getOpenedOrderDetails().addDeleteListenter(e -> delete());
//        view.getOpenedOrderDetails().addCommentListener(e -> addComment(e.getMessage()));
    }

    OrderCardHeader getHeaderByOrderId(Long id) {
        return headersGenerator.get(id);
    }

    public void filterChanged(String filter, boolean showPrevious) {
        headersGenerator.resetHeaderChain(showPrevious);
        dataProvider.setFilter(new FilesToSendGridDataProvider.FileToSendFilter(filter, showPrevious));
    }

    void onNavigation(Long id, boolean edit) {
        entityPresenter.loadEntity(id, e -> open(e, edit));
    }

    void createNewOrder() {
        System.out.println("Llegue a crearNewOrder...");
        open(entityPresenter.createNew(), true);
    }

    void cancel() {
        entityPresenter.cancel(this::close, () -> view.setOpened(true));
    }

    void closeSilently() {
        System.out.println("Llegue a closeSilently...");
        entityPresenter.close();
        view.setOpened(false);
    }

    void edit() {
        System.out.println("Llegue edit...");
        UI.getCurrent()
                .navigate(String.format(PAGE_BULK_STOREFRONT_ORDER_EDIT,
                        entityPresenter.getEntity().getId()));
    }

    void back() {
        System.out.println("Llegue a back...");
        view.setDialogElementsVisibility(true);
    }

    void review() {
        System.out.println("Llegue a Review...");
//         Using collect instead of findFirst to assure all streams are
//         traversed, and every validation updates its view
//        List<HasValue<?, ?>> fields = view.validate().collect(Collectors.toList());
//        if (fields.isEmpty()) {
        if (entityPresenter.writeEntity()) {
            view.setDialogElementsVisibility(false);
            view.getOpenedOrderDetails().display(entityPresenter.getEntity(), true);
        }
//        } else if (fields.get(0) instanceof Focusable) {
//            ((Focusable<?>) fields.get(0)).focus();
//        }
    }

    void save() {
        Optional<SystemId> bySystemId = systemIdService.findBySystemId(entityPresenter.getEntity().getSystemId());
        /* Solo llama a salvar si encuentra el cliente del system id */
        if (!bySystemId.isPresent()) {
            view.showNotification("No se puedo crear la programacion. Si el problema persiste llame a su Administrador", true);
            return;
        }
        entityPresenter.save(e -> {
            /* Si se salva correctamente se ejecuta este codigo. */
            if (entityPresenter.isNew()) {
                System.out.println("Llegue a save is New...");
                view.showCreatedNotification();
                dataProvider.refreshAll();
                /**/
                // Crea Nuevo hilo
                ScheduledExecutorService localExecutor = Executors.newSingleThreadScheduledExecutor();
                TaskScheduler scheduler = new ConcurrentTaskScheduler(localExecutor);
                /**/
                String clientCod = bySystemId.get().getSystemId();
                // Valida y Genera mensajes
                System.out.println("Generando mensajes en 5 segundos...");
                AgendaFileUtils.setBaseDir(properties.getAgendaFilePathUpload());
                scheduler.schedule(new SmsGeneratorParserRunnable(properties,
                                service,
                                e, e.getAgenda(), e.getSystemId(),
                                e.getMessageWithParam(), clientCod,
                                currentUser.getUser().getEmail(),
                                currentUser.getUser()),
                        ODateUitls.localDateTimeToDate(LocalDateTime.now().plusSeconds(5)));
                /**/
            } else {
                System.out.println("Llegue a save not is New...");
                view.showUpdatedNotification();
                dataProvider.refreshItem(e);
            }
            close();
        });
    }

    void delete() {
        EntityPresenter.CrudOperationListener<FIlesToSend>
                onSuccess = entity -> {
            dataProvider.refreshAll();
            view.showUpdatedNotification();
            close();
        };
        EntityPresenter.CrudOnPreOperation<FIlesToSend> onBeforeDelete = entity -> {
            return entity.getStatus() != Status.COMPLETED;
        };
        if (entityPresenter.getEntity().getStatus() != Status.COMPLETED) {
            entityPresenter.delete(onSuccess);
        } else {
            view.showNotification(DELETE_DENIED_INCORRECT_STATUS, true);
        }
    }

    private void open(FIlesToSend order, boolean edit) {
        System.out.println("Llegue a Open...");
        view.setDialogElementsVisibility(edit);
        view.setOpened(true);
        if (edit) {
            view.getOpenedOrderEditor().read(order, entityPresenter.isNew());
        } else {
            view.getOpenedOrderDetails().display(order, false);
        }
    }

    private void close() {
        System.out.println("FileToSendPresenter: Close");
        view.getOpenedOrderEditor().close();
        view.setOpened(false);
        view.navigateToMainView();
        entityPresenter.close();
    }
}
