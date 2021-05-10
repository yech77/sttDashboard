package com.stt.dash.ui.views.admin.users;

import com.stt.dash.backend.data.Role;
import com.stt.dash.backend.data.entity.*;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.textfield.EmailField;
import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.BeanValidationBinder;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.provider.DataProvider;
import com.vaadin.flow.data.provider.ListDataProvider;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.vaadin.gatanaso.MultiselectComboBox;

import java.util.List;

public class UserForm extends FormLayout {

    BeanValidationBinder<User> binder = new BeanValidationBinder<>(User.class);

    public UserForm(List<ORole> allRoles,
                    List<Client> parClients,
                    List<SystemId> parSystemids,
                    List<User> allUsers,
                    PasswordEncoder passwordEncoder) {
        /**/
        MultiselectComboBox<Client> clients = new MultiselectComboBox<>("Clientes");
        ComboBox<Client> comboClient = new ComboBox<>("Cliente");
        /**/
        MultiselectComboBox<SystemId> systemids = new MultiselectComboBox<>("Credenciales");
        /**/
        ComboBox<User> userParent = new ComboBox<>("Pertenece a");
        EmailField email = new EmailField("Correo (login)");
        email.getElement().setAttribute("colspan", "2");
        TextField first = new TextField("Nombre");
        TextField last = new TextField("Apellido");
        PasswordField password = new PasswordField("Clave");
        password.getElement().setAttribute("colspan", "2");
        ComboBox<String> role = new ComboBox<>();
        MultiselectComboBox<ORole> roles = new MultiselectComboBox<>("Roles");
        role.getElement().setAttribute("colspan", "2");
        role.setLabel("Role");
        /**/
        roles.getElement().setAttribute("colspan", "2");
        roles.setItems(allRoles);
        roles.setItemLabelGenerator(ORole::getRolName);
        /**/
        userParent.setItems(allUsers);
        /* ADD TO FORM */
        add(email, first, last, password, roles, clients, systemids, role);
        /**/
        ListDataProvider<String> roleProvider = DataProvider.ofItems(Role.getAllRoles());
        role.setItemLabelGenerator(s -> s != null ? s : "");
        role.setDataProvider(roleProvider);

        binder.bind(first, "firstName");
        binder.bind(last, "lastName");
        binder.bind(email, "email");
        binder.bind(role, "role");
        binder.bind(roles, "roles");
        binder.forField(userParent)
                .asRequired("Seleccione un usuario")
                .bind(User::getUserParent, User::setUserParent);

        binder.forField(password)
                .withValidator(pass -> pass.matches("^(|(?=.*\\d)(?=.*[a-z])(?=.*[A-Z]).{6,})$"),
                        "más de 6 caracteres, combinando dígitos, minúsculas y mayúsculas")
                .bind(user -> password.getEmptyValue(), (user, pass) -> {
                    if (!password.getEmptyValue().equals(pass)) {
                        user.setPasswordHash(passwordEncoder.encode(pass));
                    }
                });
        /**/
        userParent.addValueChangeListener(listener -> {
            if (!listener.isFromClient()) {
                return;
            }
            switch (listener.getSource().getValue().getUserTypeOrd()) {
                case COMERCIAL:
                    comboClient.setItems(listener.getSource().getValue().getClients());
                    break;
                case ADMIN_EMPRESAS:
                    systemids.setItems(listener.getSource().getValue().getClient().getSystemids());
                    break;
                case EMPRESA:
                    systemids.setItems(listener.getSource().getValue().getSystemids());
                    break;
                case USUARIO:
                    systemids.setItems(listener.getSource().getValue().getSystemids());
                    break;
            }

        });
    }

    public Binder<User> getBinder() {
        return binder;
    }
}
