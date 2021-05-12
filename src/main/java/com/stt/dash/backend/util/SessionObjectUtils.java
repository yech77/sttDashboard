package com.stt.dash.backend.util;

import com.stt.dash.app.security.CurrentUser;
import com.stt.dash.backend.data.OClientSession;
import com.stt.dash.backend.data.OSystemIdSession;
import com.stt.dash.backend.data.OUserSession;
import com.stt.dash.backend.data.entity.User;
import com.stt.dash.backend.repositories.OUserRepository;
import com.vaadin.flow.server.VaadinSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class SessionObjectUtils {

    /**/
    private static final Logger log = LoggerFactory.getLogger(SessionObjectUtils.class);
    /**/
    private static final String UI_CODE = "SOU";

    private OUserSession ouser_session;
    private CurrentUser currentUser;

    //private OUserRepository ouser_repo;
    public SessionObjectUtils(OUserSession ouser_session) {
        this.ouser_session = ouser_session;
    }

    public SessionObjectUtils(CurrentUser currentUser) {
        this.currentUser=currentUser;
    }

    public List<User> getUserFamilyExceptSelf(OUserRepository ouser_repo) {
//        List<User> family = getUserFamily(ouser_repo);
//        if (family.size() > 0) {
//            family.remove(getThisUserEntity(ouser_repo));
//        }
//        return family;
        return null;
    }

//    public void updateDataUserSession(UserService user_serv) {
//        ouser_session.setUser(user_serv.findByUserEmail(ouser_session.getUserEmail()));
//    }

    public List<User> getUserFamily(CurrentUser currentUser) {
        List<User> allUsers = new ArrayList<>();
        List<User> currentFam = new ArrayList<>();
        List<User> addingChildren = new ArrayList<>();

        currentFam.add(currentUser.getUser());
        addingChildren.addAll(currentUser.getUser().getUserChildren());
        while (addingChildren.size() > 0) {
            allUsers.addAll(currentFam);
            currentFam.clear();
            currentFam.addAll(addingChildren);
            addingChildren.clear();
            for (User user : currentFam) {
                addingChildren.addAll(user.getUserChildren());
            }
        }
        allUsers.addAll(currentFam);
        System.out.println("Usuarios en la familia de " + currentUser.getUser().getEmail() + ": " + allUsers);
        return allUsers;
    }

//    public List<User> getUserFamily(OUserRepository ouser_repo) {
//        User thisUser = getThisUserEntity(ouser_repo);
//        List<User> allUsers = new ArrayList<>();
//        List<User> currentFam = new ArrayList<>();
//        List<User> addingChildren = new ArrayList<>();
//
//        currentFam.add(thisUser);
//        addingChildren.addAll(thisUser.getUserChildren());
//        while (addingChildren.size() > 0) {
//            allUsers.addAll(currentFam);
//            currentFam.clear();
//            currentFam.addAll(addingChildren);
//            addingChildren.clear();
//            for (User user : currentFam) {
//                addingChildren.addAll(user.getUserChildren());
//            }
//        }
//        allUsers.addAll(currentFam);
//
//        System.out.println("Usuarios en la familia de " + thisUser.getUserEmail() + ": " + allUsers);
//        return allUsers;
//    }

    /**
     * Genera una lista de usuarios sobre los cuales el usuario actual tiene
     * aceso.
     *
     * @param ouser_repo
     * @return
     */
    /*TODO: Cambiar. */
//    public List<User> getSelfAndChildren(OUserRepository ouser_repo) {
//        List<User> allUsers = new ArrayList<>();
//        List<User> currentLayer = new ArrayList<>();
//        List<User> newChildren = new ArrayList<>();
//        currentLayer.add(getThisUserEntity(ouser_repo));
//        int recursionShield = 0;
//
//        // Busca usuarios de manera seudo-recursiva; se limita a 10,000 para
//        // evitar ciclos infinitos
//        while (recursionShield < 1000) {
//            System.out.println(recursionShield);
//            for (User user : currentLayer) {
//                newChildren.addAll(ouser_repo.findByUserParent(user));
//            }
//            if (newChildren.isEmpty()) {
//                allUsers.addAll(currentLayer);
//                break;
//            }
//            System.out.println(newChildren);
//            allUsers.addAll(currentLayer);
//            currentLayer.clear();
//            currentLayer.addAll(newChildren);
//            newChildren.clear();
//            recursionShield++;
//        }
//        System.out.println("Usuarios bajo el actual:" +allUsers);
//        return allUsers;
//    }
    /**
     * Devuelve el objeto User que corresponde a la sesión actual
     *
     * @param ouser_repo
     * @return
     * @deprecated
     * @see
     */
//    public User getThisUserEntity(OUserRepository ouser_repo) {
//        log.info("{} looking user[{}]", getStringLog(), ouser_session.getUserEmail());
//        User user = ouser_session.getUser();
//        if (user == null) {
//            return null;
//        }
//        log.info("{} found user[{}]", getStringLog(), ouser_session.getUserEmail());
//        return user;
//    }

    /**
     * Devuelve el objeto User que corresponde a la sesión actual
     *
     * @param ouser_serv
     * @return
     */
//    public User getThisUserEntity(UserService ouser_serv) {
//        log.info("{} looking user[{}]", getStringLog(), ouser_session.getUserEmail());
//        User list = ouser_serv.findByUserEmail(ouser_session.getUserEmail());
//        if (list == null) {
//            return null;
//        }
//        log.info("{} found user[{}]", getStringLog(), ouser_session.getUserEmail());
//        return list;
//    }

    /**
     * Devuelve un List de los systemidsde los clientes..
     *
     * @param cli
     * @return
     */
    public final List<String> getClientSids(String cli) {
        List<String> client_sids = null;

//        Obtener los systemIds del CLiente.
        for (OClientSession next : ouser_session.getClients()) {
            if (next.getClientCod().equalsIgnoreCase(cli)) {
                client_sids = new ArrayList<>(next.getSystemids().size());
                for (OSystemIdSession next1 : next.getSystemids()) {
                    client_sids.add(next1.getSystemId());
                }
                break;
            }
        }
        return client_sids;
    }

    /**
     * Devuelve un List de los clientes del usuario.
     *
     * @return
     */
    public final List<OClientSession> getClients() {
        List<OClientSession> client_sids = new ArrayList<>(ouser_session.getClients().size());
        ouser_session.getClients().forEach(client -> {
            client_sids.add(client);
        });
        return client_sids;
    }

    /**
     * @param client_cod
     * @return OClientSession
     */
    public final OClientSession getClient(String client_cod) {
        OClientSession c = null;
        for (OClientSession client : ouser_session.getClients()) {
            if (client.getClientCod().equalsIgnoreCase(client_cod)) {
                c = client;
            }
        }
        return c;
    }

    /**
     * Devuelve un SET de systemids del cliente
     *
     * @param client_cod
     * @return
     */
    public final Set<OSystemIdSession> getSystemId(String client_cod) {
        OClientSession c = getClient(client_cod);
        return c == null ? null : c.getSystemids();
    }

    /**
     * Devuelve un LIST de todos los systemIds del usuario
     *
     * @return
     */
    public final List<OSystemIdSession> getAllSystemIds() {
        List<OSystemIdSession> sys_ids = new ArrayList<>();
        if (ouser_session.getUserTypeOrd().ordinal() > 1) {
            return ouser_session.getSessionSystemId();
        }
        for (OClientSession client : getClients()) {
            sys_ids.addAll(client.getSystemids());
        }
        return sys_ids;
    }

    public final List<String> getStringSystemIds(List<OSystemIdSession> sys_ids) {
        List<String> strings = new ArrayList<>();
        for (OSystemIdSession sid : sys_ids) {
            strings.add(sid.getSystemId());
        }
        return strings;
    }

    private String getStringLog() {
        String id = VaadinSession.getCurrent().getSession().getId();
        StringBuilder sb = new StringBuilder();
        sb.append('[').append(id).append("] [").append(UI_CODE).append("]");
        return sb.toString();
    }
}
