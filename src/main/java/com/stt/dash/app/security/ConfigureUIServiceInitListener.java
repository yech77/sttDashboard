package com.stt.dash.app.security;

import com.stt.dash.ui.views.PasswordChangeView;
import com.stt.dash.ui.views.dashboard.main.MainDashboardView;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.server.ServiceInitEvent;
import com.vaadin.flow.server.VaadinServiceInitListener;
import com.vaadin.flow.spring.annotation.SpringComponent;
import com.stt.dash.ui.components.OfflineBanner;
import com.stt.dash.ui.exceptions.AccessDeniedException;
import com.stt.dash.ui.views.login.LoginView;

/**
 * Adds before enter listener to check access to views.
 * Adds the Offline banner.
 */
@SpringComponent
public class ConfigureUIServiceInitListener implements VaadinServiceInitListener {

    @Override
    public void serviceInit(ServiceInitEvent event) {
        event.getSource().addUIInitListener(uiEvent -> {
            final UI ui = uiEvent.getUI();
            ui.add(new OfflineBanner());
            ui.addBeforeEnterListener(this::beforeEnter);
        });
    }

    /**
     * Reroutes the user if she is not authorized to access the view.
     *
     * @param event before navigation event with event details
     */
    private void beforeEnter(BeforeEnterEvent event) {
        final boolean accessGranted = SecurityUtils.isAccessGranted(event.getNavigationTarget());
        reRouteToMainIfItIsLoadingPAsswordChangeView(event);
        if (!accessGranted) {
            if (SecurityUtils.isUserLoggedIn()) {
                event.rerouteToError(AccessDeniedException.class);
            } else {
                event.rerouteTo(LoginView.class);
            }
        }
    }

    /**
     * Si se esta cargando PasswordChangeView inmediatamente despues de hacer Login,
     * se re enruta al Main.
     *
     * @param event
     */
    private void reRouteToMainIfItIsLoadingPAsswordChangeView(BeforeEnterEvent event) {
        if (event.getTrigger().name().equals("PAGE_LOAD") && PasswordChangeView.class.equals(event.getNavigationTarget())) {
            event.rerouteTo(MainDashboardView.class);
        }
    }
}
