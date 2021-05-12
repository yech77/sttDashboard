package com.stt.dash.ui.views.rol;

import com.stt.dash.app.security.CurrentUser;
import com.stt.dash.backend.data.Role;
import com.stt.dash.backend.data.entity.OAuthority;
import com.stt.dash.backend.data.entity.ORole;
import com.stt.dash.backend.service.OAuthorityService;
import com.stt.dash.backend.service.ORoleService;
import com.stt.dash.ui.MainView;
import com.stt.dash.ui.crud.AbstractBakeryCrudView;
import com.stt.dash.ui.utils.BakeryConst;
import com.stt.dash.ui.utils.converters.CurrencyFormatter;
import com.vaadin.flow.component.crud.BinderCrudEditor;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.BeanValidationBinder;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.vaadin.gatanaso.MultiselectComboBox;

import java.util.ArrayList;
import java.util.Currency;
import java.util.List;
import java.util.Set;

@Route(value = "ROLES", layout = MainView.class)
@PageTitle("ROLES")
@Secured({Role.ADMIN, "UI_ROL"})
public class ORolesView extends AbstractBakeryCrudView<ORole> {

    private CurrencyFormatter currencyFormatter = new CurrencyFormatter();

    @Autowired
    public ORolesView(OAuthorityService auth_service, ORoleService service, CurrentUser currentUser) {
        super(ORole.class, service, new Grid<>(), createForm(auth_service.findAll()), currentUser);

    }

    @Override
    protected void setupGrid(Grid<ORole> grid) {
        grid.addColumn(ORole::getRolName).setHeader("Rol Name");
        grid.addColumn(role -> {
            Set<OAuthority> authority = role.getAuthorities();
            if (authority == null) {
                return "-";
            }
            List<OAuthority> authList = new ArrayList<>(authority);
            StringBuilder string = new StringBuilder();

            for (OAuthority auth : authList) {
                string.append(auth.getAuthName()).append(", ");
            }
            return authList.isEmpty() ? "-" : string.toString().substring(0, string.length() - 2);
        }).setHeader("Authorities").setFlexGrow(5);
    }

    @Override
    protected String getBasePage() {
        return "OROLES";
    }

    private static BinderCrudEditor<ORole> createForm(List<OAuthority> authorities) {
        TextField roleName = new TextField("Role name");
        MultiselectComboBox<OAuthority> Authorities = new MultiselectComboBox<>("Authorities");
        roleName.getElement().setAttribute("colspan", "2");
//		TextField price = new TextField("Unit price");
//		price.getElement().setAttribute("colspan", "2");

        Authorities.setItems(authorities);
        Authorities.setItemLabelGenerator(OAuthority::getAuthName);
        FormLayout form = new FormLayout(roleName, Authorities);

        Binder<ORole> binder = new BeanValidationBinder<>(ORole.class);

        binder.forField(roleName)
                .bind(ORole::getRolName, ORole::setRolName);
//        binder.bind(roleName, "rolName");
        binder.forField(Authorities)
                .bind(ORole::getAuthorities, ORole::setAuthorities);
//		binder.forField(price).withConverter(new PriceConverter()).bind("price");
//		price.setPattern("\\d+(\\.\\d?\\d?)?$");
//		price.setPreventInvalidInput(true);

        String currencySymbol = Currency.getInstance(BakeryConst.APP_LOCALE).getSymbol();
//		price.setPrefixComponent(new Span(currencySymbol));

        return new BinderCrudEditor<>(binder, form);
    }

}
