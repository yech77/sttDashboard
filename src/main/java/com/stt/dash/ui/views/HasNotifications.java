package com.stt.dash.ui.views;

import com.vaadin.flow.component.HasElement;
import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.Notification.Position;
import com.stt.dash.ui.utils.BakeryConst;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;

/**
 * Interface for views showing notifications to users
 */
public interface HasNotifications extends HasElement {

    default void showNotification(String message) {
        showNotification(message, false);
    }

    default void showNotification(String message, boolean persistent) {
        if (persistent) {
            Notification notification = getNotification(message);
            notification.open();
        } else {
            Notification.show(message, BakeryConst.NOTIFICATION_DURATION, Position.TOP_CENTER);
        }
    }

    default void showNotificationInformation(String message) {
        showNotificationInformation(message, false);
    }

    default void showNotificationError(String message) {
        showNotificationError(message, false);
    }

    default void showNotificationSuccess(String message) {
        showNotificationSuccess(message, false);
    }

    default void showNotificationInformation(String message, boolean persistent) {
        if (persistent) {
            Notification notification = getNotification(message);
            notification.open();
        } else {
            NotificationVariant lumoPrimary = NotificationVariant.LUMO_PRIMARY;
            Icon icon = VaadinIcon.INFO_CIRCLE_O.create();
            Notification notification = getNotification(message, lumoPrimary, icon);
            notification.setPosition(Position.TOP_CENTER);
            notification.open();
        }
    }

    default void showNotificationSuccess(String message, boolean persistent) {
        if (persistent) {
            Notification notification = getNotification(message);
            notification.open();
        } else {
            NotificationVariant lumoPrimary = NotificationVariant.LUMO_SUCCESS;
            Icon icon = VaadinIcon.CHECK_CIRCLE.create();
            Notification notification = getNotification(message, lumoPrimary, icon);
            notification.setPosition(Position.TOP_CENTER);
            notification.open();
        }
    }

    default void showNotificationError(String message, boolean persistent) {
        if (persistent) {
            Notification notification = getNotification(message);
            notification.open();
        } else {
            NotificationVariant lumoPrimary = NotificationVariant.LUMO_ERROR;
            Icon icon = VaadinIcon.WARNING.create();
            Notification notification = getNotification(message, lumoPrimary, icon);
            notification.setPosition(Position.TOP_CENTER);
            notification.open();
        }
    }

    private static Notification getNotification(String message) {
        Button close = new Button("Cerrar");
        close.getElement().setAttribute("theme", "tertiary small error");
        Notification notification = new Notification(new Text(message), close);
        notification.setPosition(Position.TOP_CENTER);
        notification.setDuration(0);
        close.addClickListener(event -> notification.close());
        return notification;
    }

    private static Notification getNotification(String message, NotificationVariant lumoPrimary, Icon icon) {
        Text text = new Text(message);
        HorizontalLayout v = new HorizontalLayout(icon, new Div(text));
        v.setAlignItems(FlexComponent.Alignment.CENTER);
        Notification notification = new Notification();
        notification.addThemeVariants(lumoPrimary);
        notification.add(v);
        notification.setDuration(BakeryConst.NOTIFICATION_DURATION);
        return notification;
    }
}
