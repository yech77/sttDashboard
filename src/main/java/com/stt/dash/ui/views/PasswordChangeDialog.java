package com.stt.dash.ui.views;

import com.stt.dash.app.security.SecurityUtils;
import com.stt.dash.uiv2.components.detailsdrawer.DetailsDrawerFooterStt;
import com.stt.dash.uiv2.util.LumoStyles;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.textfield.PasswordField;

public class PasswordChangeDialog extends Dialog {
    private final FormLayout formLayout = new FormLayout();
    private final PasswordField oldPassword = new PasswordField("Old Password");
    private final PasswordField newPassword = new PasswordField("New Password");
    private final PasswordField confirmPassword = new PasswordField("Confirm Password");
    private final DetailsDrawerFooterStt footer = new DetailsDrawerFooterStt();
    private final PasswordChangePresenter passwordChangePresenter;

    public PasswordChangeDialog(PasswordChangePresenter passwordChangePresenter) {
        this.passwordChangePresenter = passwordChangePresenter;
        formLayout.addClassNames(LumoStyles.Padding.Bottom.L, LumoStyles.Padding.Horizontal.L, LumoStyles.Padding.Top.S);
        formLayout.setResponsiveSteps(new FormLayout.ResponsiveStep("0", 1, FormLayout.ResponsiveStep.LabelsPosition.TOP), new FormLayout.ResponsiveStep("21em", 1, FormLayout.ResponsiveStep.LabelsPosition.TOP));
        formLayout.add(oldPassword, newPassword, confirmPassword, footer);
        add(formLayout);
        footer.addSaveListener(e -> {
            if (newPassword.getValue().equals(confirmPassword.getValue())) {
                passwordChangePresenter.changePassword(oldPassword.getValue(), newPassword.getValue());
                close();

            }
        });
    }

}
