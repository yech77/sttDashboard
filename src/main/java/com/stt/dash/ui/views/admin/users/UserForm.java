package com.stt.dash.ui.views.admin.users;

import com.stt.dash.app.security.CurrentUser;
import com.stt.dash.backend.data.Role;
import com.stt.dash.backend.data.entity.*;
import com.stt.dash.ui.views.HasNotifications;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.confirmdialog.ConfirmDialog;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.textfield.EmailField;
import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.BeanValidationBinder;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.provider.DataProvider;
import com.vaadin.flow.data.provider.ListDataProvider;
import com.vaadin.flow.dom.Element;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.vaadin.gatanaso.MultiselectComboBox;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;

public class UserForm extends FormLayout{
    private final List<User> allMyUsers;
    private final CurrentUser currentUser;
    /**/
    FormItem clientsFormItem;
    FormItem comboClientFormItem;
    FormItem systemidsFormItem;
    /**/
    BeanValidationBinder<User> binder = new BeanValidationBinder<>(User.class);
    HorizontalLayout h = new HorizontalLayout();
    MultiselectComboBox<Client> clients = new MultiselectComboBox<>();
    MultiselectComboBox<SystemId> systemids = new MultiselectComboBox<>();
    ComboBox<Client> comboClient = new ComboBox<>();
    MultiselectComboBox<ORole> roles = new MultiselectComboBox<>();
    Checkbox isActive = new Checkbox("ACTIVO");
    ConfirmDialog dialog;
    ComboBox<User> userParent = new ComboBox<>();
    ComboBox<User.OUSER_TYPE_ORDINAL> userTypeOrd = new ComboBox<>();
    ComboBox<User.OUSER_TYPE> userType = new ComboBox<>();
    ComboBox<String> role = new ComboBox<>();
    public UserForm(List<ORole> allRoles,
                    List<Client> parClients,
                    Collection<SystemId> parSystemids,
                    List<User> allUsers,
                    CurrentUser currentUser,
                    PasswordEncoder passwordEncoder) {
        this.allMyUsers = allUsers;
        this.currentUser=currentUser;
        /**/
        EmailField email = new EmailField();
        TextField first = new TextField();
        TextField last = new TextField();
        PasswordField password = new PasswordField();
        /**/
        setResponsiveSteps(
                new ResponsiveStep("25em", 1, ResponsiveStep.LabelsPosition.TOP),
                new ResponsiveStep("32em", 2, ResponsiveStep.LabelsPosition.TOP));
        userType.setWidthFull();
        userTypeOrd.setWidthFull();
        userParent.setWidthFull();
        first.setWidthFull();
        last.setWidthFull();
        email.setWidthFull();
        password.setWidthFull();
//        createdDate.setWidthFull();
        h.setWidthFull();
        roles.setWidthFull();
        comboClient.setWidthFull();
        clients.setWidthFull();
        systemids.setWidthFull();
        email.setPlaceholder("user@something.com");
//        formItem.add(isActive);
        setColspan(addFormItem(userTypeOrd, "Tipo de Usuario"), 1);
        setColspan(addFormItem(userParent, "Creador"), 1);
        setColspan(addFormItem(first, "Nombre"), 1);
        setColspan(addFormItem(last, "Apellido"), 1);
//        setColspan(addFormItem(createdDate, "Fecha de Creación"), 1);
//        setColspan(addFormItem(userType, ""), 2);
        setColspan(addFormItem(email, isActive), 1);
        setColspan(addFormItem(password, "Clave"), 1);
        clientsFormItem = addFormItem(clients, "Clientes");
        setColspan(clientsFormItem, 2);
        comboClientFormItem = addFormItem(comboClient, "Cliente");
        setColspan(comboClientFormItem, 2);
        systemidsFormItem = addFormItem(systemids, "Credenciales");
        setColspan(systemidsFormItem, 2);
        setColspan(addFormItem(roles, "Roles"), 2);
        /**/
        binder.bind(first, "firstName");
        binder.bind(last, "lastName");
        binder.forField(userTypeOrd).bind(User::getUserTypeOrd, User::setUserTypeOrd);
        binder.forField(userType).bind(User::getUserType, User::setUserType);
        binder.forField(isActive).bind(User::isActive, User::setActive);
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
        userTypeOrd.setItems(User.OUSER_TYPE_ORDINAL.values());
        userType.setItems(User.OUSER_TYPE.values());
        /**/
        clients.setItems(parClients);
        clients.setItemLabelGenerator(Client::getClientName);
        comboClient.setItems(parClients);
        comboClient.setItemLabelGenerator(Client::getClientName);
        comboClient.addValueChangeListener((evt) -> {
            if (evt.getSource().getValue() == null) {
                return;
            }
            systemids.setItems(evt.getSource().getValue().getSystemids());
            systemids.setValue(new HashSet<>(evt.getSource().getValue().getSystemids()));
        });
        systemids.setItemLabelGenerator(SystemId::getSystemId);
        /**/
        roles.setItems(allRoles);
        roles.setItemLabelGenerator(ORole::getRolName);
        /**/
        userParent.setItems(allUsers);
        setUserParentList(User.OUSER_TYPE_ORDINAL.ADMIN_EMPRESAS);
        userParent.setItemLabelGenerator(User::getEmail);
        /**/
        ListDataProvider<String> roleProvider = DataProvider.ofItems(Role.getAllRoles());
        role.setLabel("Role");
        role.setItemLabelGenerator(s -> s != null ? s : "");
        role.setDataProvider(roleProvider);
        /* CHANGE LISTENER */
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
                case USUARIO:
                    systemids.setItems(listener.getSource().getValue().getSystemids());
                    break;
            }

        });
        isActive.addValueChangeListener(change -> {
            if (!change.isFromClient()) {
                return;
            }
            if (!change.getValue()) {
                dialog = new ConfirmDialog("Desactivar Usuario",
                        "Al desactivar este usuario no podra usar el sistema", "Cerrar", this::cancelar);
                dialog.open();
            }
        });
        userTypeOrd.addValueChangeListener((evt) -> {
            User.OUSER_TYPE type;
//            if (!evt.isFromClient()) {
//                return;
//            }
            userParent.setValue(null);
            if (evt.getSource().getValue() == User.OUSER_TYPE_ORDINAL.COMERCIAL) {
                type = User.OUSER_TYPE.HAS;
                userParent.setItems(filterUsersOfType(this.allMyUsers, User.OUSER_TYPE_ORDINAL.COMERCIAL));
            } else if (evt.getSource().getValue() == User.OUSER_TYPE_ORDINAL.ADMIN_EMPRESAS) {
                type = User.OUSER_TYPE.IS;
                userParent.setItems(filterUsersOfType(this.allMyUsers, User.OUSER_TYPE_ORDINAL.COMERCIAL));
            } else if (evt.getSource().getValue() == User.OUSER_TYPE_ORDINAL.EMPRESA) {
                type = User.OUSER_TYPE.BY;
                userParent.setItems(filterUsersOfType(this.allMyUsers, User.OUSER_TYPE_ORDINAL.ADMIN_EMPRESAS));
            } else {
                type = User.OUSER_TYPE.BY;
                userParent.setItems(filterUsersOfType(this.allMyUsers, User.OUSER_TYPE_ORDINAL.EMPRESA, User.OUSER_TYPE_ORDINAL.ADMIN_EMPRESAS));
            }
            userType.setValue(type);
            doBinderOrd(evt.getSource().getValue());
            doShowClientOrd(evt.getSource().getValue());
        });

        /**/
        fillUserType(currentUser.getUser().getUserType(),
                currentUser.getUser().getUserTypeOrd(),
                userType, userTypeOrd);
        doBinderOrd(currentUser.getUser().getUserTypeOrd());
        doShowClientOrd(currentUser.getUser().getUserTypeOrd());
    }

    /**
     * Muestra el combo o el multicombo dependiendo del tipo de usuario ordinal
     *
     * @param changeListener
     */
    public void doShowClientOrd(User.OUSER_TYPE_ORDINAL changeListener){
        if (changeListener == User.OUSER_TYPE_ORDINAL.USUARIO ||
                changeListener == User.OUSER_TYPE_ORDINAL.EMPRESA) {
            /* USUARIO SOLO SELECCIONA CREDENCIALES */
            systemidsFormItem.setVisible(true);
            comboClientFormItem.setVisible(false);
            clientsFormItem.setVisible(false);
        } else if (changeListener == User.OUSER_TYPE_ORDINAL.ADMIN_EMPRESAS) {
            /* ADMIN SOLO SELECCIONA CLIENTE */
            systemidsFormItem.setVisible(false);
            comboClientFormItem.setVisible(true);
            clientsFormItem.setVisible(false);
        } else if (changeListener == User.OUSER_TYPE_ORDINAL.COMERCIAL) {
            /* COMERCIAL SOLO SELECCIONA CLIENTES */
            systemidsFormItem.setVisible(false);
            comboClientFormItem.setVisible(false);
            clientsFormItem.setVisible(true);
        }
    }

    private void cancelar(ConfirmDialog.ConfirmEvent event) {
        dialog.close();
        isActive.focus();
    }

    public Binder<User> getBinder() {
        return binder;
    }

    private void setUserParentList(User.OUSER_TYPE_ORDINAL type) {
        if (null == type) {
            userParent.setItems(filterUsersOfType(this.allMyUsers, User.OUSER_TYPE_ORDINAL.EMPRESA, User.OUSER_TYPE_ORDINAL.ADMIN_EMPRESAS));
            return;
        }
        switch (type) {
            case COMERCIAL:
                userParent.setItems(filterUsersOfType(this.allMyUsers, User.OUSER_TYPE_ORDINAL.COMERCIAL));
                break;
            case ADMIN_EMPRESAS:
                userParent.setItems(filterUsersOfType(this.allMyUsers, User.OUSER_TYPE_ORDINAL.COMERCIAL));
                break;
            case EMPRESA:
                userParent.setItems(filterUsersOfType(this.allMyUsers, User.OUSER_TYPE_ORDINAL.ADMIN_EMPRESAS));
                break;
            default:
                userParent.setItems(filterUsersOfType(this.allMyUsers, User.OUSER_TYPE_ORDINAL.EMPRESA, User.OUSER_TYPE_ORDINAL.ADMIN_EMPRESAS));
                break;
        }
    }
    public void setUser(User user) {
//        binder.removeBean();
        if (user == null) {
            return;
        }
        System.out.println("Seting usertype: " + user.getUserType());
        userType.setValue(user.getUserType());
        userTypeOrd.setValue(user.getUserTypeOrd());
        setUserParentList(user.getUserTypeOrd());
        doBinderOrd(user.getUserTypeOrd());
        doShowClientOrd(user.getUserTypeOrd());

        // No permite que el usuario pueda cambiar sus propios datos criticos
        if (user.equals(currentUser.getUser())) {
            userParent.setReadOnly(true);
            userTypeOrd.setReadOnly(true);
            roles.setReadOnly(true);
            userType.setReadOnly(true);
            systemids.setReadOnly(true);
            clients.setReadOnly(true);
            comboClient.setReadOnly(true);
            isActive.setReadOnly(true);
        } else {
            userParent.setReadOnly(false);
            userTypeOrd.setReadOnly(false);
            roles.setReadOnly(false);
            systemids.setReadOnly(false);
            clients.setReadOnly(false);
            comboClient.setReadOnly(false);

        }
//        binder.setBean(user);
        //activeStatus.setValue(user.getUserStatus() == OUser.OUSER_STATUS.ACTIVO);
    }
    /**
     * Llena el combo de tipo de usuario. Un usuario de Tipo IS solo uede crear
     * user tipo BY. Los tipo BY solo tienen asignados SIDS.
     *
     * @param usertype
     * @param c
     */
    private void fillUserType(User.OUSER_TYPE usertype,
                              User.OUSER_TYPE_ORDINAL userTypeOrd,
                              ComboBox<User.OUSER_TYPE> c,
                              ComboBox<User.OUSER_TYPE_ORDINAL> d) {
        c.clear();
        c.setItems(User.OUSER_TYPE.values());
        d.clear();
        if (userTypeOrd == User.OUSER_TYPE_ORDINAL.COMERCIAL) {
            d.setItems(User.OUSER_TYPE_ORDINAL.values());
        } else if (userTypeOrd == User.OUSER_TYPE_ORDINAL.ADMIN_EMPRESAS) {
            d.setItems(User.OUSER_TYPE_ORDINAL.EMPRESA, User.OUSER_TYPE_ORDINAL.USUARIO);
        } else if (userTypeOrd == User.OUSER_TYPE_ORDINAL.EMPRESA) {
            d.setItems(User.OUSER_TYPE_ORDINAL.USUARIO);
        } else {
            d.setItems(User.OUSER_TYPE_ORDINAL.USUARIO);
            c.setEnabled(false);
        }
    }

    private List<User> filterUsersOfType(List<User> users,
                                         User.OUSER_TYPE_ORDINAL... targetTypes) {
        List<User> values = new ArrayList<>();
        for (User user : users) {
            for (User.OUSER_TYPE_ORDINAL type : targetTypes) {
                if (user.getUserTypeOrd() == type) {
                    values.add(user);
                    break;
                }
            }
        }
        return values;
    }

    /**
     * Realiza el binder al combo o al multi en cliente dependiendo del tipo de
     * usuario.
     *
     * @param userTypeOrd
     */
    private void doBinderOrd(User.OUSER_TYPE_ORDINAL userTypeOrd) {
        //binder.removeBinding(this.userType);
        System.out.println("el DoBinderORD es: " + userTypeOrd);
        if (null != userTypeOrd) {
            switch (userTypeOrd) {
                case COMERCIAL:
                    binder.removeBinding(comboClient);
                    binder.forField(clients)
                            .asRequired()
                            .bind(User::getClients, User::setClients);
                    break;
                case ADMIN_EMPRESAS:
                    binder.removeBinding(clients);
                    binder.forField(comboClient)
                            .asRequired()
                            .bind(User::getClient, User::setClient);
                    break;
                case EMPRESA:
                case USUARIO:
                    binder.removeBinding(clients);
                    binder.removeBinding(comboClient);
                    binder.forField(systemids)
                            .asRequired()
                            .bind(User::getSystemids, User::setSystemids);
                    break;
                default:
                    break;
            }
        }
    }
}
