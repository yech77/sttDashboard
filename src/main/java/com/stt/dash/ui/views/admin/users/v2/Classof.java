package com.stt.dash.ui.views.admin.users.v2;

import com.stt.dash.app.security.CurrentUser;
import com.stt.dash.backend.data.entity.User;
import com.stt.dash.backend.service.UserService;
import com.stt.dash.ui.utils.BeforeSavingResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.ArrayList;
import java.util.List;

import static com.stt.dash.backend.data.entity.User.OUSER_TYPE_ORDINAL.ADMIN_EMPRESAS;
import static com.stt.dash.backend.data.entity.User.OUSER_TYPE_ORDINAL.COMERCIAL;
import static com.stt.dash.backend.data.entity.User.OUSER_TYPE_ORDINAL.EMPRESA;
import static com.stt.dash.backend.data.entity.User.OUSER_TYPE_ORDINAL.USUARIO;

public class Classof {
    CurrentUser currentUser;
    UserService userService;
    long isotherCounter = -1;

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
