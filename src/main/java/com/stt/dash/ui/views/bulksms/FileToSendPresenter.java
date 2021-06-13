package com.stt.dash.ui.views.bulksms;

import com.stt.dash.app.security.CurrentUser;
import com.stt.dash.backend.data.entity.FIlesToSend;
import com.stt.dash.backend.data.entity.Order;
import com.stt.dash.backend.service.FilesToSendService;
import com.stt.dash.ui.crud.EntityPresenter;
import com.stt.dash.ui.dataproviders.FilesToSendGridDataProvider;
import com.stt.dash.ui.views.storefront.beans.OrderCardHeader;
import com.vaadin.flow.component.Focusable;
import com.vaadin.flow.component.HasValue;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.spring.annotation.SpringComponent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;

import java.util.List;
import java.util.stream.Collectors;

import static com.stt.dash.ui.utils.BakeryConst.PAGE_STOREFRONT_ORDER_EDIT;

@SpringComponent
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class FileToSendPresenter {

    private FileToSendCardHeaderGenerator headersGenerator;
    private FileToSendFrontView view;

    private final EntityPresenter<FIlesToSend, FileToSendFrontView> entityPresenter;
    private final FilesToSendGridDataProvider dataProvider;
    private final CurrentUser currentUser;
    private final FilesToSendService service;

    @Autowired
    FileToSendPresenter(FilesToSendService service, FilesToSendGridDataProvider dataProvider,
                        EntityPresenter<FIlesToSend, FileToSendFrontView> entityPresenter, CurrentUser currentUser) {
        this.entityPresenter = entityPresenter;
        this.dataProvider = dataProvider;
        this.currentUser = currentUser;
        this.service = service;
        headersGenerator = new FileToSendCardHeaderGenerator();
        headersGenerator.resetHeaderChain(false);
        dataProvider.setPageObserver(p->headersGenerator.ordersRead(p.getContent()));
    }

    void init(FileToSendFrontView view) {
        this.entityPresenter.setView(view);
        this.view = view;
        view.getGrid().setDataProvider(dataProvider);
        view.getOpenedOrderEditor().setCurrentUser(currentUser.getUser());
        view.getOpenedOrderEditor().addCancelListener(e -> cancel());
        view.getOpenedOrderEditor().addReviewListener(e -> review());
//        view.getOpenedOrderDetails().addSaveListenter(e -> save());
//        view.getOpenedOrderDetails().addCancelListener(e -> cancel());
//        view.getOpenedOrderDetails().addBackListener(e -> back());
//        view.getOpenedOrderDetails().addEditListener(e -> edit());
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
                .navigate(String.format(PAGE_STOREFRONT_ORDER_EDIT,
                        entityPresenter.getEntity().getId()));
    }

    void back() {
        view.setDialogElementsVisibility(true);
    }

    void review() {
        // Using collect instead of findFirst to assure all streams are
        // traversed, and every validation updates its view
//        List<HasValue<?, ?>> fields = view.validate().collect(Collectors.toList());
//        if (fields.isEmpty()) {
//            if (entityPresenter.writeEntity()) {
//                view.setDialogElementsVisibility(false);
//                view.getOpenedOrderDetails().display(entityPresenter.getEntity(), true);
//            }
//        } else if (fields.get(0) instanceof Focusable) {
//            ((Focusable<?>) fields.get(0)).focus();
//        }
    }
    void save() {
        entityPresenter.save(e -> {
            if (entityPresenter.isNew()) {
                view.showCreatedNotification();
                dataProvider.refreshAll();
            } else {
                view.showUpdatedNotification();
                dataProvider.refreshItem(e);
            }
            close();
        });

    }

//    void addComment(String comment) {
//        if (entityPresenter.executeUpdate(e -> orderService.addComment(currentUser.getUser(), e, comment))) {
//            // You can only add comments when in view mode, so reopening in that state.
//            open(entityPresenter.getEntity(), false);
//        }
//    }

    private void open(FIlesToSend order, boolean edit) {
        view.setDialogElementsVisibility(edit);
        view.setOpened(true);

        if (edit) {
            view.getOpenedOrderEditor().read(order, entityPresenter.isNew());
        } else {
//            view.getOpenedOrderDetails().display(order, false);
        }
    }

    private void close() {
//        view.getOpenedOrderEditor().close();
        view.setOpened(false);
        view.navigateToMainView();
        entityPresenter.close();
    }
}
