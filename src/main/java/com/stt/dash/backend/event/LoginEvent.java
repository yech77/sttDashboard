package com.stt.dash.backend.event;

import com.stt.dash.backend.service.LoginAttemptService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.authentication.event.AuthenticationSuccessEvent;
import org.springframework.stereotype.Component;

@Component
public class LoginEvent implements ApplicationListener<AuthenticationSuccessEvent> {

    private LoginAttemptService loginAttemptService;

    public LoginEvent(@Autowired LoginAttemptService loginAttemptService) {
        this.loginAttemptService = loginAttemptService;
    }

    @Override
    public void onApplicationEvent(AuthenticationSuccessEvent event) {
        UsernamePasswordAuthenticationToken userToken = (UsernamePasswordAuthenticationToken) event.getSource();
        loginAttemptService.loginSucceeded(userToken.getName());
    }
}
