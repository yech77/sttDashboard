package com.stt.dash.backend.event;

import com.stt.dash.backend.service.LoginAttemptService;
import org.springframework.context.ApplicationListener;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.authentication.event.AbstractAuthenticationFailureEvent;
import org.springframework.stereotype.Component;

@Component
public class LoginFailureEvent implements ApplicationListener<AbstractAuthenticationFailureEvent> {

    private LoginAttemptService loginAttemptService;

    public LoginFailureEvent(LoginAttemptService loginAttemptService) {
        this.loginAttemptService = loginAttemptService;
    }

    @Override
    public void onApplicationEvent(AbstractAuthenticationFailureEvent event) {
        UsernamePasswordAuthenticationToken userToken = (UsernamePasswordAuthenticationToken) event.getSource();
        loginAttemptService.loginFailed(userToken.getName());
    }
}
