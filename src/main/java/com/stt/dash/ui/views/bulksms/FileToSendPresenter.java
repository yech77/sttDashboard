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
import com.stt.dash.backend.util.ws.BalanceWebClient;
import com.stt.dash.backend.util.ws.SystemIdBalanceOResponse;
import com.stt.dash.ui.crud.EntityPresenter;
import com.stt.dash.ui.dataproviders.FilesToSendGridDataProvider;
import com.stt.dash.ui.utils.ODateUitls;
import com.stt.dash.ui.views.storefront.beans.OrderCardHeader;
import com.vaadin.flow.component.Focusable;
import com.vaadin.flow.component.HasValue;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.spring.annotation.SpringComponent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.concurrent.ConcurrentTaskScheduler;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.stream.Collectors;

import static com.stt.dash.ui.utils.BakeryConst.PAGE_BULK_STOREFRONT_ORDER_EDIT;

@SpringComponent
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class FileToSendPresenter {

    private static final String DELETE_DENIED_INCORRECT_STATUS = "No se puede borrar. Programacion ya enviada.";
    private FileToSendCardHeaderGenerator headersGenerator;
    private FileToSendFrontView view;
    private WebClient webClient;

    private final EntityPresenter<FIlesToSend, FileToSendFrontView> entityPresenter;
    private final FilesToSendGridDataProvider dataProvider;
    private final CurrentUser currentUser;
    private final FilesToSendService service;
    private final SystemIdService systemIdService;
    private final OProperties properties;

    @Autowired
    FileToSendPresenter(@Autowired FilesToSendService service,
                        @Autowired SystemIdService systemIdService,
                        @Autowired OProperties properties,
                        @Autowired FilesToSendGridDataProvider dataProvider,
                        @Autowired EntityPresenter<FIlesToSend, FileToSendFrontView> entityPresenter,
                        @Autowired WebClient webClient,
                        @Autowired CurrentUser currentUser) {
        this.entityPresenter = entityPresenter;
        this.dataProvider = dataProvider;
        this.currentUser = currentUser;
        this.service = service;
        this.systemIdService = systemIdService;
        headersGenerator = new FileToSendCardHeaderGenerator();
        headersGenerator.resetHeaderChain(false);
        dataProvider.setPageObserver(p -> headersGenerator.filesToSendRead(p.getContent()));
        this.properties = properties;
        this.webClient = webClient;
    }

    void init(FileToSendFrontView view) {
        this.entityPresenter.setView(view);
        this.view = view;
        view.getGrid().setDataProvider(dataProvider);
        view.getOpenedFileToSendEditorView().setCurrentUser(currentUser.getUser());
        view.getOpenedFileToSendEditorView().addCancelListener(e -> cancel());
        view.getOpenedFileToSendEditorView().addReviewListener(e -> review());
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
        open(entityPresenter.createNew(), true);
    }

    void cancel() {
        entityPresenter.cancel(this::close, () -> view.setOpened(true));
    }

    void closeSilently() {
        entityPresenter.close();
        view.setOpened(false);
    }

    void edit() {
        UI.getCurrent()
                .navigate(String.format(PAGE_BULK_STOREFRONT_ORDER_EDIT,
                        entityPresenter.getEntity().getId()));
    }

    void back() {
        view.setDialogElementsVisibility(true);
    }

    //
//    void review() {
////         Using collect instead of findFirst to assure all streams are
////         traversed, and every validation updates its view
////        List<HasValue<?, ?>> fields = view.validate().collect(Collectors.toList());
////        if (fields.isEmpty()) {
//        if (entityPresenter.writeEntity()) {
//            view.setDialogElementsVisibility(false);
//            view.getOpenedOrderDetails().display(entityPresenter.getEntity(), true);
//        }
////        } else if (fields.get(0) instanceof Focusable) {
////            ((Focusable<?>) fields.get(0)).focus();
////        }
//    }
    void review() {
        // Using collect instead of findFirst to assure all streams are
        // traversed, and every validation updates its view
        List<HasValue<?, ?>> fields = view.validate().collect(Collectors.toList());
        if (fields.isEmpty()) {
            if (entityPresenter.writeEntity()) {
                view.setDialogElementsVisibility(false);
                view.getOpenedOrderDetails().display(entityPresenter.getEntity(), true);
            }
        } else if (fields.get(0) instanceof Focusable) {
            ((Focusable<?>) fields.get(0)).focus();
        }
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
                view.showCreatedNotification();
                dataProvider.refreshAll();
                /* Debo repetir el close porque colocarlo fuera al final del if no se ejecuta por el mono.block. */
                close();
                /**/
                // Crea Nuevo hilo
                ScheduledExecutorService localExecutor = Executors.newSingleThreadScheduledExecutor();
                TaskScheduler scheduler = new ConcurrentTaskScheduler(localExecutor);
                /**/
                String clientCod = bySystemId.get().getSystemId();
                // Valida y Genera mensajes
                AgendaFileUtils.setBaseDir(properties.getAgendaFilePathUpload());
                scheduler.schedule(new SmsGeneratorParserRunnable(properties,
                                service,
                                e, e.getAgenda(), e.getSystemId(),
                                e.getMessageWithParam(), clientCod,
                                currentUser.getUser().getEmail(),
                                currentUser.getUser()),
                        ODateUitls.localDateTimeToDate(LocalDateTime.now().plusSeconds(5)));
                /* Disminuir saldo a usar */
                BalanceWebClient balanceWebClient = new BalanceWebClient(webClient, SystemIdBalanceOResponse.class);
                try {
                    Mono<SystemIdBalanceOResponse> mono = balanceWebClient.callSyncData(e.getSystemId(), e.getTotalSmsToSend());
                    SystemIdBalanceOResponse block = mono.block();
                    System.out.println("Aca");
                } catch (IOException ex) {
                    throw new RuntimeException(ex);
                }
            } else {
                view.showUpdatedNotification();
                dataProvider.refreshItem(e);
                close();
            }
        });
    }

    void delete() {
        EntityPresenter.CrudOperationListener<FIlesToSend> onSuccess = entity -> {
            dataProvider.refreshAll();
            /* Devolver el saldo  */
            updateBalance(entity.getSystemId(), entity.getTotalSmsToSend() * -1);
            view.showUpdatedNotification();
            close();
        };
//        EntityPresenter.CrudOnPreOperation<FIlesToSend> onBeforeDelete = entity -> {
//            return entity.getStatus() != Status.COMPLETED;
//        };
        if (entityPresenter.getEntity().getStatus() != Status.COMPLETED) {
            entityPresenter.delete(onSuccess);
        } else {
            view.showNotification(DELETE_DENIED_INCORRECT_STATUS, true);
        }
    }

    private void updateBalance(String systemid, int numOfSms) {
        BalanceWebClient balanceWebClient = new BalanceWebClient(webClient, SystemIdBalanceOResponse.class);
        try {
            Mono<SystemIdBalanceOResponse> mono = balanceWebClient.callSyncData(systemid, numOfSms);
            SystemIdBalanceOResponse block = mono.block();
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }

    }

    private void open(FIlesToSend fileToSend, boolean edit) {
        view.setDialogElementsVisibility(edit);
        view.setOpened(true);
        if (edit) {
            view.getOpenedFileToSendEditorView().read(fileToSend, entityPresenter.isNew());
        } else {
            view.getOpenedOrderDetails().display(fileToSend, false);
        }
    }

    private void close() {
        view.getOpenedFileToSendEditorView().close();
        view.setOpened(false);
        view.navigateToMainView();
        entityPresenter.close();
    }
}
