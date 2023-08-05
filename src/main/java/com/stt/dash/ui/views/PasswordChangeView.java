package com.stt.dash.ui.views;

import com.stt.dash.app.security.CurrentUser;
import com.stt.dash.backend.service.UserService;
import com.stt.dash.ui.MainView;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import org.springframework.security.crypto.password.PasswordEncoder;

@Route(value = "user-password-change", layout = MainView.class)
@PageTitle("BakeryConst.TITLE_SMS_SHOW_VIEW")
//@Secured({Role.ADMIN, "UI_PERMISSIONS"})
public class PasswordChangeView extends Div {
    private final UserService userService;
    private final CurrentUser currentUser;

    public PasswordChangeView(CurrentUser currentUser, UserService userService, PasswordEncoder passwordEncoder) {
        this.userService = userService;
        this.currentUser = currentUser;
        PasswordChangePresenter passwordChangePresenter = new PasswordChangePresenter(currentUser, userService, passwordEncoder);
        PasswordChangeDialog passwordChangeDialog = new PasswordChangeDialog(passwordChangePresenter);
        passwordChangeDialog.open();
        passwordChangeDialog.setCloseOnOutsideClick(true);
    }
}
