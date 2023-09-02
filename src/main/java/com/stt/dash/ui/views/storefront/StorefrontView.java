package com.stt.dash.ui.views.storefront;

import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import org.springframework.beans.factory.annotation.Autowired;

import com.vaadin.flow.component.HasValue;
import com.vaadin.flow.component.Tag;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.confirmdialog.ConfirmDialog;
import com.vaadin.flow.component.dependency.JsModule;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.polymertemplate.Id;
import com.vaadin.flow.component.polymertemplate.PolymerTemplate;
import com.vaadin.flow.data.binder.ValidationException;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouteAlias;
import com.vaadin.flow.templatemodel.TemplateModel;
import com.stt.dash.app.HasLogger;
import com.stt.dash.backend.data.entity.Order;
import com.stt.dash.backend.data.entity.util.EntityUtil;
import com.stt.dash.ui.MainView;
import com.stt.dash.ui.components.SearchBar;
import com.stt.dash.ui.utils.BakeryConst;
import com.stt.dash.ui.views.EntityView;
import com.stt.dash.ui.views.orderedit.OrderDetails;
import com.stt.dash.ui.views.orderedit.OrderEditor;

import static com.stt.dash.ui.utils.BakeryConst.EDIT_SEGMENT;
import static com.stt.dash.ui.utils.BakeryConst.ORDER_ID;

@Tag("storefront-view")
@JsModule("./src/views/storefront/storefront-view.js")
@Route(value = BakeryConst.PAGE_STOREFRONT_ORDER_TEMPLATE, layout = MainView.class)
@RouteAlias(value = BakeryConst.PAGE_STOREFRONT_ORDER_EDIT_TEMPLATE, layout = MainView.class)
@PageTitle(BakeryConst.TITLE_BULK_SCHEDULER)
public class StorefrontView extends PolymerTemplate<TemplateModel>
        implements HasLogger, BeforeEnterObserver, EntityView<Order> {

    @Id("search")
    private SearchBar searchBar;

    @Id("grid")
    private Grid<Order> grid;

    @Id("dialog")
    private Dialog dialog;

    private ConfirmDialog confirmation;

    private final OrderEditor orderEditor;

    private final OrderDetails orderDetails = new OrderDetails();

    private final OrderPresenter presenter;

    @Autowired
    public StorefrontView(OrderPresenter presenter, OrderEditor orderEditor) {
        this.presenter = presenter;
        this.orderEditor = orderEditor;

        searchBar.setActionText("New order");
        searchBar.setCheckboxText("Show past orders");
        searchBar.setPlaceHolder("Search");

        grid.setSelectionMode(Grid.SelectionMode.NONE);

        grid.addColumn(OrderCard.getTemplate()
                .withProperty("orderCard", OrderCard::create)
                .withProperty("header", order -> presenter.getHeaderByOrderId(order.getId()))
                .withEventHandler("cardClick",
                        order -> UI.getCurrent().navigate(BakeryConst.PAGE_STOREFRONT + "/" + order.getId())));

        getSearchBar().addFilterChangeListener(
                e -> presenter.filterChanged(getSearchBar().getFilter(), getSearchBar().isCheckboxChecked()));
        getSearchBar().addActionClickListener(e -> presenter.createNewOrder());

        presenter.init(this);

        dialog.addDialogCloseActionListener(e -> presenter.cancel());
    }

    @Override
    public ConfirmDialog getConfirmDialog() {
        return confirmation;
    }

    @Override
    public void setConfirmDialog(ConfirmDialog confirmDialog) {
        this.confirmation = confirmDialog;
    }

    void setOpened(boolean opened) {
        dialog.setOpened(opened);
    }

    @Override
    public void beforeEnter(BeforeEnterEvent event) {
        Optional<Long> orderId = event.getRouteParameters().getLong(ORDER_ID);
        if (orderId.isPresent()) {
            boolean isEditView = EDIT_SEGMENT.equals(getLastSegment(event));
            presenter.onNavigation(orderId.get(), isEditView);
        } else if (dialog.isOpened()) {
            presenter.closeSilently();
        }
    }

    void navigateToMainView() {
        getUI().ifPresent(ui -> ui.navigate(BakeryConst.PAGE_STOREFRONT));
    }

    @Override
    public boolean isDirty() {
        return orderEditor.hasChanges() || orderDetails.isDirty();
    }

    @Override
    public void write(Order entity) throws ValidationException {
        orderEditor.write(entity);
    }

    public Stream<HasValue<?, ?>> validate() {
        return orderEditor.validate();
    }

    SearchBar getSearchBar() {
        return searchBar;
    }

    OrderEditor getOpenedOrderEditor() {
        return orderEditor;
    }

    OrderDetails getOpenedOrderDetails() {
        return orderDetails;
    }

    Grid<Order> getGrid() {
        return grid;
    }

    @Override
    public void clear() {
        orderDetails.setDirty(false);
        orderEditor.clear();
    }

    void setDialogElementsVisibility(boolean editing) {
        dialog.add(editing ? orderEditor : orderDetails);
        orderEditor.setVisible(editing);
        orderDetails.setVisible(!editing);
    }

    @Override
    public String getEntityName() {
        return EntityUtil.getName(Order.class);
    }

    private String getLastSegment(BeforeEnterEvent event) {
        List<String> segments = event.getLocation().getSegments();
        return segments.get(segments.size() - 1);
    }
}
