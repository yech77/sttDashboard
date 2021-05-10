package com.stt.dash.backend.data;

import com.stt.dash.backend.data.entity.OUser;
import com.stt.dash.backend.data.entity.SystemId;
import com.stt.dash.backend.data.entity.User;
import com.vaadin.flow.spring.annotation.VaadinSessionScope;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
@VaadinSessionScope
public class OUserSession {

    private Long id;

    private Set<OAuthoritySession> auths = new HashSet<>();

    private User.OUSER_TYPE userType;

    private User.OUSER_TYPE_ORDINAL userTypeOrd;

    /*Usuario tiene cliente(s) si es de tipo HAS o IS*/
    private Set<OClientSession> clients = new HashSet<>();

    /*Usuario puede tener un padre*/
    private Collection<OUserSession> userSessionChildren = new ArrayList<>();
    private Collection<OUser> userChildren = new ArrayList<>();

    /*Data del usuario */
    Map<String, Object> data = new HashMap<>();

    private User user;

    private String userName;
    private String userLastname;
    private String userEmail;

    /* El usuario tiene systemIds si es de tipo By */
    private List<String> stringSystemids = new ArrayList<>();
    private List<SystemId> systemids = new ArrayList<>();
    private List<OSystemIdSession> sessionSystemId = new ArrayList<>();

    /**
     * @deprecated  use getUserChildren instead.
     *
     * @return
     */
    public Collection<OUserSession> getUserSessionChildren() {
        return userSessionChildren;
    }

    /**
     * @deprecated  use setUserChildren instead.
     *
     * @return
     */
    public void setUserSessionChildren(Collection<OUserSession> userSessionChildren) {
        this.userSessionChildren = userSessionChildren;
    }

    public User.OUSER_TYPE getUserType() {
        return userType;
    }

    public void setUserType(User.OUSER_TYPE userType) {
        this.userType = userType;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserLastname() {
        return userLastname;
    }

    public void setUserLastname(String userLastname) {
        this.userLastname = userLastname;
    }

//    public String getUserEmail() {
//        return userEmail;
//    }
//
//    public void setUserEmail(String userEmail) {
//        this.userEmail = userEmail;
//    }

    public Set<OAuthoritySession> getAuths() {
        return auths;
    }

    public void addAllAuth(Set<OAuthoritySession> auths) {
        this.auths.addAll(auths);
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Set<OClientSession> getClients() {
        return clients;
    }

    public void setClients(Set<OClientSession> clients) {
        this.clients = clients;
    }

    /**
     * @deprecated  use getSystemId instead.
     *
     * @return
     */
    public List<String> getStringSystemid() {
        return stringSystemids;
    }

    /**
     * @deprecated  use setSystemId instead.
     *
     * @return
     */
    public void setStringSystemids(List<String> systemids) {
        this.stringSystemids = systemids;
    }

    public List<SystemId> getSystemid() {
        return systemids;
    }

    public void setSystemids(List<SystemId> systemids) {
        this.systemids = systemids;
    }

    public Object putData(String key, Object value) {
        return data.put(key, value);
    }

    public Object getData(String key) {
        return data.get(key);
    }

    public Object removeData(String key) {
        return data.remove(key);
    }

    public Collection<OUser> getUserChildren() {
        return userChildren;
    }

    public void setUserChildren(Collection<OUser> userChildren) {
        this.userChildren = userChildren;
    }

    public List<OSystemIdSession> getSessionSystemId() {
        return sessionSystemId;
    }

    public void setSessionSystemId(List<OSystemIdSession> sessionSystemId) {
        this.sessionSystemId = sessionSystemId;
    }

    @Override
    public String toString() {
        return "OUserSession{" + "id=" + id + ", auths=" + auths + ", userType=" + userType + ", userTypeOrd=" + userTypeOrd + ", clients=" + clients + ", userSessionChildren=" + userSessionChildren + ", userChildren=" + userChildren + ", data=" + data + ", userName=" + userName + ", userLastname=" + userLastname + ", userEmail=" + userEmail + ", stringSystemids=" + stringSystemids + ", systemids=" + systemids + '}';
    }

    public User.OUSER_TYPE_ORDINAL getUserTypeOrd() {
        return userTypeOrd;
    }

    public void setUserTypeOrd(User.OUSER_TYPE_ORDINAL userTypeOrd) {
        this.userTypeOrd = userTypeOrd;
    }

//    public User getUser() {
//        return user;
//    }
//
//    public void setUser(User user) {
//        this.user = user;
//    }
}
