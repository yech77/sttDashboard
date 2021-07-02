package com.stt.dash.app;

import com.stt.dash.backend.data.entity.MyAuditEventComponent;
import com.stt.dash.backend.data.entity.ODashAuditEvent;
import org.springframework.security.core.context.SecurityContextImpl;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;
import java.util.Enumeration;

@Component
public class OSessionListener  implements HttpSessionListener {

    private MyAuditEventComponent auditEvent;

    public OSessionListener(MyAuditEventComponent auditEvent) {
        this.auditEvent = auditEvent;
    }

    @Override
    public void sessionDestroyed(HttpSessionEvent hse) {
        System.out.println("Session destroyed");
//        VaadinSession vs;
        String name = "";
        Enumeration e = hse.getSession().getAttributeNames();
        while (e.hasMoreElements()) {
            Object o = hse.getSession().getAttribute((String) e.nextElement());
            System.out.println("Attributtes of Session-Destroyed: " + o);
//            if (o instanceof VaadinSession) {
//                vs = (VaadinSession) o;
//            }
            if (o instanceof SecurityContextImpl) {
                SecurityContextImpl i = (SecurityContextImpl) o;
                name = i.getAuthentication().getName();
                break;
            }
        }
        auditEvent.add(ODashAuditEvent.OEVENT_TYPE.LOGOUT, name);
        HttpSessionListener.super.sessionDestroyed(hse);
    }

    @Override
    public void sessionCreated(HttpSessionEvent hse) {
        HttpSessionListener.super.sessionCreated(hse);
        System.out.println("Session created");
        String name = "";
        Enumeration e = hse.getSession().getAttributeNames();
        while (e.hasMoreElements()) {
            Object o = hse.getSession().getAttribute((String) e.nextElement());
            System.out.println("Attributtes of Session-Created: " + o);
//            if (o instanceof VaadinSession) {
//                vs = (VaadinSession) o;
//            }
            if (o instanceof SecurityContextImpl) {
                SecurityContextImpl i = (SecurityContextImpl) o;
                name = i.getAuthentication().getName();
                break;
            }
        }
        auditEvent.add(ODashAuditEvent.OEVENT_TYPE.LOGIN_IN, name);
    }

}

