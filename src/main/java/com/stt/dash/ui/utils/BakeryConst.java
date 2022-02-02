package com.stt.dash.ui.utils;

import java.util.Locale;

import org.springframework.data.domain.Sort;

public class BakeryConst {

    public static final Locale APP_LOCALE = new Locale("es", "VE");

    public static final String ORDER_ID = "orderID";
    public static final String EDIT_SEGMENT = "edit";

    public static final String PAGE_ROOT = "";
    public static final String PAGE_STOREFRONT = "storefront";
    public static final String PAGE_STOREFRONT_ORDER_TEMPLATE =
            PAGE_STOREFRONT + "/:" + ORDER_ID + "?";
    public static final String PAGE_STOREFRONT_ORDER_EDIT_TEMPLATE =
            PAGE_STOREFRONT + "/:" + ORDER_ID + "/" + EDIT_SEGMENT;
    public static final String PAGE_STOREFRONT_ORDER_EDIT =
            "storefront/%d/edit";

    public static final String PAGE_BULK_STOREFRONT = "bulksmsfront";
    public static final String PAGE_BULK_STOREFRONT_ORDER_TEMPLATE =
            PAGE_BULK_STOREFRONT + "/:" + ORDER_ID + "?";
    public static final String PAGE_BULK_STOREFRONT_ORDER_EDIT_TEMPLATE =
            PAGE_BULK_STOREFRONT + "/:" + ORDER_ID + "/" + EDIT_SEGMENT;
    public static final String PAGE_BULK_STOREFRONT_ORDER_EDIT =
            "bulksmsfront/%d/edit";
    public static final String PAGE_DASHBOARD = "dashboard";
    public static final String PAGE_CARRIER = "carrier";
    public static final String PAGE_CLIENT = "client";
    public static final String PAGE_SMS_VIEW = "sms-view";
    public static final String PAGE_DASHBOARD_MAIN = "dashboard-main";
    public static final String PAGE_USERS = "users";
    public static final String PAGE_ROLES = "roles";
    public static final String PAGE_AUDIT = "audit";
    public static final String PAGE_BULKSMS = "bulk-sms";
    public static final String PAGE_BULKSMS_SCHEDULER = "bulk-sms-scheduler";
    public static final String PAGE_PRODUCTS = "products";

    public static final String TITLE_BULK_SCHEDULER = "Programar Masivos";
    public static final String TITLE_DASHBOARD = "Dashboard";
    public static final String TITLE_CARRIER = "Evolución Operadora";
    public static final String TITLE_CLIENT = "Evolución Cliente";
    public static final String TITLE_SMS_VIEW = "Busqueda de Mensajes";
    public static final String TITLE_DASHBOARD_MAIN = "Dashboard";
    public static final String TITLE_USERS = "Usuarios";
    public static final String TITLE_AUDIT = "Auditoria";
    public static final String TITLE_BULKSMS_SCHEDULER = "Programar Masivos";
    public static final String TITLE_BULKSMS = "Masivos";
    public static final String TITLE_ROLES = "Roles";
    public static final String TITLE_PRODUCTS = "Products";
    public static final String TITLE_LOGOUT = "Logout";
    public static final String TITLE_NOT_FOUND = "Page was not found";
    public static final String TITLE_ACCESS_DENIED = "Access denied";

    public static final String[] ORDER_SORT_FIELDS = {"dueDate", "dueTime", "id"};
    public static final String[] BULK_SORT_FIELDS = {"dateToSend", "id"};
    public static final Sort.Direction DEFAULT_SORT_DIRECTION = Sort.Direction.ASC;

    public static final String VIEWPORT = "width=device-	width, minimum-scale=1, initial-scale=1, user-scalable=yes, viewport-fit=cover";

    // Mutable for testing.
    public static int NOTIFICATION_DURATION = 4000;

}
