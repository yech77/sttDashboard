package com.stt.dash.ui.views.storefront;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;

import com.vaadin.flow.component.Focusable;
import com.vaadin.flow.component.HasValue;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.spring.annotation.SpringComponent;
import com.stt.dash.app.security.CurrentUser;
import com.stt.dash.backend.data.entity.Order;
import com.stt.dash.backend.service.OrderService;
import com.stt.dash.ui.crud.EntityPresenter;
import com.stt.dash.ui.dataproviders.OrdersGridDataProvider;
import com.stt.dash.ui.dataproviders.OrdersGridDataProvider.OrderFilter;
import com.stt.dash.ui.views.storefront.beans.OrderCardHeader;

import static com.stt.dash.ui.utils.BakeryConst.PAGE_STOREFRONT_ORDER_EDIT;

@SpringComponent
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class OrderPresenter {

	private OrderCardHeaderGenerator headersGenerator;
	private StorefrontView view;

	private final EntityPresenter<Order, StorefrontView> entityPresenter;
	private final OrdersGridDataProvider dataProvider;
	private final CurrentUser currentUser;
	private final OrderService orderService;

	@Autowired
	OrderPresenter(OrderService orderService, OrdersGridDataProvider dataProvider,
			EntityPresenter<Order, StorefrontView> entityPresenter, CurrentUser currentUser) {
		this.orderService = orderService;
		this.entityPresenter = entityPresenter;
		this.dataProvider = dataProvider;
		this.currentUser = currentUser;
		headersGenerator = new OrderCardHeaderGenerator();
		headersGenerator.resetHeaderChain(false);
		dataProvider.setPageObserver(p -> headersGenerator.ordersRead(p.getContent()));
	}

	void init(StorefrontView view) {
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
		view.getOpenedOrderDetails().addCommentListener(e -> addComment(e.getMessage()));
	}

	OrderCardHeader getHeaderByOrderId(Long id) {
		return headersGenerator.get(id);
	}

	public void filterChanged(String filter, boolean showPrevious) {
		headersGenerator.resetHeaderChain(showPrevious);
		dataProvider.setFilter(new OrderFilter(filter, showPrevious));
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
		System.out.println("Llegue a P- EDIT...");
        UI.getCurrent()
                .navigate(String.format(PAGE_STOREFRONT_ORDER_EDIT,
                        entityPresenter.getEntity().getId()));
	}

	void back() {

		System.out.println("Llegue a P- BACK...");
		view.setDialogElementsVisibility(true);
	}

	void review() {
		System.out.println("Llegue a P- REVIEW...");
		// Using collect instead of findFirst to assure all streams are
		// traversed, and every validation updates its view
		List<HasValue<?, ?>> fields = view.validate().collect(Collectors.toList());
		if (fields.isEmpty()) {
			System.out.println("Llegue a P- REVIEW - ISEMPTY...");
			if (entityPresenter.writeEntity()) {
				view.setDialogElementsVisibility(false);
				view.getOpenedOrderDetails().display(entityPresenter.getEntity(), true);
			}
		} else if (fields.get(0) instanceof Focusable) {
			System.out.println("Llegue a P- REVIEW...");
			((Focusable<?>) fields.get(0)).focus();
		}
	}

	void save() {
		System.out.println("Llegue a P- SAVE...");
		entityPresenter.save(e -> {
			if (entityPresenter.isNew()) {

				System.out.println("Llegue a P- SAVE - ISNEW...");
				view.showCreatedNotification();
				dataProvider.refreshAll();
			} else {
				System.out.println("Llegue a P- SAVE - !ISNEW...");
				view.showUpdatedNotification();
				dataProvider.refreshItem(e);
			}
			close();
		});

	}

	void addComment(String comment) {
		if (entityPresenter.executeUpdate(e -> orderService.addComment(currentUser.getUser(), e, comment))) {
			// You can only add comments when in view mode, so reopening in that state.
			open(entityPresenter.getEntity(), false);
		}
	}

	private void open(Order order, boolean edit) {
		System.out.println("Llegue a P- OPEN...");
		view.setDialogElementsVisibility(edit);
		view.setOpened(true);

		if (edit) {
			System.out.println("Llegue a P- OPEN...TRUE");
			view.getOpenedOrderEditor().read(order, entityPresenter.isNew());
		} else {
			System.out.println("Llegue a P- OPEN...FALSE");
			view.getOpenedOrderDetails().display(order, false);
		}
	}

	private void close() {
		System.out.println("Llegue a P- CLOSE...");
		view.getOpenedOrderEditor().close();
		view.setOpened(false);
		view.navigateToMainView();
		entityPresenter.close();
	}
}
