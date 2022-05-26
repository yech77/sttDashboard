package com.stt.dash.backend.event;

import com.stt.dash.backend.data.entity.MyAuditEventComponent;
import com.stt.dash.backend.data.entity.ODashAuditEvent;
import com.stt.dash.backend.service.LoginAttemptService;
import org.springframework.context.ApplicationListener;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.authentication.event.AbstractAuthenticationFailureEvent;
import org.springframework.stereotype.Component;

@Component
public class LoginFailureEvent implements ApplicationListener<AbstractAuthenticationFailureEvent> {

    private LoginAttemptService loginAttemptService;
    private final MyAuditEventComponent auditEventComponent;

    public LoginFailureEvent(LoginAttemptService loginAttemptService,
                             MyAuditEventComponent auditEventComponent) {
        this.loginAttemptService = loginAttemptService;
        this.auditEventComponent = auditEventComponent;
    }

    @Override
    public void onApplicationEvent(AbstractAuthenticationFailureEvent event) {
        UsernamePasswordAuthenticationToken userToken = (UsernamePasswordAuthenticationToken) event.getSource();
        if (loginAttemptService.isBlocked(userToken.getName())) {
            return;
        }
        loginAttemptService.loginFailed(userToken.getName());
        if (loginAttemptService.isBlocked(userToken.getName())) {
            auditEventComponent.add(ODashAuditEvent.OEVENT_TYPE.BLOCKED, "Bloqueo por límite de intentos fallido alcanzado.");
        }
    }
}
