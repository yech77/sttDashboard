package com.stt.dash.ui.views;

import com.stt.dash.app.security.CurrentUser;
import com.stt.dash.backend.data.entity.User;
import com.stt.dash.ui.views.dashboard.DashboardView;
import com.vaadin.flow.component.littemplate.LitTemplate;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.BeforeLeaveEvent;
import com.vaadin.flow.router.BeforeLeaveObserver;
import org.apache.commons.lang3.ObjectUtils;

public abstract class BaseFom extends LitTemplate implements BeforeEnterObserver, BeforeLeaveObserver, HasNotifications {
    private final CurrentUser currentUser;
    private final User.OUSER_TYPE_ORDINAL typeOrdinal;

    public BaseFom(CurrentUser currentUser) {
        this.currentUser = currentUser;
        typeOrdinal = getTypeOrdinal(currentUser.getUser());
    }

    @Override
    public void beforeEnter(BeforeEnterEvent beforeEnterEvent) {
        doBeforeEnter(beforeEnterEvent);
    }

    public void doBeforeEnter(BeforeEnterEvent beforeEnterEvent) {

    }

    private User.OUSER_TYPE_ORDINAL getTypeOrdinal(User user) {
        return user.getUserTypeOrd();
    }

    @Override
    public void beforeLeave(BeforeLeaveEvent beforeLeaveEvent) {
        doBeforeLeave(beforeLeaveEvent);
    }

    public void doBeforeLeave(BeforeLeaveEvent beforeLeaveEvent) {
    }
}
