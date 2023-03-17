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

@Tag("user-form-v2")
@JsModule("./src/views/user/user-form-v2.ts")
@Route(value = "user-form-v2", layout = MainView.class)
@PageTitle("BakeryConst.TITLE_SMS_SHOW_VIEW")
public class UserFormV2 extends LitTemplate {
    @Id("clients")
    private ComboBox<Client> clientComboBox;
    @Id("systemids")
    private MultiComboBox<SystemId> systemIdComboBox;
    @Id("userName")
    private TextField userNameTextField;
    @Id("userLastname")
    private TextField userLastnameTextField;
    @Id("userEmail")
    private EmailField userEmailTextField;
    @Id("active")
    private Checkbox activeCheckBox;
    @Id("password")
    private PasswordField passwordField;

    private final ClientService clientService;

    public UserFormV2(ClientService clientService) {
        this.clientService = clientService;
        systemIdComboBox.setItemLabelGenerator(SystemId::getSystemId);
        systemIdComboBox.setOpened(true);
        clientComboBox.setWidthFull();
        clientComboBox.setItemLabelGenerator(cliente -> {
            return String.format("%s - %s", cliente.getClientCod(), cliente.getClientName());
        });
        clientComboBox.addValueChangeListener((evt) -> {
            if (evt.getSource().getValue() == null) {
                return;
            }
            systemIdComboBox.setItems(evt.getSource().getValue().getSystemids());
            systemIdComboBox.setValue(new HashSet<>(evt.getSource().getValue().getSystemids()));
        });
        clientComboBox.setItems(clientService.findAll().getContent());
    }
}
