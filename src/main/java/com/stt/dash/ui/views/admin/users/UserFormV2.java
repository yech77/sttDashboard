package com.stt.dash.ui.views.admin.users;

import com.stt.dash.app.security.CurrentUser;
import com.stt.dash.backend.data.entity.Client;
import com.stt.dash.backend.data.entity.SystemId;
import com.stt.dash.backend.data.entity.User;
import com.stt.dash.backend.service.ClientService;
import com.stt.dash.ui.MainView;
import com.stt.dash.ui.utils.I18nUtils;
import com.vaadin.componentfactory.multiselect.MultiComboBox;
import com.vaadin.flow.component.Tag;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.dependency.JsModule;
import com.vaadin.flow.component.littemplate.LitTemplate;
import com.vaadin.flow.component.template.Id;
import com.vaadin.flow.component.textfield.EmailField;
import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.BeanValidationBinder;
import com.vaadin.flow.data.binder.ValidationResult;
import com.vaadin.flow.data.binder.Validator;
import com.vaadin.flow.data.binder.ValueContext;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.HashSet;
import java.util.Set;

@Tag("user-form-v2")
@JsModule("./src/views/user/user-form-v2.ts")
@Route(value = "user-form-v2", layout = MainView.class)
@PageTitle("BakeryConst.TITLE_SMS_SHOW_VIEW")
public class UserFormV2 extends LitTemplate {
    public static final String MSG_DEBE_ESCOGER_UN_CLIENTE1 = "Debe escoger un Cliente";
    public static final String DEBE_TENER_AL_MENOS_UNA_CREDENCIAL = "Debe tener al menos una Credencial";
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
    @Id("save")
    private Button saveButton;
    BeanValidationBinder<User> binder = new BeanValidationBinder<>(User.class);

    public UserFormV2(CurrentUser currentUser, PasswordEncoder passwordEncoder) {
        systemIdComboBox.setItemLabelGenerator(SystemId::getSystemId);
        systemIdComboBox.setOpened(true);
        clientComboBox.setWidthFull();
        clientComboBox.setItemLabelGenerator(cliente -> {
            return String.format("%s - %s", cliente.getClientCod(), cliente.getClientName());
        });
        clientComboBox.setItems(currentUser.getUser().getClients());
        clientComboBox.addValueChangeListener((evt) -> {
            if (evt.getSource().getValue() == null || !evt.isFromClient()) {
                return;
            }
            systemIdComboBox.setItems(evt.getSource().getValue().getSystemids());
            systemIdComboBox.setValue(new HashSet<>(evt.getSource().getValue().getSystemids()));
        });
        doMulticomboI18N();
        /**/
        doBinder(passwordEncoder, userEmailTextField, userNameTextField, userLastnameTextField, passwordField);
    }


    private void doBinder(PasswordEncoder passwordEncoder, EmailField email, TextField first, TextField last, PasswordField password) {
        binder.bind(first, "firstName");
        binder.bind(last, "lastName");
        binder.bind(email, "email");
        binder.forField(activeCheckBox).bind(User::isActive, User::setActive);
        /**/
        doBinderClient();
        doBinderSystemid();
        doBinderPassword(passwordEncoder, password);
    }

    private void doBinderClient() {
        binder.forField(clientComboBox).asRequired(new Validator<Client>() {
            @Override
            public ValidationResult apply(Client client, ValueContext valueContext) {
                if (client != null) {
                    return ValidationResult.ok();
                }
                return ValidationResult.error(MSG_DEBE_ESCOGER_UN_CLIENTE1);
            }
        }).bind(User::getClient, User::setClient);
    }

    private void doBinderSystemid() {
        binder.forField(systemIdComboBox).asRequired(new Validator<Set<SystemId>>() {
            @Override
            public ValidationResult apply(Set<SystemId> systemIds, ValueContext valueContext) {
                if (systemIds != null && systemIds.size() > 0) {
                    return ValidationResult.ok();
                }
                return ValidationResult.error(DEBE_TENER_AL_MENOS_UNA_CREDENCIAL);
            }
        }).bind(User::getSystemids, User::setSystemids);
    }

    private void doBinderPassword(PasswordEncoder passwordEncoder, PasswordField password) {
        binder.forField(password)
                .withValidator(pass -> pass.matches("^(|(?=.*\\d)(?=.*[a-z])(?=.*[A-Z]).{6,})$"), "más de 6 caracteres, combinando dígitos, minúsculas y mayúsculas")
                .bind(user -> password.getEmptyValue(), (user, pass) -> {
                    if (!password.getEmptyValue().equals(pass)) {
                        user.setPasswordHash(passwordEncoder.encode(pass));
                    }
                });
    }

    private void doMulticomboI18N() {
        systemIdComboBox.setI18n(I18nUtils.getMulticomboI18n());
    }
}
