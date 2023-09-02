package com.stt.dash.backend.event;

import com.stt.dash.backend.data.entity.MyAuditEventComponent;
import com.stt.dash.backend.data.entity.ODashAuditEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;
import org.springframework.security.web.authentication.logout.SimpleUrlLogoutSuccessHandler;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
public class LogOutTesting implements LogoutHandler {

    private MyAuditEventComponent auditEventComponent;

    public LogOutTesting(@Autowired MyAuditEventComponent auditEventComponent) {
        this.auditEventComponent = auditEventComponent;
    }

    @Override
    public void logout(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Authentication authentication) {
        if (authentication != null) {
            String ipAddress = httpServletRequest.getHeader("X-FORWARDED-FOR");
            if (ipAddress == null) {
                ipAddress = httpServletRequest.getRemoteAddr();
            }
            auditEventComponent.add(ODashAuditEvent.OEVENT_TYPE.LOGOUT, "desde: " + ipAddress);
        }
    }
}