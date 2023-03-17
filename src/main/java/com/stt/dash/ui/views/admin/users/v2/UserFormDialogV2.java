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
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class UserFormDialogV2 extends FormLayout implements OnUIForm<User> {
    public static final String MSG_DEBE_ESCOGER_UN_CLIENTE1 = "Debe escoger un Cliente";
    private final List<User> allMyUsers;
    private final CurrentUser currentUser;
    FormItem comboClientFormItem;
    FormItem systemidsFormItem;
    /**/ BeanValidationBinder<User> binder = new BeanValidationBinder<>(User.class);
    HorizontalLayout h = new HorizontalLayout();
    MultiComboBox<SystemId> systemids = new MultiComboBox<>();
    ComboBox<Client> comboClient = new ComboBox<>();
    Checkbox isActive = new Checkbox("Activo");
    ConfirmDialog dialog;
    ComboBox<User.OUSER_TYPE> userType = new ComboBox<>();
    ComboBox<User.OUSER_TYPE_ORDINAL> userTypeOrdCombo = new ComboBox<>();
    ComboBox<User> userParentCombobox = new ComboBox<>();
    ComboBox<String> role = new ComboBox<>();

    public UserFormDialogV2(List<ORole> allRoles, List<Client> parClients, List<Client> allClient, Collection<SystemId> parSystemids, List<User> allUsers, CurrentUser currentUser, PasswordEncoder passwordEncoder) {
        this.allMyUsers = allUsers;
        this.currentUser = currentUser;
        /**/
        userTypeOrdCombo.setItems(User.OUSER_TYPE_ORDINAL.values());
        /**/
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
        email.setPlaceholder("user@something.com");
//        formItem.add(isActive);
        doColSpan(email, first, last, password);
        /**/
        doBinder(passwordEncoder, email, first, last, password);
        /**/
        doSetItems(allRoles, parClients, allClient, allUsers);
        doValueListeners();

        /**/
        fillUserType(currentUser.getUser().getUserType(), currentUser.getUser().getUserTypeOrd(), userType);
        doShowClientOrd(currentUser.getUser().getUserTypeOrd());
        /* Cada vez que se cambia el correo se llena este set que ya no esta en pantalla. */
        email.addBlurListener(event -> {
            userTypeOrdCombo.setValue(User.OUSER_TYPE_ORDINAL.ADMIN_EMPRESAS);
            userParentCombobox.setValue(currentUser.getUser());
            userType.setValue(User.OUSER_TYPE.IS);
        });
    }

    private void doSetItems(List<ORole> allRoles, List<Client> parClients, List<Client> allClients, List<User> allUsers) {
        userType.setItems(currentUser.getUser().getUserType());
        /**/
        doSetItemsClient(allClients);
        systemids.setItemLabelGenerator(SystemId::getSystemId);
        /**/
        doSetItemsUserParent();
        /**/
        /**/
        doSetItemsRole();
    }

    private void doSetItemsClient(List<Client> parClients) {
        comboClient.setItems(parClients);
        comboClient.setItemLabelGenerator(Client::getClientName);
        comboClient.addValueChangeListener((evt) -> {
            if (evt.getSource().getValue() == null) {
                return;
            }
            systemids.setItems(evt.getSource().getValue().getSystemids());
            systemids.setValue(new HashSet<>(evt.getSource().getValue().getSystemids()));
        });
    }

    private void doSetItemsUserParent() {
        userParentCombobox.setItems(currentUser.getUser());
        userParentCombobox.setValue(currentUser.getUser());
    }

    private void doSetItemsRole() {
        role.setLabel("Role");
        role.setItemLabelGenerator(s -> s != null ? s : "");
        role.setDataProvider(DataProvider.ofItems(Role.getAllRoles()));
    }

    private void doValueListeners() {
        /* CHANGE LISTENER */
        addChangeListenerIsActive();
        addChageListenerUserType();
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

    private void addChageListenerUserType() {
        doShowClientOrd(User.OUSER_TYPE_ORDINAL.COMERCIAL);
    }

    private void doMulticomboI18N() {
        systemids.setI18n(I18nUtils.getMulticomboI18n());
    }

    private void doWidthFull(EmailField email, TextField first, TextField last, PasswordField password) {
        userType.setWidthFull();
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
        binder.forField(userType).bind(User::getUserType, User::setUserType);
        binder.forField(isActive).bind(User::isActive, User::setActive);
        binder.bind(email, "email");
        binder.bind(role, "role");
        binder.forField(userParentCombobox).bind(User::getUserParent, User::setUserParent);
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

    /**
     * Muestra el combo o el multicombo dependiendo del tipo de usuario ordinal
     *
     * @param changeListener
     */
    public void doShowClientOrd(User.OUSER_TYPE_ORDINAL changeListener) {
        if (changeListener == null) {
            return;
        }
        systemidsFormItem.setVisible(true);
        comboClientFormItem.setVisible(true);
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

    public void setUser(User user) {
//        binder.removeBean();
        System.out.println("Llamado le setUser de User: " + user);
        if (user == null) {
            return;
        }
        binder.setBean(user);
        System.out.println("Seting usertype: " + user.getUserType());
        userType.setValue(user.getUserType());
        doShowClientOrd(user.getUserTypeOrd());

        // No permite que el usuario pueda cambiar sus propios datos criticos
        if (user.equals(currentUser.getUser())) {
            userType.setReadOnly(true);
            systemids.setReadOnly(true);
            comboClient.setReadOnly(true);
            isActive.setReadOnly(true);
        } else {
            systemids.setReadOnly(false);
            comboClient.setReadOnly(false);

        }
//        binder.setBean(user);
        //activeStatus.setValue(user.getUserStatus() == OUser.OUSER_STATUS.ACTIVO);
    }

    /**
     * Llena el combo de tipo de usuario. Un usuario de Tipo IS solo pueden crear
     * user tipo BY. Los tipo BY solo tienen asignados SIDS.
     *
     * @param usertype
     * @param userTypeCombo
     */
    private void fillUserType(User.OUSER_TYPE usertype, User.OUSER_TYPE_ORDINAL userTypeOrd, ComboBox<User.OUSER_TYPE> userTypeCombo) {
        userTypeCombo.clear();
        userTypeCombo.setItems(User.OUSER_TYPE.values());
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
        setColspan(addFormItem(first, "Nombre"), 1);
        setColspan(addFormItem(last, "Apellido"), 1);
//        setColspan(addFormItem(createdDate, "Fecha de Creación"), 1);
//        setColspan(addFormItem(userType, ""), 2);
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
}
