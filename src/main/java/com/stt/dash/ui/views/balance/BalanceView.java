package com.stt.dash.ui.views.balance;

import com.stt.dash.app.security.CurrentUser;
import com.stt.dash.app.session.ListGenericBean;
import com.stt.dash.backend.data.Role;
import com.stt.dash.backend.data.entity.SystemId;
import com.stt.dash.backend.service.SystemIdBalanceWebClientService;
import com.stt.dash.backend.util.ws.SystemIdBalanceOResponse;
import com.stt.dash.ui.MainView;
import com.stt.dash.uiv2.components.FlexBoxLayout;
import com.stt.dash.uiv2.components.detailsdrawer.DetailsDrawer;
import com.stt.dash.uiv2.components.detailsdrawer.DetailsDrawerFooter;
import com.stt.dash.uiv2.components.detailsdrawer.DetailsDrawerHeader;
import com.stt.dash.views.ViewFrame;
import com.vaadin.componentfactory.multiselect.MultiComboBox;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.data.provider.ListDataProvider;
import com.vaadin.flow.router.Route;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.access.annotation.Secured;

@Route(value = "balance", layout = MainView.class)
@Secured({Role.ADMIN, "UI_AUDIT"})
public class BalanceView extends ViewFrame {
    private final SystemIdBalanceWebClientService balanceWebClientService;
    private final BalancePresenter presenter;
    private MultiComboBox<SystemId> systemIdMultiComboBox;
    private Grid<SystemIdBalanceOResponse> grid;
    private ListDataProvider<SystemIdBalanceOResponse> listDataProvider;

    private DetailsDrawer detailsDrawer;
    private DetailsDrawerHeader detailsDrawerHeader;
    private DetailsDrawerFooter detailsDrawerFooter;

    public BalanceView(SystemIdBalanceWebClientService balanceWebClientService,
                       @Qualifier("getUserSystemIdString") ListGenericBean<String> stringListGenericBean,
                       CurrentUser currentUser) {
        this.balanceWebClientService = balanceWebClientService;
        setViewContent(createContent());
        presenter = new BalancePresenter(balanceWebClientService, stringListGenericBean, this);
    }

    private Component createContent() {
        FlexBoxLayout flexBoxLayout = new FlexBoxLayout(createGrid());

        return flexBoxLayout;
    }

    private Component createGrid() {
        grid = new Grid<>();
        grid.addColumn(v -> {
            return v.getSystemid().getSystem_id();
        }).setHeader("Credencial");
        grid.addColumn(SystemIdBalanceOResponse::getBalance_credit).setHeader("Credito");
        grid.addColumn(SystemIdBalanceOResponse::getCredit_used).setHeader("Usado");
        grid.addColumn(SystemIdBalanceOResponse::getExpiration_date).setHeader("Vencimiento");
        return grid;
    }

    public void setDataProvider(ListDataProvider<SystemIdBalanceOResponse> listDataProvider) {
        this.listDataProvider = listDataProvider;
        grid.setDataProvider(listDataProvider);
    }

}
