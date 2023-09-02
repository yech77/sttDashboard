package com.stt.dash.ui.views.login;

import com.stt.dash.ui.MainView;
import com.stt.dash.ui.views.dashboard.main.MainDashboardView;
import com.vaadin.flow.component.dependency.JsModule;
import com.vaadin.flow.component.login.LoginI18n;
import com.vaadin.flow.component.login.LoginOverlay;
import com.vaadin.flow.component.page.Viewport;
import com.vaadin.flow.router.AfterNavigationEvent;
import com.vaadin.flow.router.AfterNavigationObserver;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.stt.dash.app.security.SecurityUtils;
import com.stt.dash.ui.utils.BakeryConst;
import com.stt.dash.ui.views.storefront.StorefrontView;

import java.util.HashMap;
import java.util.Map;


@Route
@PageTitle("Portal cliente STT")
@JsModule("./styles/shared-styles.js")
//@Viewport(BakeryConst.VIEWPORT)
public class LoginView extends LoginOverlay
        implements AfterNavigationObserver, BeforeEnterObserver {

    public LoginView() {
        LoginI18n i18n = LoginI18n.createDefault();
        i18n.setHeader(new LoginI18n.Header());
        i18n.getHeader().setTitle("Orinoco");

        i18n.getHeader().setDescription("Portal del cliente - Soluciones TextTech");
        i18n.setAdditionalInformation(null);
        i18n.setForm(new LoginI18n.Form());
        i18n.getForm().setSubmit("Entrar");
        i18n.getForm().setTitle("Iniciar sesión");
        i18n.getForm().setUsername("Correo");
        i18n.getForm().setPassword("Clave");
        i18n.getErrorMessage().setTitle("Ha ocurrido un error");
        i18n.getErrorMessage().setMessage("Usuario o clave inválido");

        setI18n(i18n);
        setForgotPasswordButtonVisible(false);
        setAction("login");
    }

    @Override
    public void beforeEnter(BeforeEnterEvent event) {
        if (SecurityUtils.isUserLoggedIn()) {
            event.forwardTo(MainDashboardView.class);
        } else {
            setOpened(true);
        }
    }

    @Override
    public void afterNavigation(AfterNavigationEvent event) {
        setError(event.getLocation().getQueryParameters().getParameters().containsKey(
                "error"));
    }

}
