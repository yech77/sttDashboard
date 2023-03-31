package com.stt.dash.ui.views.admin.users.v2;

import com.stt.dash.app.security.CurrentUser;
import com.stt.dash.backend.data.Role;
import com.stt.dash.backend.data.entity.Client;
import com.stt.dash.backend.data.entity.ORole;
import com.stt.dash.backend.data.entity.SystemId;
import com.stt.dash.backend.data.entity.User;
import com.stt.dash.ui.crud.OnUIForm;
import com.stt.dash.ui.utils.I18nUtils;
import com.vaadin.componentfactory.multiselect.MultiComboBox;
import com.vaadin.flow.component.HasValue;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.confirmdialog.ConfirmDialog;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.textfield.EmailField;
import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.BeanValidationBinder;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.binder.ValidationResult;
import com.vaadin.flow.data.binder.Validator;
import com.vaadin.flow.data.binder.ValueContext;
import com.vaadin.flow.data.provider.DataProvider;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterListener;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.BeforeLeaveEvent;
import com.vaadin.flow.router.BeforeLeaveObserver;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

public class UserFormDialogV2 extends FormLayout implements OnUIForm<User>, BeforeEnterObserver, BeforeLeaveObserver {
    public static final String MSG_DEBE_ESCOGER_UN_CLIENTE1 = "Debe escoger un Cliente";
    private final List<User> allMyUsers;
    private final CurrentUser currentUser;
    FormItem comboClientFormItem;
    FormItem systemidsFormItem;
    /**/ BeanValidationBinder<User> binder = new BeanValidationBinder<>(User.class);
    HorizontalLayout h = new HorizontalLayout();
    MultiComboBox<SystemId> systemids = new MultiComboBox<>();
    ComboBox<Client> comboClient = new ComboBox<>();
    ComboBox<User.OUSER_TYPE_ORDINAL> userTypeOrdCombo = new ComboBox<>();
    Checkbox isActive = new Checkbox("Activo");
    Checkbox isCompanyAdmin = new Checkbox("Administrador de Empresa");
    //    ComboBox<User> userParentCombobox = new ComboBox<>();
    ConfirmDialog dialog;
    private final List<SystemId> list;
    private final Collection<User> userBelongList;

    public UserFormDialogV2(List<ORole> allRoles, List<SystemId> parSystemids, List<User> allUsers, Collection<User> userBelongList, CurrentUser currentUser, PasswordEncoder passwordEncoder) {
        this.allMyUsers = allUsers;
        this.currentUser = currentUser;
        this.list = parSystemids;
        this.userBelongList = userBelongList;
        EmailField email = new EmailField();
        TextField first = new TextField();
        TextField last = new TextField();
        PasswordField password = new PasswordField();
        /**/
        setResponsiveSteps(new ResponsiveStep("25em", 1, ResponsiveStep.LabelsPosition.TOP), new ResponsiveStep("32em", 2, ResponsiveStep.LabelsPosition.TOP));
        /**/
        doMulticomboI18N();
        /**/
        doWidthFull(email, first, last, password);
        first.setPlaceholder("Nombre");
        email.setPlaceholder("user@something.com");
//        formItem.add(isActive);
        doColSpan(email, first, last, password);
        /**/
        doBinder(passwordEncoder, email, first, last, password);
        /**/
        doSetItems(allRoles, currentUser.getUser().getClients(), allUsers);
        doValueListeners();

        /**/
        if (ObjectUtils.isNotEmpty(list)) {
            systemids.setItems(list);
        }
        /**/
        userTypeOrdCombo.setItems(User.OUSER_TYPE_ORDINAL.values());
        /**/
        if (currentUser.getUser().getUserTypeOrd() != User.OUSER_TYPE_ORDINAL.COMERCIAL) {
            isCompanyAdmin.setValue(false);
            isCompanyAdmin.setEnabled(false);
        }
        isCompanyAdmin.addValueChangeListener(event -> {
            userTypeOrdCombo.setValue(isCompanyAdmin.getValue() ? User.OUSER_TYPE_ORDINAL.ADMIN_EMPRESAS : User.OUSER_TYPE_ORDINAL.EMPRESA);
        });
    }

    private void doSetItems(List<ORole> allRoles, Collection<Client> parClients, List<User> allUsers) {
        /**/
        doSetItemsClient(parClients);
        systemids.setItemLabelGenerator(SystemId::getSystemId);
        /**/
    }

    private void doSetItemsClient(Collection<Client> parClients) {
        comboClient.setItems(parClients);
        comboClient.setItemLabelGenerator(Client::getClientName);
        comboClient.addValueChangeListener((evt) -> {
            if (!evt.isFromClient() || evt.getSource().getValue() == null) {
                return;
            }
            /* TODO: Verificar porque se hace esta condicion para el set de items. */
            if (User.OUSER_TYPE_ORDINAL.COMERCIAL == currentUser.getUser().getUserTypeOrd()) {
                systemids.setItems(evt.getSource().getValue().getSystemids());
            }
        });
    }

    private void doValueListeners() {
        /* CHANGE LISTENER */
        addChangeListenerIsActive();
    }

    private void addChangeListenerIsActive() {
        isActive.addValueChangeListener(change -> {
            if (!change.isFromClient()) {
                return;
            }
            if (!change.getValue()) {
                dialog = new ConfirmDialog("Desactivar Usuario", "Al desactivar este usuario no podra usar el sistema", "Cerrar", this::cancelar);
                dialog.open();
            }
        });
    }

    private void doMulticomboI18N() {
        systemids.setI18n(I18nUtils.getMulticomboI18n());
    }

    private void doWidthFull(EmailField email, TextField first, TextField last, PasswordField password) {
        first.setWidthFull();
        last.setWidthFull();
        email.setWidthFull();
        password.setWidthFull();
//        createdDate.setWidthFull();
        h.setWidthFull();
        comboClient.setWidthFull();
        systemids.setWidthFull();
    }

    private void doBinder(PasswordEncoder passwordEncoder, EmailField email, TextField first, TextField last, PasswordField password) {
        binder.bind(first, "firstName");
        binder.bind(last, "lastName");
        binder.forField(userTypeOrdCombo).bind(User::getUserTypeOrd, User::setUserTypeOrd);
        binder.forField(isActive).bind(User::isActive, User::setActive);
        binder.bind(email, "email");
//        binder.forField(userParentCombobox).bind(User::getUserParent, User::setUserParent);
        /**/
        doBinderSystemid();
        doBinderClient();
        doBinderPassword(passwordEncoder, password);
    }

    private void doBinderSystemid() {
        binder.forField(systemids).asRequired(new Validator<Set<SystemId>>() {
            @Override
            public ValidationResult apply(Set<SystemId> systemIds, ValueContext valueContext) {
                if (systemIds != null && systemIds.size() > 0) {
                    return ValidationResult.ok();
                }
                return ValidationResult.error("Debe tener al menos una Credencial");
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

    private void doBinderClient() {
        binder.forField(comboClient).asRequired(new Validator<Client>() {
            @Override
            public ValidationResult apply(Client client, ValueContext valueContext) {
                if (client != null) {
                    return ValidationResult.ok();
                }
                return ValidationResult.error(MSG_DEBE_ESCOGER_UN_CLIENTE1);
            }
        }).bind(User::getClient, User::setClient);
    }

    private void removeBinding(HasValue<?, ?> binding) {
        binder.removeBinding(binding);
    }

    private void cancelar(ConfirmDialog.ConfirmEvent event) {
        dialog.close();
        isActive.focus();
    }

    public Binder<User> getBinder() {
        System.out.println("Llamado getBinder de User");
        return binder;
    }

    private List<User> filterUsersOfType(List<User> users, User.OUSER_TYPE_ORDINAL... targetTypes) {
        List<User> values = new ArrayList<>();
        for (User user : users) {
            for (User.OUSER_TYPE_ORDINAL type : targetTypes) {
                System.out.println("Usuario: " + user.getEmail() + ":" + user.getUserTypeOrd() + " - " + type);
                if (user.getUserTypeOrd() == type) {
                    values.add(user);
                    break;
                }
            }
        }
        return values;
    }

    private void doColSpan(EmailField email, TextField first, TextField last, PasswordField password) {
        setColspan(addFormItem(isCompanyAdmin, ""), 2);
//        setColspan(addFormItem(userParentCombobox, "Pertenece a"), 1);
        setColspan(addFormItem(first, "Nombre"), 1);
        setColspan(addFormItem(last, "Apellido"), 1);
        setColspan(addFormItem(email, isActive), 1);
        setColspan(addFormItem(password, "Clave"), 1);
        comboClientFormItem = addFormItem(comboClient, "Cliente");
        setColspan(comboClientFormItem, 2);
        systemidsFormItem = addFormItem(systemids, "Credenciales");
        setColspan(systemidsFormItem, 2);
    }

    @Override
    public void onSaveUI(long idBeforeSave, User entity) {
        if (idBeforeSave == 0) {
            allMyUsers.add(entity);
        }
    }

    @Override
    public void beforeEnter(BeforeEnterEvent beforeEnterEvent) {
    }

    @Override
    public void beforeLeave(BeforeLeaveEvent beforeLeaveEvent) {
    }
}
