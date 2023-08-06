package com.stt.dash.ui.views;

import com.stt.dash.app.security.SecurityUtils;
import com.stt.dash.ui.views.dashboard.main.MainDashboardView;
import com.stt.dash.uiv2.components.detailsdrawer.DetailsDrawerFooterStt;
import com.stt.dash.uiv2.util.LumoStyles;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.data.value.ValueChangeMode;

public class PasswordChangeDialog extends Dialog {
    private final FormLayout formLayout = new FormLayout();
    private final PasswordField oldPassword = new PasswordField("Old Password");
    private final PasswordField newPassword = new PasswordField("New Password");
    private final PasswordField confirmPassword = new PasswordField("Confirm Password");
    private final DetailsDrawerFooterStt footer = new DetailsDrawerFooterStt();

    public PasswordChangeDialog(PasswordChangePresenter passwordChangePresenter) {
        formLayout.addClassNames(LumoStyles.Padding.Bottom.L, LumoStyles.Padding.Horizontal.L, LumoStyles.Padding.Top.S);
        formLayout.setResponsiveSteps(new FormLayout.ResponsiveStep("0", 1, FormLayout.ResponsiveStep.LabelsPosition.TOP), new FormLayout.ResponsiveStep("21em", 1, FormLayout.ResponsiveStep.LabelsPosition.TOP));
        formLayout.add(oldPassword, newPassword, confirmPassword, footer);
        add(formLayout);
        footer.disableSave();
        /**/
        oldPassword.setRequired(true);
        newPassword.setRequired(true);
        confirmPassword.setRequired(true);
        /**/
        oldPassword.setValueChangeMode(ValueChangeMode.LAZY);
        oldPassword.addValueChangeListener(e -> {
            if (!passwordChangePresenter.isMatches(oldPassword.getValue())) {
                oldPassword.setInvalid(true);
                oldPassword.setErrorMessage("Contraseña incorrecta");
            } else {
                oldPassword.setInvalid(false);
            }
            validForEnableSave();
        });
        newPassword.setPattern("^(|(?=.*\\d)(?=.*[a-z])(?=.*[A-Z]).{6,})$");
        newPassword.setHelperText("Mínimo 6 caracteres y al menos 1 dígito, 1 minúscula y 1 mayúscula");
        newPassword.setErrorMessage("No cumple con el formato requerido");
        newPassword.addValueChangeListener(e -> validForEnableSave());
        confirmPassword.setValueChangeMode(ValueChangeMode.LAZY);
        confirmPassword.addValueChangeListener(e -> {
            if (!newPassword.getValue().equals(confirmPassword.getValue())) {
                confirmPassword.setInvalid(true);
                confirmPassword.setErrorMessage("Las contraseñas no coinciden");
            } else {
                confirmPassword.setInvalid(false);
            }
            validForEnableSave();
        });
        footer.addSaveListener(e -> {
            if (newPassword.getValue().equals(confirmPassword.getValue())) {
                passwordChangePresenter.changePassword(oldPassword.getValue(), newPassword.getValue());
                close();
            }
        });
        footer.addCancelListener(e -> {
            getUI().ifPresent(ui -> ui.navigate(MainDashboardView.class));
            close();
        });
    }

    /**
     * Valida si los campos son válidos para habilitar el botón de guardar
     */
    private void validForEnableSave() {
        if (!isValid(oldPassword) || !isValid(newPassword) || !isValid(confirmPassword)) {
            footer.disableSave();
        } else {
            footer.enableSave();
        }
    }

    /**
     * Valida si el campo es válido y no está vacío
     *
     * @param passwordField
     * @return
     */
    private boolean isValid(PasswordField passwordField) {
        return !passwordField.isEmpty() && !passwordField.isInvalid();
    }
}
