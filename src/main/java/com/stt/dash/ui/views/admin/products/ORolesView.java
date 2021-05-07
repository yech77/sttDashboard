package com.stt.dash.ui.views.admin.products;

import com.stt.dash.app.security.CurrentUser;
import com.stt.dash.backend.data.Role;
import com.stt.dash.backend.data.entity.ORole;
import com.stt.dash.backend.data.entity.Product;
import com.stt.dash.backend.service.ORoleService;
import com.stt.dash.backend.service.ProductService;
import com.stt.dash.ui.MainView;
import com.stt.dash.ui.crud.AbstractBakeryCrudView;
import com.stt.dash.ui.utils.BakeryConst;
import com.stt.dash.ui.utils.converters.CurrencyFormatter;
import com.vaadin.flow.component.crud.BinderCrudEditor;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.BeanValidationBinder;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;

import java.util.Currency;

import static com.stt.dash.ui.utils.BakeryConst.PAGE_PRODUCTS;

@Route(value = "OROLES", layout = MainView.class)
@PageTitle("OROLES")
@Secured(Role.ADMIN)
public class ORolesView extends AbstractBakeryCrudView<ORole> {

	private CurrencyFormatter currencyFormatter = new CurrencyFormatter();

	@Autowired
	public ORolesView(ORoleService service, CurrentUser currentUser) {
		super(ORole.class, service, new Grid<>(), createForm(), currentUser);
	}

	@Override
	protected void setupGrid(Grid<ORole> grid) {
		grid.addColumn(ORole::getRolName).setHeader("Rol Name").setFlexGrow(10);
		grid.addColumn(ORole::getId).setHeader("Id").setFlexGrow(10);
		grid.addColumn(ORole::getAuthorities).setHeader("Unit Price");
	}

	@Override
	protected String getBasePage() {
		return "OROLES";
	}

	private static BinderCrudEditor<ORole> createForm() {
		TextField roleName = new TextField("Role name");
		roleName.getElement().setAttribute("colspan", "2");
//		TextField price = new TextField("Unit price");
//		price.getElement().setAttribute("colspan", "2");

		FormLayout form = new FormLayout(roleName);

		BeanValidationBinder<ORole> binder = new BeanValidationBinder<>(ORole.class);

		binder.bind(roleName, "rolName");

//		binder.forField(price).withConverter(new PriceConverter()).bind("price");
//		price.setPattern("\\d+(\\.\\d?\\d?)?$");
//		price.setPreventInvalidInput(true);

		String currencySymbol = Currency.getInstance(BakeryConst.APP_LOCALE).getSymbol();
//		price.setPrefixComponent(new Span(currencySymbol));

		return new BinderCrudEditor<>(binder, form);
	}

}
