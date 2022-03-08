package com.stt.dash.ui;

import com.stt.dash.app.security.CurrentUser;
import com.stt.dash.backend.data.entity.User;
import com.stt.dash.ui.smsview.SmsView;
import com.stt.dash.ui.views.HasConfirmation;
import com.stt.dash.ui.views.audit.AuditView;
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
import com.vaadin.flow.component.avatar.AvatarVariant;
import com.vaadin.flow.component.confirmdialog.ConfirmDialog;
import com.vaadin.flow.component.contextmenu.ContextMenu;
import com.vaadin.flow.component.dependency.NpmPackage;
import com.vaadin.flow.component.html.Anchor;
import com.vaadin.flow.component.html.Footer;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.H6;
import com.vaadin.flow.component.html.ListItem;
import com.vaadin.flow.component.html.Nav;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.html.UnorderedList;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.page.Viewport;
import com.vaadin.flow.component.tabs.Tab;
import com.vaadin.flow.component.tabs.TabVariant;
import com.vaadin.flow.component.tabs.Tabs;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.RouterLink;
import com.vaadin.flow.server.PWA;
import com.stt.dash.app.security.SecurityUtils;
import com.stt.dash.ui.views.admin.users.UsersView;
import com.vaadin.flow.server.VaadinServletRequest;
import com.vaadin.flow.theme.Theme;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static com.stt.dash.ui.utils.BakeryConst.*;

@Viewport(VIEWPORT)
@PWA(name = "Orinoco App Starter", shortName = "Orinoco Dash",
        startPath = "login",
        backgroundColor = "#227aef", themeColor = "#227aef",
        offlinePath = "offline-page.html",
        offlineResources = {"images/offline-login-banner.jpg"},
        enableInstallPrompt = false)
//@PWA(name = "VaadinCRM", shortName = "CRM")
@Theme(themeFolder = "odashboard")
public class MainView extends AppLayout {

    /* Hora del servidor para establecer busquedas de YYYY-MM-DD*/
    public static LocalDateTime localDateTime = LocalDateTime.now();
    private final ConfirmDialog confirmDialog = new ConfirmDialog();
//	private final Tabs menu;

    /**
     * A simple navigation item component, based on ListItem element.
     */
    public static class MenuItemInfo extends ListItem {

        private final Class<? extends Component> view;

        public MenuItemInfo(String menuTitle, String iconClass, Class<? extends Component> view) {
            this.view = view;
            RouterLink link = new RouterLink();
            // Use Lumo classnames for various styling
            link.addClassNames("flex", "mx-s", "p-s", "relative", "text-secondary");
            link.setRoute(view);

            Span text = new Span(menuTitle);
            // Use Lumo classnames for various styling
            text.addClassNames("font-medium", "text-s");

            link.add(new LineAwesomeIcon(iconClass), text);
            add(link);
        }

        public Class<?> getView() {
            return view;
        }

        /**
         * Simple wrapper to create icons using LineAwesome iconset. See
         * https://icons8.com/line-awesome
         */
        @NpmPackage(value = "line-awesome", version = "1.3.0")
        public static class LineAwesomeIcon extends Span {
            public LineAwesomeIcon(String lineawesomeClassnames) {
                // Use Lumo classnames for suitable font size and margin
                addClassNames("me-s", "text-l");
                if (!lineawesomeClassnames.isEmpty()) {
                    addClassNames(lineawesomeClassnames);
                }
            }
        }
    }

    private H6 viewTitle;

    //	private AuthenticatedUser authenticatedUser;
//	private AccessAnnotationChecker accessChecker;
    private CurrentUser currentUser;

    public void MainView(CurrentUser currentUser) {
        setPrimarySection(Section.DRAWER);
        addToNavbar(true, createHeaderContent());
        addToDrawer(createDrawerContent());
//
//		confirmDialog.setCancelable(true);
//		confirmDialog.setConfirmButtonTheme("raised tertiary error");
//		confirmDialog.setCancelButtonTheme("raised tertiary");
//
//		this.setDrawerOpened(false);
//		Span appName = new Span(currentUser.getUser().getFirstName() + " "
//				+ currentUser.getUser().getLastName());
//		appName.addClassName("hide-on-mobile");
//
//		menu = createMenuTabs();
//		setPrimarySection(Section.DRAWER);
//		this.addToNavbar(appName);
//		this.addToDrawer( menu);
//		this.getElement().appendChild(confirmDialog.getElement());
//
//		getElement().addEventListener("search-focus", e -> {
//			getElement().getClassList().add("hide-navbar");
//		});
//
//		getElement().addEventListener("search-blur", e -> {
//			getElement().getClassList().remove("hide-navbar");
//		});
    }

    public MainView(CurrentUser currentUser) {
//		this.authenticatedUser = authenticatedUser;
//		this.accessChecker = accessChecker;
        this.currentUser = currentUser;
        if (!SecurityUtils.isUserLoggedIn()) {
            return;
        }
        setPrimarySection(Section.DRAWER);
        Footer f = createFooter();
        f.getElement().getStyle().set("margin", "auto");
        addToNavbar(true, createHeaderContent(), f);
        addToDrawer(createDrawerContent());
    }


    private Component createHeaderContent() {
        DrawerToggle toggle = new DrawerToggle();
//		toggle.addClassName("text-secondary");
//		toggle.addThemeVariants(ButtonVariant.LUMO_CONTRAST);
//		toggle.getElement().setAttribute("aria-label", "Menu toggle");

        viewTitle = new H6();
//		viewTitle.addClassNames("m-0", "text-l");
        viewTitle.getStyle()
                .set("font-size", "var(--lumo-font-size-l)")
                .set("margin", "0");
        HorizontalLayout header = new HorizontalLayout(toggle, viewTitle);
        header.addClassNames("bg-base", "border-b", "border-contrast-10", "box-border", "flex", "h-xl", "items-center",
                "w-full");
        header.setAlignItems(FlexComponent.Alignment.CENTER);

        return header;
    }

    private Component createDrawerContent() {
        H2 appName = new H2("Orinoco Dash");
        appName.addClassNames("flex", "items-center", "h-xl", "m-0", "px-m", "text-m");

        com.vaadin.flow.component.html.Section section = new com.vaadin.flow.component.html.Section(appName,
                createMenuTabs());
        section.addClassNames("flex", "flex-col", "items-stretch", "max-h-full", "min-h-full");
        return section;
    }

    private Nav createNavigation() {
        Nav nav = new Nav();
        nav.addClassNames("border-b", "border-contrast-10", "flex-grow", "overflow-auto");
        nav.getElement().setAttribute("aria-labelledby", "views");

        // Wrap the links in a list; improves accessibility
        UnorderedList list = new UnorderedList();
        list.addClassNames("list-none", "m-0", "p-0");
        nav.add(list);

        for (MenuItemInfo menuItem : createMenuItems()) {
            list.add(menuItem);

        }
        return nav;
    }

    private MenuItemInfo[] createMenuItems() {
        return new MenuItemInfo[]{ //
                new MenuItemInfo("Evolucion Cliente", "la la-chart-line", ClientChartView.class), //
                new MenuItemInfo("Usuarios", "la la-user", UsersView.class), //
                new MenuItemInfo("Roles", "la la-users", ORolesView.class), //
                new MenuItemInfo("Cargar Masivos", "la la-user", BulkSmsView.class), //
                new MenuItemInfo("Programar Masivos", "la la-file", FileToSendFrontView.class), //
                new MenuItemInfo("Dashboar Principal", "la la-chart-area", MainDashboardView.class), //

//                new MenuItemInfo("Hello World", "la la-globe", HelloWorldView.class), //

//				new MenuItemInfo("About", "la la-file", AboutView.class), //

//                new MenuItemInfo("Dashboard", "la la-chart-area", DashboardView.class), //

//                new MenuItemInfo("Card List", "la la-list", CardListView.class), //

//                new MenuItemInfo("List", "la la-th", ListView.class), //

//                new MenuItemInfo("Master-Detail", "la la-columns", MasterDetailView.class), //

//                new MenuItemInfo("Person Form", "la la-user", PersonFormView.class), //

//                new MenuItemInfo("Address Form", "la la-map-marker", AddressFormView.class), //

//                new MenuItemInfo("Credit Card Form", "", CreditCardFormView.class), //

//                new MenuItemInfo("Image List", "la la-th-list", ImageListView.class), //

//                new MenuItemInfo("Checkout Form", "", CheckoutFormView.class), //

        };
    }

//    private static RouterLink createLink(MenuItemInfo menuItemInfo) {
//        RouterLink link = new RouterLink();
//        link.addClassNames("flex", "mx-s", "p-s", "relative", "text-secondary");
//        link.setRoute(menuItemInfo.getView());
//
//        Span icon = new Span();
//        icon.addClassNames("me-s", "text-l");
//        if (!menuItemInfo.getIconClass().isEmpty()) {
//            icon.addClassNames(menuItemInfo.getIconClass());
//        }
//
//        Span text = new Span(menuItemInfo.getText());
//        text.addClassNames("font-medium", "text-s");
//
//        link.add(icon, text);
//        return link;
//    }

    private Footer createFooter() {
        Footer layout = new Footer();
        layout.addClassNames("flex", "items-center", "my-s", "px-m", "py-xs");

//		Optional<com.stt.dash.backend.data.entity.User> maybeUser = currentUser.getUser();
        User user = currentUser.getUser();
//		if (maybeUser.isPresent()) {
//			User user = maybeUser.get();

        Avatar avatar = new Avatar(user.getFirstName() + " " + user.getLastName());
        avatar.addThemeVariants(AvatarVariant.LUMO_LARGE);

//        MenuBar menuBar = new MenuBar();
//        menuBar.addThemeVariants(MenuBarVariant.LUMO_TERTIARY_INLINE);
//
//        MenuItem menuItem = menuBar.addItem(avatar);
//        SubMenu subMenu = menuItem.getSubMenu();
//        subMenu.addItem("Profile");
//        subMenu.addItem("Settings");
//        subMenu.addItem("Help");
//        subMenu.addItem("Sign out", e -> {
//            logout();
//        });


        ContextMenu userMenu = new ContextMenu(avatar);
        userMenu.setOpenOnClick(true);
        userMenu.addItem("Logout", e -> {
            logout();
        });
        userMenu.addItem("Settings", e -> {
            Notification none = new Notification();
            none.setText("Por incluir");
            none.setDuration(1500);
            none.open();
        });

//        Span name = new Span(user.getFirstName());
//        name.addClassNames("font-medium", "text-s", "text-secondary");

        layout.add(avatar);
//		} else {
//			Anchor loginLink = new Anchor("login", "Sign in");
//			layout.add(loginLink);
//		}

        return layout;
    }

    @Override
    protected void afterNavigation() {
        super.afterNavigation();
        confirmDialog.setOpened(false);
        if (getContent() instanceof HasConfirmation) {
            ((HasConfirmation) getContent()).setConfirmDialog(confirmDialog);
        }
        viewTitle.setText(getCurrentPageTitle());
    }

    private String getCurrentPageTitle() {
        PageTitle title = getContent().getClass().getAnnotation(PageTitle.class);
        return title == null ? "" : title.value();
    }


    public void logout() {
//        UI.getCurrent().getPage().setLocation(SecurityConfiguration.LOGOUT_URL);
        SecurityContextLogoutHandler logoutHandler = new SecurityContextLogoutHandler();
        logoutHandler.logout(VaadinServletRequest.getCurrent().getHttpServletRequest(), null, null);
    }


//	@Override
//	protected void afterNavigation() {
//		super.afterNavigation();
//		confirmDialog.setOpened(false);
//		if (getContent() instanceof HasConfirmation) {
//			((HasConfirmation) getContent()).setConfirmDialog(confirmDialog);
//		}
//		RouteConfiguration configuration = RouteConfiguration.forSessionScope();
//		if (configuration.isRouteRegistered(this.getContent().getClass())) {
//			String target = configuration.getUrl(this.getContent().getClass());
//			Optional < Component > tabToSelect = menu.getChildren().filter(tab -> {
//				Component child = tab.getChildren().findFirst().get();
//				return child instanceof RouterLink && ((RouterLink) child).getHref().equals(target);
//			}).findFirst();
//			tabToSelect.ifPresent(tab -> menu.setSelectedTab((Tab) tab));
//		} else {
//			menu.setSelectedTab(null);
//		}
//	}

    private static Tabs createMenuTabs() {
        final Tabs tabs = new Tabs();
        tabs.setOrientation(Tabs.Orientation.VERTICAL);
        tabs.add(getAvailableTabs());
        return tabs;
    }

    private static Tab[] getAvailableTabs() {
        final List<Tab> tabs = new ArrayList<>(6);
//		tabs.add(createTab(VaadinIcon.EDIT, TITLE_STOREFRONT,
//						StorefrontView.class));
        tabs.add(createTab(VaadinIcon.ENVELOPES_O, TITLE_SMS_VIEW,
                SmsView.class));
//		tabs.add(createTab(VaadinIcon.CLOCK,TITLE_DASHBOARD, DashboardView.class));
//		if (SecurityUtils.isAccessGranted(MainDashboardView.class)) {
//		}
        tabs.add(createTab(VaadinIcon.HOME, TITLE_DASHBOARD_MAIN, MainDashboardView.class));
        tabs.add(createTab(VaadinIcon.ACCORDION_MENU, "Mensajes enviados", SmsShowView.class));
        if (SecurityUtils.isAccessGranted(FileToSendFrontView.class)) {
            tabs.add(createTab(VaadinIcon.CALENDAR_ENVELOPE, TITLE_BULKSMS_SCHEDULER,
                    FileToSendFrontView.class));
        }
        if (SecurityUtils.isAccessGranted(BulkSmsView.class)) {
            tabs.add(createTab(VaadinIcon.USER, TITLE_BULKSMS, BulkSmsView.class));
        }
        if (SecurityUtils.isAccessGranted(ClientChartView.class)) {
            tabs.add(createTab(VaadinIcon.CHART_LINE, TITLE_CLIENT, ClientChartView.class));
        }
        if (SecurityUtils.isAccessGranted(CarrierChartView.class)) {
            tabs.add(createTab(VaadinIcon.CHART_TIMELINE, TITLE_CARRIER, CarrierChartView.class));
        }
        if (SecurityUtils.isAccessGranted(UsersView.class)) {
            tabs.add(createTab(VaadinIcon.USER, TITLE_USERS, UsersView.class));
        }
        if (SecurityUtils.isAccessGranted(ORolesView.class)) {
            tabs.add(createTab(VaadinIcon.KEY, TITLE_ROLES, ORolesView.class));
        }
        if (SecurityUtils.isAccessGranted(AuditView.class)) {
            tabs.add(createTab(VaadinIcon.CLOCK, TITLE_AUDIT,
                    AuditView.class));
        }
//		if (SecurityUtils.isAccessGranted(ProductsView.class)) {
//			tabs.add(createTab(VaadinIcon.CALENDAR, TITLE_PRODUCTS, ProductsView.class));
//		}
//		final String contextPath = VaadinServlet.getCurrent().getServletContext().getContextPath();
//		final Tab logoutTab = createTab(createLogoutLink(contextPath));
//		tabs.add(logoutTab);
        return tabs.toArray(new Tab[tabs.size()]);
    }

    private static Tab createTab(VaadinIcon icon, String title, Class<? extends Component> viewClass) {
        return createTab(populateLink(new RouterLink(null, viewClass), icon, title));
    }

    private static Tab createTab(Component content) {
        final Tab tab = new Tab();
        tab.addThemeVariants(TabVariant.LUMO_ICON_ON_TOP);
        tab.add(content);
        return tab;
    }

    private static Anchor createLogoutLink(String contextPath) {
        final Anchor a = populateLink(new Anchor(), VaadinIcon.ARROW_RIGHT, TITLE_LOGOUT);
        a.setHref(contextPath + "/logout");
        return a;
    }

    private static <T extends HasComponents> T populateLink(T a, VaadinIcon icon, String title) {
        a.add(icon.create());
        a.add(title);
        return a;
    }
}