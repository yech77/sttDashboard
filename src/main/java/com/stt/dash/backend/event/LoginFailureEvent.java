package com.stt.dash.backend.event;

import com.stt.dash.backend.service.LoginAttemptService;
import com.stt.dash.backend.service.UserService;
import org.springframework.context.ApplicationListener;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.authentication.event.AbstractAuthenticationFailureEvent;
import org.springframework.stereotype.Component;

@Component
public class LoginFailureEvent implements ApplicationListener<AbstractAuthenticationFailureEvent> {

    private final LoginAttemptService loginAttemptService;

    private final UserService userService;

    public LoginFailureEvent(LoginAttemptService loginAttemptService, UserService userService) {
        this.loginAttemptService = loginAttemptService;
        this.userService = userService;
    }

    @Override
    public void onApplicationEvent(AbstractAuthenticationFailureEvent event) {
        UsernamePasswordAuthenticationToken userToken = (UsernamePasswordAuthenticationToken) event.getSource();
        loginAttemptService.loginFailed(userToken.getName());
        if (loginAttemptService.isBlocked(userToken.getName())) {
            userService.deactivateUser(userToken.getName(), "Bloqueo por límite de intentos fallido alcanzado.");
            loginAttemptService.pullOutUserOfAttemps(userToken.getName());
        }
    }
}
