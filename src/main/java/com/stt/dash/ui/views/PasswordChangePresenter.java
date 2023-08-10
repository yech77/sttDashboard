package com.stt.dash.ui.views;

import com.stt.dash.app.security.CurrentUser;
import com.stt.dash.backend.data.entity.User;
import com.stt.dash.backend.service.UserService;
import com.vaadin.flow.server.VaadinSession;
import org.springframework.security.crypto.password.PasswordEncoder;

public class PasswordChangePresenter {
    private final UserService userService;
    private final CurrentUser currentUser;
    private final PasswordEncoder passwordEncoder;

    public PasswordChangePresenter(CurrentUser currentUser, UserService userService, PasswordEncoder passwordEncoder) {
        this.userService = userService;
        this.currentUser = currentUser;
        this.passwordEncoder = passwordEncoder;
    }

    public void changePassword(String oldPassword, String newPassword) {
        if (isMatches(oldPassword)) {
            User user = currentUser.getUser();
            user.setPasswordHash(passwordEncoder.encode(newPassword));
            userService.changePassword(user);
            VaadinSession.getCurrent().getSession().invalidate();
        }
    }

    public boolean isMatches(String oldPassword) {
        return passwordEncoder.matches(oldPassword, currentUser.getUser().getPasswordHash());
    }

}
