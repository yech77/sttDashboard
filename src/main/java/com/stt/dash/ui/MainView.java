package com.stt.dash.ui;

import com.stt.dash.app.security.CurrentUser;
import com.stt.dash.ui.smsview.SmsView;
import com.stt.dash.ui.views.HasConfirmation;
import com.stt.dash.ui.views.admin.users.UserAuthoritiesForm;
import com.stt.dash.ui.views.admin.users.v2.UsersViewv2;
import com.stt.dash.ui.views.audit.AuditViewV2;
import com.stt.dash.ui.views.bulksms.BulkSmsView;
import com.stt.dash.ui.views.bulksms.FileToSendFrontView;
import com.stt.dash.ui.views.carrier.CarrierChartView;
import com.stt.dash.ui.views.client.ClientChartView;
import com.stt.dash.ui.views.rol.ORolesView;
import com.stt.dash.ui.views.dashboard.main.MainDashboardView;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.HasComponents;
import com.vaadin.flow.component.applayout.AppLayout;
import com.vaadin.flow.component.applayout.DrawerToggle;
import com.vaadin.flow.component.avatar.Avatar;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.confirmdialog.ConfirmDialog;
import com.vaadin.flow.component.contextmenu.MenuItem;
import com.vaadin.flow.component.contextmenu.SubMenu;
import com.vaadin.flow.component.html.Anchor;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.menubar.MenuBar;
import com.vaadin.flow.component.menubar.MenuBarVariant;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.page.Viewport;
import com.vaadin.flow.component.tabs.Tab;
import com.vaadin.flow.component.tabs.Tabs;
import com.vaadin.flow.router.RouteConfiguration;
import com.vaadin.flow.router.RouterLink;
import com.vaadin.flow.server.PWA;
import com.stt.dash.app.security.SecurityUtils;
import com.stt.dash.ui.views.admin.users.UsersView;
import com.vaadin.flow.server.VaadinServlet;
import com.vaadin.flow.server.VaadinServletRequest;
import com.vaadin.flow.server.VaadinSession;
import liquibase.pro.packaged.T;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static com.stt.dash.ui.utils.BakeryConst.*;

@Viewport(VIEWPORT)
@PWA(name = "Orinoco App Starter", shortName = "Orinoco Dash", startPath = "login", backgroundColor = "#227aef", themeColor = "#227aef", offlinePath = "offline-page.html", offlineResources = {"images/offline-login-banner.jpg"}, enableInstallPrompt = false)
//@PWA(name = "VaadinCRM", shortName = "CRM")
//@Theme(themeFolder = "odashboard")
public class MainView extends AppLayout {
    /* Hora del servidor para establecer busquedas de YYYY-MM-DD*/
    public static LocalDateTime localDateTime = LocalDateTime.now();
    private final ConfirmDialog confirmDialog = new ConfirmDialog();
    //	private final ConfirmDialog confirmDialog = new ConfirmDialog();
    private final Tabs menu;
    private final CurrentUser currentUser;

    public MainView(@Autowired CurrentUser currentUser) {
        final String contextPath = VaadinServlet.getCurrent().getServletContext().getContextPath();
        this.currentUser = currentUser;
        confirmDialog.setCancelable(true);
        confirmDialog.setConfirmButtonTheme("raised tertiary error");
        confirmDialog.setCancelButtonTheme("raised tertiary");

//		this.setDrawerOpened(false);
        H1 title = new H1("Orinoco");
        title.getStyle()
                .set("font-size", "var(--lumo-font-size-l)")
                .set("margin", "0");
//		Span appName = new Span("Orinoco Dash");
//		appName.addClassName("hide-on-mobile");

        menu = createMenuTabs();

//		this.addToNavbar(true, menu);
        /**/
        this.addToNavbar(new DrawerToggle(), title);
        Avatar avatar = avatarMenuBar(currentUser.getUser().getFirstName() + " " + currentUser.getUser().getLastName());
        /**/
        MenuBar menuBar = new MenuBar();
        menuBar.addThemeVariants(MenuBarVariant.LUMO_TERTIARY_INLINE);
        MenuItem menuItem = menuBar.addItem(avatar);
        SubMenu subMenu = menuItem.getSubMenu();
//        subMenu.addItem("Logout", menuItemClickEvent -> logout());
        subMenu.addItem(createLogoutButton(contextPath));
        /**/
        VerticalLayout verticalLayout = new VerticalLayout(menuBar);
        verticalLayout.setId("myHorizontal");
        verticalLayout.setAlignItems(FlexComponent.Alignment.END);
        this.addToNavbar(verticalLayout);
        this.addToDrawer(menu);
        this.getElement().appendChild(confirmDialog.getElement());

        getElement().addEventListener("search-focus", e -> {
            getElement().getClassList().add("hide-navbar");
        });

        getElement().addEventListener("search-blur", e -> {
            getElement().getClassList().remove("hide-navbar");
        });
    }

    @Override
    protected void afterNavigation() {
        super.afterNavigation();
        confirmDialog.setOpened(false);
        if (getContent() instanceof HasConfirmation) {
            ((HasConfirmation) getContent()).setConfirmDialog(confirmDialog);
        }
        RouteConfiguration configuration = RouteConfiguration.forSessionScope();
        if (configuration.isRouteRegistered(this.getContent().getClass())) {
            String target = configuration.getUrl(this.getContent().getClass());
            Optional<Component> tabToSelect = menu.getChildren().filter(tab -> {
                Component child = tab.getChildren().findFirst().get();
                return child instanceof RouterLink && ((RouterLink) child).getHref().equals(target);
            }).findFirst();
            tabToSelect.ifPresent(tab -> menu.setSelectedTab((Tab) tab));
        } else {
            menu.setSelectedTab(null);
        }
    }

    private static Tabs createMenuTabs() {
        final Tabs tabs = new Tabs();
        tabs.setOrientation(Tabs.Orientation.VERTICAL);
        tabs.add(getAvailableTabs());
        return tabs;
    }

    private static Tab[] getAvailableTabs() {
        final List<Tab> tabs = new ArrayList<>(6);

        if (SecurityUtils.isAccessGranted(MainDashboardView.class)) {
            tabs.add(createTab(VaadinIcon.HOME, TITLE_DASHBOARD_MAIN, MainDashboardView.class));
        }

        if (SecurityUtils.isAccessGranted(SmsShowView.class)) {
            tabs.add(createTab(VaadinIcon.ACCORDION_MENU, "Tráfico por cliente", SmsShowView.class));
        }
        if (SecurityUtils.isAccessGranted(ClientChartView.class)) {
            tabs.add(createTab(VaadinIcon.CHART_LINE, TITLE_CLIENT, ClientChartView.class));
        }
        if (SecurityUtils.isAccessGranted(CarrierChartView.class)) {
            tabs.add(createTab(VaadinIcon.CHART_TIMELINE, TITLE_CARRIER, CarrierChartView.class));
        }
        if (SecurityUtils.isAccessGranted(SmsView.class)) {
            tabs.add(createTab(VaadinIcon.ENVELOPES_O, TITLE_SMS_VIEW, SmsView.class));
        }
        if (SecurityUtils.isAccessGranted(BulkSmsView.class)) {
            tabs.add(createTab(VaadinIcon.NEWSPAPER, TITLE_BULKSMS, BulkSmsView.class));
        }

        if (SecurityUtils.isAccessGranted(FileToSendFrontView.class)) {
            tabs.add(createTab(VaadinIcon.CALENDAR_ENVELOPE, TITLE_BULKSMS_SCHEDULER, FileToSendFrontView.class));
        }
        if (SecurityUtils.isAccessGranted(AuditViewV2.class)) {
            tabs.add(createTab(VaadinIcon.CLOCK, TITLE_AUDIT, AuditViewV2.class));
        }

        if (SecurityUtils.isAccessGranted(UsersViewv2.class)) {
            tabs.add(createTab(VaadinIcon.USERS, TITLE_USERS, UsersViewv2.class));
        }
        if (SecurityUtils.isAccessGranted(UserAuthoritiesForm.class)) {
            tabs.add(createTab(VaadinIcon.USER_CHECK, "Permisos", UserAuthoritiesForm.class));
        }
        return tabs.toArray(new Tab[tabs.size()]);
    }

    private static Tab createTab(VaadinIcon icon, String title, Class<? extends Component> viewClass) {
        return createTab(populateLink(new RouterLink(null, viewClass), icon, title));
    }

    private static Tab createTab(Component content) {
        final Tab tab = new Tab();
        tab.add(content);
        return tab;
    }

    public void logout() {
        SecurityContextLogoutHandler logoutHandler = new SecurityContextLogoutHandler();
        logoutHandler.logout(VaadinServletRequest.getCurrent().getHttpServletRequest(), null, null);
    }

    private static Anchor createLogoutLink(String contextPath) {
        final Anchor a = new Anchor();
        a.add(TITLE_LOGOUT);
//        a.setHref(contextPath + "/logout");
        a.setHref("/logout");
        return a;
    }

    private static Button createLogoutButton(String contextPath) {
        final Button logout = new Button(TITLE_LOGOUT);
        /*TODO: CAMBIAR*/
        logout.addClickListener(e -> {
            VaadinSession.getCurrent().getSession().invalidate();
        });
        return logout;
    }

    private static <T extends HasComponents> T populateLink(T a, VaadinIcon vaadinIcon, String title) {
        Icon icon = vaadinIcon.create();
        a.add(icon);
        a.add(title);
        return a;
    }


    public Avatar avatarMenuBar(String name) {
//		String pictureUrl = person.getPictureUrl();

        Avatar avatar = new Avatar(name);
//		avatar.setImage(pictureUrl);


        return avatar;
    }

}