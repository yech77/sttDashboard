package com.stt.dash.ui.views.admin.users.v2;

import com.stt.dash.app.HasLogger;
import com.stt.dash.app.security.CurrentUser;
import com.stt.dash.app.session.SetGenericBean;
import com.stt.dash.backend.data.Role;
import com.stt.dash.backend.data.entity.Client;
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
import com.stt.dash.ui.utils.BeforeSavingResponse;
import com.stt.dash.ui.views.admin.users.UserForm;
import com.vaadin.flow.component.crud.BinderCrudEditor;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.data.renderer.TemplateRenderer;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import org.apache.commons.lang3.ObjectUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.StringJoiner;

import static com.stt.dash.backend.data.entity.User.OUSER_TYPE_ORDINAL.*;
import static com.stt.dash.ui.utils.BakeryConst.PAGE_USERS;

@Route(value = PAGE_USERS + "v2", layout = MainView.class)
@PageTitle(BakeryConst.TITLE_USERS)
@Secured({Role.ADMIN, "UI_USER"})
public class UsersViewv2 extends AbstractBakeryCrudView<User> implements HasLogger {
    private static Logger log = LoggerFactory.getLogger(UsersViewv2.class);
    private final CurrentUser currentUser;
    private final UserService userService;

    @Autowired
    public UsersViewv2(UserService service,
                       CurrentUser currentUser,
                       ORoleService roleService,
                       OUserRepository ouser_repo,
                       PasswordEncoder passwordEncoder,
                       SetGenericBean<SystemId> comercial,
                       ClientService clientService) {
        super(User.class, service, new Grid<>(), createForm(roleService.findAll(""), service, currentUser, currentUser.getUser().getSystemids(), passwordEncoder, clientService), currentUser);
        this.currentUser = currentUser;
        this.userService = service;
        log.info(comercial.getSet().size() + "*************");
    }

    @Override
    public void setupGrid(Grid<User> grid) {
        grid.addColumn(createNameRenderer()).setHeader("Nombre / Tipo").setAutoWidth(true);
        grid.addColumn(User::getEmail).setHeader("Correo").setAutoWidth(true);
//        grid.addColumn(createRolRenderer()).setHeader("Roles").setWidth("350px");
//        grid.addThemeVariants(GridVariant.LUMO_WRAP_CELL_CONTENT);
    }

    private static TemplateRenderer<User> createNameRenderer() {
        return TemplateRenderer.<User>of("<div>[[item.userType]]<br><small><span style=\"font-size: var(--lumo-font-size-xxs); color: var(--lumo-secondary-text-color);\">[[item.userTypeOrd]]</span></small></div>")
                .withProperty("userType", u -> {
                    return u.getFirstName() + " " + u.getLastName();
                })
                .withProperty("userTypeOrd", u -> {
                    return u.getUserParent().getFirstName().toLowerCase() + " " + u.getUserParent().getLastName().toLowerCase();
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
        return PAGE_USERS + "v2";
    }

    private static BinderCrudEditor<User> createForm(List<ORole> roleList, UserService userService, CurrentUser currentUser, Set<SystemId> systemIdSet, PasswordEncoder passwordEncoder, ClientService clientService) {
        List<User> userList = getUserList(userService, currentUser);
        List<User> userBelongList = getUserBelongList(userService, currentUser);
        List<SystemId> parSystemids = getParSystemids(systemIdSet);
        UserFormDialogV2 form = new UserFormDialogV2(roleList, parSystemids, userList, userBelongList, currentUser, passwordEncoder);
        return new BinderCrudEditor<User>(form.getBinder(), form);
    }

    private static List<SystemId> getParSystemids(Set<SystemId> systemIdSet) {
        return ObjectUtils.isNotEmpty(systemIdSet) ? new ArrayList<>(systemIdSet) : Collections.emptyList();
    }

    private static List<User> getUserBelongList(UserService userService, CurrentUser currentUser) {
        List<User> userBelongList = new ArrayList<>();
        Page<User> userBelongsPage = null;
        User.OUSER_TYPE_ORDINAL userTypeOrd = currentUser.getUser().getUserTypeOrd();
        if (userTypeOrd == ADMIN_EMPRESAS) {
            userBelongList = getUserBelongList(userService, currentUser, userBelongList);
        } else if (userTypeOrd == COMERCIAL) {
            userBelongList.add(currentUser.getUser());
        }
        return userBelongList;
    }

    private static List<User> getUserBelongList(UserService userService, CurrentUser currentUser, List<User> userBelongList) {
        Page<User> userBelongsPage = userService.findByUserTypeOrdAndClients(ADMIN_EMPRESAS, currentUser.getUser().getClient(), Pageable.unpaged());
        return userBelongsPage.hasContent() ? userBelongsPage.getContent() : Collections.emptyList();
    }

    private static List<User> getUserList(UserService userService, CurrentUser currentUser) {
        SessionObjectUtils sessionObjectUtils = new SessionObjectUtils(currentUser);
        return currentUser.getUser().getUserTypeOrd() == COMERCIAL ? userService.getRepository().findAll() : sessionObjectUtils.getUserFamily(currentUser);
    }


    @Override
    protected BeforeSavingResponse beforeSaving(long idBeforeSave, User userToCreate) {
        BeforeSavingResponse bsr = new BeforeSavingResponse();
        /* No se puede crear un usuario sin que exista su administrador*/
        if (userToCreate.getUserTypeOrd() == COMERCIAL) {
            /*TODO: Falta colocarle el papa a lops comerciales, que seria superadmin. */
            userToCreate.setUserType(User.OUSER_TYPE.HAS);
        } else if (userToCreate.getUserTypeOrd() == ADMIN_EMPRESAS) {
            /* Un ADMIN_EMPRESAS siempre va a pertenecer a un COMERCIAL*/
            userToCreate.setUserParent(currentUser.getUser());
            userToCreate.setUserType(User.OUSER_TYPE.IS);
        } else {
            /* Un Usuario debe, en primera opcion, pertenecer a un ADMIN_EMPRESAS */
            if (currentUser.getUser().getUserTypeOrd() == ADMIN_EMPRESAS) {
                userToCreate.setUserParent(currentUser.getUser());
                userToCreate.setUserTypeOrd(EMPRESA);
                userToCreate.setUserType(User.OUSER_TYPE.IS);
            } else if (currentUser.getUser().getUserTypeOrd() == EMPRESA) {
                userToCreate.setUserParent(currentUser.getUser());
                userToCreate.setUserTypeOrd(USUARIO);
                userToCreate.setUserType(User.OUSER_TYPE.BY);
            } else if (currentUser.getUser().getUserTypeOrd() == COMERCIAL) {
                /* Se busca usuario administrador para asignarlo automaticamente */
                Page<User> userBelongsPage = null;
                List<User> userBelongList = new ArrayList<>();
                userToCreate.setUserTypeOrd(EMPRESA);
                userToCreate.setUserType(User.OUSER_TYPE.BY);
                userBelongsPage = userService.findByUserTypeOrdAndClients(ADMIN_EMPRESAS, userToCreate.getClient(), Pageable.unpaged());
                if (userBelongsPage.hasContent()) {
                    userBelongList = userBelongsPage.getContent();
                    userToCreate.setUserParent(userBelongList.get(0));
                } else {
                    bsr.setSuccess(false);
                    bsr.setMessage("Debe crear primero un Usuario Administrador de: " + userToCreate.getClient().getClientCod() + " " + userToCreate.getClient().getClientName());
                    return bsr;
                }
            }
        }
        bsr.setSuccess(true);
        return bsr;
    }
}
