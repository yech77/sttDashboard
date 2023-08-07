package com.stt.dash.ui.views.balance;

import com.stt.dash.app.security.CurrentUser;
import com.stt.dash.app.session.ListGenericBean;
import com.stt.dash.backend.data.Role;
import com.stt.dash.backend.data.entity.SystemId;
import com.stt.dash.backend.service.SystemIdBalanceWebClientService;
import com.stt.dash.backend.util.ws.SystemIdBalanceOResponse;
import com.stt.dash.ui.MainView;
import com.stt.dash.ui.utils.ODateUitls;
import com.stt.dash.uiv2.components.FlexBoxLayout;
import com.stt.dash.uiv2.components.detailsdrawer.DetailsDrawer;
import com.stt.dash.uiv2.components.detailsdrawer.DetailsDrawerFooter;
import com.stt.dash.uiv2.components.detailsdrawer.DetailsDrawerHeader;
import com.stt.dash.views.ViewFrame;
import com.vaadin.componentfactory.multiselect.MultiComboBox;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.grid.ColumnTextAlign;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.provider.ListDataProvider;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.router.Route;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.access.annotation.Secured;

import java.text.NumberFormat;
import java.util.Locale;

@Route(value = "balance", layout = MainView.class)
@Secured({Role.ADMIN, "UI_BALANCE"})
public class BalanceView extends ViewFrame {
    private final SystemIdBalanceWebClientService balanceWebClientService;
    private final BalancePresenter presenter;
    private MultiComboBox<SystemId> systemIdMultiComboBox;
    private Grid<SystemIdBalanceOResponse> grid;

    private DetailsDrawer detailsDrawer;
    private DetailsDrawerHeader detailsDrawerHeader;
    private DetailsDrawerFooter detailsDrawerFooter;
    private TextField searchTextField;
    private final NumberFormat integerInstance = NumberFormat.getIntegerInstance(new Locale("es", "ES"));

    public BalanceView(SystemIdBalanceWebClientService balanceWebClientService,
                       @Qualifier("getUserSystemIdString") ListGenericBean<String> stringListGenericBean,
                       CurrentUser currentUser) {
        this.balanceWebClientService = balanceWebClientService;
        setViewContent(createContent());
        presenter = new BalancePresenter(balanceWebClientService, stringListGenericBean, this);
        searchTextField.setClearButtonVisible(true);
        searchTextField.setWidth("50%");
        searchTextField.setPlaceholder("Ingrese credencial a buscar");
        searchTextField.setPrefixComponent(new Icon(VaadinIcon.SEARCH));
        searchTextField.setValueChangeMode(ValueChangeMode.EAGER);
        searchTextField.addValueChangeListener(e -> presenter.applyFilter(searchTextField.getValue()));
    }

    private Component createContent() {
        searchTextField = new TextField();
        VerticalLayout verticalLayout = new VerticalLayout(searchTextField, createGrid());
        FlexBoxLayout flexBoxLayout = new FlexBoxLayout(verticalLayout);
        return flexBoxLayout;
    }

    private Component createGrid() {
        grid = new Grid<>();
        grid.addColumn(v -> {
                    return v.getSystemid().getSystem_id();
                })
                .setSortable(true)
                .setHeader("Credencial");
        grid.addColumn(s -> integerInstance.format(s.getBalance_credit()))
                .setSortable(true)
                .setTextAlign(ColumnTextAlign.END)
                .setHeader("Credito");
        grid.addColumn(s -> integerInstance.format(s.getCredit_used()))
                .setSortable(true)
                .setTextAlign(ColumnTextAlign.END)
                .setHeader("Usado");
        grid.addColumn(s -> integerInstance.format(s.getLocked_balance()))
                .setSortable(true)
                .setTextAlign(ColumnTextAlign.END)
                .setHeader("Reservado");
        grid.addColumn(v -> integerInstance.format(v.getBalance_credit() - (v.getCredit_used() + v.getLocked_balance())))
                .setSortable(true)
                .setTextAlign(ColumnTextAlign.END)
                .setHeader("Disponible");
        grid.addColumn(s -> ODateUitls.dd_MM_yyyy.format(ODateUitls.valueOf(s.getExpiration_date())))
                .setSortable(true)
                .setHeader("Vencimiento");
        grid.addThemeVariants(GridVariant.LUMO_ROW_STRIPES);
        return grid;
    }

    public void setDataProvider(ListDataProvider<SystemIdBalanceOResponse> listDataProvider) {
        grid.setDataProvider(listDataProvider);
    }

}