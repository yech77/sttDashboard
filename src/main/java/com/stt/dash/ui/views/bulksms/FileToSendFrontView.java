package com.stt.dash.ui.views.bulksms;

import com.stt.dash.app.HasLogger;
import com.stt.dash.backend.data.Role;
import com.stt.dash.backend.data.entity.FIlesToSend;
import com.stt.dash.backend.data.entity.util.EntityUtil;
import com.stt.dash.ui.MainView;
import com.stt.dash.ui.components.SearchBar;
import com.stt.dash.ui.utils.BakeryConst;
import com.stt.dash.ui.views.EntityView;
import com.vaadin.flow.component.Tag;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.confirmdialog.ConfirmDialog;
import com.vaadin.flow.component.dependency.JsModule;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.littemplate.LitTemplate;
import com.vaadin.flow.component.polymertemplate.Id;
import com.vaadin.flow.data.binder.ValidationException;
import com.vaadin.flow.router.*;
import org.springframework.security.access.annotation.Secured;

import java.util.List;
import java.util.Optional;

import static com.stt.dash.ui.utils.BakeryConst.EDIT_SEGMENT;
import static com.stt.dash.ui.utils.BakeryConst.ORDER_ID;

@Tag("file-to-sendfront-view")
@JsModule("./src/views/bulksms/file-to-sendfront-view.ts")
@Route(value = BakeryConst.PAGE_BULK_STOREFRONT_ORDER_TEMPLATE, layout = MainView.class)
@RouteAlias(value = BakeryConst.PAGE_BULK_STOREFRONT_ORDER_EDIT_TEMPLATE, layout = MainView.class)
@Secured({Role.ADMIN, "UI_PROGRAM_SMS"})
@PageTitle(BakeryConst.TITLE_STOREFRONT)
public class FileToSendFrontView extends LitTemplate implements HasLogger, BeforeEnterObserver, EntityView<FIlesToSend> {

    @Id("search")
    private SearchBar searchBar;

    @Id("grid")
    private Grid<FIlesToSend> grid;

    @Id("dialog")
    private Dialog dialog;

    private ConfirmDialog confirmation;

    private final FileToSendEditor fileToSendEditor;

    private final FileToSendDetails orderDetails = new FileToSendDetails();

    private final FileToSendPresenter presenter;

    public FileToSendFrontView(FileToSendEditor fileToSendEditor, FileToSendPresenter presenter) {
        this.fileToSendEditor = fileToSendEditor;
        this.presenter = presenter;

        searchBar.setActionText("Programar nuevo envio");
        searchBar.setCheckboxText("Ver envios anteriores");
        searchBar.setPlaceHolder("Buscar");


        grid.setSelectionMode(Grid.SelectionMode.NONE);

        grid.addColumn(FileToSendCard.getTemplate()
                .withProperty("orderCard", FileToSendCard::create)
                .withProperty("header", fileToSend -> presenter.getHeaderByOrderId(fileToSend.getId()))
                .withEventHandler("cardClick",
                        order -> UI.getCurrent().navigate(BakeryConst.PAGE_BULK_STOREFRONT + "/" + order.getId())));

        getSearchBar().addFilterChangeListener(
                e -> presenter.filterChanged(getSearchBar().getFilter(), getSearchBar().isCheckboxChecked()));
        getSearchBar().addActionClickListener(e -> presenter.createNewOrder());

        presenter.init(this);

        dialog.addDialogCloseActionListener(e -> presenter.cancel());
    }


    @Override
    public boolean isDirty() {
        System.out.println("******* DIRTY ************ filetoSend: "+ fileToSendEditor.hasChanges() +
                "orderDetails: " + orderDetails.isDirty());
        return fileToSendEditor.hasChanges() || orderDetails.isDirty();
    }

    @Override
    public void clear() {
        orderDetails.setDirty(false);
        fileToSendEditor.clear();
    }

    @Override
    public void write(FIlesToSend entity) throws ValidationException {
        fileToSendEditor.write(entity);
    }

    @Override
    public void setConfirmDialog(ConfirmDialog confirmDialog) {
        this.confirmation = confirmDialog;
    }

    @Override
    public ConfirmDialog getConfirmDialog() {
        return confirmation;
    }

    FileToSendEditor getOpenedOrderEditor() {
        return fileToSendEditor;
    }

    FileToSendDetails getOpenedOrderDetails() {
        return orderDetails;
    }


    Grid<FIlesToSend> getGrid() {
        return grid;
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

    SearchBar getSearchBar() {
        return searchBar;
    }

    @Override
    public String getEntityName() {
        return EntityUtil.getName(FIlesToSend.class);
    }

    private String getLastSegment(BeforeEnterEvent event) {
        List<String> segments = event.getLocation().getSegments();
        return segments.get(segments.size() - 1);
    }
    void setOpened(boolean opened) {
        dialog.setOpened(opened);
    }

    void setDialogElementsVisibility(boolean editing) {
        System.out.println("SET_DIALOG_ELEMETN: EDITING " + editing);
        dialog.add(editing ? fileToSendEditor : orderDetails);
        fileToSendEditor.setVisible(editing);
        orderDetails.setVisible(!editing);
    }

//    public Stream<HasValue<?, ?>> validate() {
//        return orderEditor.validate();
//    }

    void navigateToMainView() {
        getUI().ifPresent(ui -> ui.navigate(BakeryConst.PAGE_BULK_STOREFRONT));
    }
}
