package com.stt.dash.ui.views.admin.users;

import com.stt.dash.app.security.CurrentUser;
import com.stt.dash.backend.data.Role;
import com.stt.dash.backend.data.entity.ORole;
import com.stt.dash.backend.data.entity.OUser;
import com.stt.dash.backend.data.entity.User;
import com.stt.dash.backend.repositories.OUserRepository;
import com.stt.dash.backend.service.ORoleService;
import com.stt.dash.backend.service.UserService;
import com.stt.dash.backend.util.SessionObjectUtils;
import com.stt.dash.ui.MainView;
import com.stt.dash.ui.crud.AbstractBakeryCrudView;
import com.stt.dash.ui.utils.BakeryConst;
import com.stt.dash.ui.views.HasNotifications;
import com.vaadin.flow.component.crud.BinderCrudEditor;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
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
public class UsersView extends AbstractBakeryCrudView<User> {

    @Autowired
    public UsersView(UserService service,
                     CurrentUser currentUser,
                     ORoleService roleService,
                     OUserRepository ouser_repo,
                     PasswordEncoder passwordEncoder) {
        super(User.class, service, new Grid<>(),
                createForm(roleService.findAll(""),
                        service,
                        currentUser,
                        passwordEncoder),
                currentUser);
    }

    @Override
    public void setupGrid(Grid<User> grid) {
        grid.addColumn(User::getEmail).setWidth("250px").setHeader("Correo").setFlexGrow(5);
        grid.addColumn(u -> u.getFirstName() + " " + u.getLastName()).setHeader("Nombre").setWidth("180px").setFlexGrow(5);
        grid.addColumn(role -> {
            Set<ORole> authority = role.getRoles();
            if (authority == null) {
                return "-";
            }
            StringJoiner stringJoiner = new StringJoiner(", ", "[", "]");
            for (ORole r :
                    authority) {
                stringJoiner.add(r.getRolName());
            }
            return stringJoiner.toString();
        }).setHeader("Roles").setWidth("150px");
    }

    @Override
    protected String getBasePage() {
        return PAGE_USERS;
    }

    private static BinderCrudEditor<User> createForm(List<ORole> roleList,
                                                     UserService userService,
                                                     CurrentUser currentUser,
                                                     PasswordEncoder passwordEncoder) {
        SessionObjectUtils sessionObjectUtils = new SessionObjectUtils(currentUser);
        List<User> allUsers=new ArrayList<>();
        if (currentUser.getUser().getUserTypeOrd() == User.OUSER_TYPE_ORDINAL.COMERCIAL) {
            allUsers.addAll(userService.getRepository().findAll());
        } else {
            allUsers.addAll(sessionObjectUtils.getUserFamily(currentUser));
        }
        UserForm form = new UserForm(roleList,
                null,
                null,
                allUsers, currentUser, passwordEncoder);
        return new BinderCrudEditor<User>(form.getBinder(), form);
    }
}
