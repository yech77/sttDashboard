package com.stt.dash.ui.views.admin.users;

import com.stt.dash.app.HasLogger;
import com.stt.dash.app.security.CurrentUser;
import com.stt.dash.app.session.SetGenericBean;
import com.stt.dash.backend.data.Role;
import com.stt.dash.backend.data.entity.ORole;
import com.stt.dash.backend.data.entity.SystemId;
import com.stt.dash.backend.data.entity.User;
import com.stt.dash.backend.repositories.OUserRepository;
import com.stt.dash.backend.service.ClientService;
import com.stt.dash.backend.service.ORoleService;
import com.stt.dash.backend.service.UserService;
import com.stt.dash.backend.util.SessionObjectUtils;
import com.stt.dash.ui.MainView;
import com.stt.dash.ui.crud.AbstractBakeryCrudView;
import com.stt.dash.ui.utils.BakeryConst;
import com.vaadin.flow.component.crud.BinderCrudEditor;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.data.renderer.TemplateRenderer;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.StringJoiner;

import static com.stt.dash.ui.utils.BakeryConst.PAGE_USERS;

@Route(value = PAGE_USERS, layout = MainView.class)
@PageTitle(BakeryConst.TITLE_USERS)
@Secured({Role.ADMIN, "UI_USER"})
public class UsersView extends AbstractBakeryCrudView<User> implements HasLogger {
    private static Logger log = LoggerFactory.getLogger(UsersView.class);

    @Autowired
    public UsersView(UserService service, CurrentUser currentUser, ORoleService roleService, OUserRepository ouser_repo, PasswordEncoder passwordEncoder, SetGenericBean<SystemId> comercial, ClientService clientService) {
        super(User.class, service, new Grid<>(), createForm(roleService.findAll(""), service, currentUser, comercial, passwordEncoder, clientService), currentUser);
        log.info(comercial.getSet().size() + "*************");
    }

    @Override
    public void setupGrid(Grid<User> grid) {
        grid.addColumn(createNameRenderer()).setHeader("Nombre / Tipo").setAutoWidth(true);
        grid.addColumn(User::getEmail).setHeader("Correo").setAutoWidth(true);
        grid.addColumn(createRolRenderer()).setHeader("Roles").setWidth("350px");
//        grid.addThemeVariants(GridVariant.LUMO_WRAP_CELL_CONTENT);
    }

    private static TemplateRenderer<User> createNameRenderer() {
        return TemplateRenderer.<User>of("<div>[[item.userType]]<br><small><span style=\"font-size: var(--lumo-font-size-xxs); color: var(--lumo-secondary-text-color);\">[[item.userTypeOrd]]</span></small></div>")
                .withProperty("userType", u -> {
                    return u.getFirstName() + " " + u.getLastName();
                })
                .withProperty("userTypeOrd", u -> {
                    return u.getUserTypeOrd().name().toLowerCase();
                });
    }

    private static TemplateRenderer<User> createRolRenderer() {
        return TemplateRenderer.<User>of("<div><small><span style=\"font-size: var(--lumo-font-size-s);\">[[item.rolType]]</span></small></div>")
                .withProperty("rolType", role -> {
                    Set<ORole> authority = role.getRoles();
                    if (authority == null) {
                        return "-";
                    }
                    StringJoiner stringJoiner = new StringJoiner(", ", "", "");
                    for (ORole r : authority) {
                        stringJoiner.add(r.getRolName().toLowerCase());
                    }
                    return stringJoiner.toString();
                });
    }

    @Override
    protected String getBasePage() {
        return PAGE_USERS;
    }

    private static BinderCrudEditor<User> createForm(List<ORole> roleList, UserService userService, CurrentUser currentUser, SetGenericBean comercial, PasswordEncoder passwordEncoder, ClientService clientService) {
        SessionObjectUtils sessionObjectUtils = new SessionObjectUtils(currentUser);
        List<User> allUsers = new ArrayList<>();
        if (currentUser.getUser().getUserTypeOrd() == User.OUSER_TYPE_ORDINAL.COMERCIAL) {
            allUsers.addAll(userService.getRepository().findAll());
        } else {
            allUsers.addAll(sessionObjectUtils.getUserFamily(currentUser));
        }
        UserForm form = new UserForm(roleList, new ArrayList<>(currentUser.getUser().getClients()), clientService.findAll().getContent(), comercial.getSet(), allUsers, currentUser, passwordEncoder);
        return new BinderCrudEditor<User>(form.getBinder(), form);
    }
}
