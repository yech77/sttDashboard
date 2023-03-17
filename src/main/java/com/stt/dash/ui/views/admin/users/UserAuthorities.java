package com.stt.dash.ui.views.admin.users;

import com.stt.dash.backend.data.entity.Client;
import com.stt.dash.backend.data.entity.SystemId;
import com.stt.dash.backend.service.ClientService;
import com.stt.dash.ui.MainView;
import com.vaadin.componentfactory.multiselect.MultiComboBox;
import com.vaadin.flow.component.Tag;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.dependency.JsModule;
import com.vaadin.flow.component.littemplate.LitTemplate;
import com.vaadin.flow.component.template.Id;
import com.vaadin.flow.component.textfield.EmailField;
import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

import java.util.HashSet;

@Tag("user-authorities")
@JsModule("./src/views/user/user-authorities.ts")
@Route(value = "user-authorities", layout = MainView.class)
@PageTitle("BakeryConst.TITLE_SMS_SHOW_VIEW")
public class UserAuthorities extends LitTemplate {
    @Id("clients")
    private ComboBox<Client> clientComboBox;

    private final ClientService clientService;

    public UserAuthorities(ClientService clientService) {
        this.clientService = clientService;
        clientComboBox.setWidthFull();
        clientComboBox.setItemLabelGenerator(cliente -> {
            return String.format("%s - %s", cliente.getClientCod(), cliente.getClientName());
        });
        clientComboBox.addValueChangeListener((evt) -> {
            if (evt.getSource().getValue() == null) {
                return;
            }
        });
        clientComboBox.setItems(clientService.findAll().getContent());
    }
}
