package com.stt.dash.backend.data.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;

import javax.persistence.*;
import javax.validation.constraints.*;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

/**
 * Usuario
 * IS- Tiene un cliente asignado.
 * HAS- Tiene uno o mas Client asignados.
 * BY- Tiene asignado uno o mas SystemID. No tiene asignado Client.
 * @since v1.00
 */
@Entity
@Table(indexes = @Index(columnList = "userEmail"))
public class OUser extends AbstractEntitySequence {
    public static enum OUSER_TYPE {
        IS, HAS, BY
    }

    public static enum OUSER_TYPE_ORDINAL {
        COMERCIAL, ADMIN_EMPRESAS, EMPRESA, USUARIO
    }
    @NotNull
    @Size(min = 4, max = 255)
    private String passwordHash;

    private boolean locked = false;

    @CreatedDate
    private LocalDateTime createdDate;

    @CreatedBy
    private String createdBy;

    /* Usuario tiene roles*/
    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "user_has_roles",
            joinColumns = @JoinColumn(name = "ouser_id"),
            inverseJoinColumns = @JoinColumn(name = "orole_id"))
    private Set<ORole> roles = new HashSet<>();

    @NotBlank
    @Enumerated(EnumType.ORDINAL)
    private OUSER_TYPE userType;

    @NotBlank
    @Enumerated(EnumType.ORDINAL)
    @NotNull
    private OUSER_TYPE_ORDINAL userTypeOrd;

    /*Usuario tiene cliente(s) si es de tipo HAS o IS*/
    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "user_has_clients",
            joinColumns = @JoinColumn(name = "ouser_id"),
            inverseJoinColumns = @JoinColumn(name = "client_id"))
    private Set<Client> clients = new HashSet<>();

    /*Usuario tiene SiD(s) si es de tipo BY*/
    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "user_has_sids",
            joinColumns = @JoinColumn(name = "ouser_id"),
            inverseJoinColumns = @JoinColumn(name = "systemid_id"))
    private Set<SystemId> systemids;

    /*Usuario puede tener un padre*/
    @ManyToOne(fetch = FetchType.EAGER)
    private OUser userParent;

    @OneToMany(fetch = FetchType.EAGER, mappedBy = "userParent")
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private Set<OUser> userChildren;


    @NotBlank
    @Size(min = 3, max = 20)
    @Column(length = 20)
//    @GridColum(order = 0, columnName = "NOMBRE")
    private String userName;

    @Size(max = 20)
    @Column(length = 20)
    @NotBlank
//    @GridColum(order = 1, columnName = "APELLIDO")
    private String userLastname;

    @NotBlank
    @Size(min = 3, max = 75)
    @Column(length = 75, unique = true)
    @Email
//    @GridColum(order = 3, columnName = "CORREO")
    private String userEmail;

    @Size(min = 3, max = 15)
    @Column(length = 15)
//    @GridColum(order = 4, columnName = "PSW")
    private String userPassword;

    @PrePersist
    @PreUpdate
    private void prepareData(){
        this.userEmail = userEmail == null ? null : userEmail.toLowerCase();
    }

    public Set<Client> getClients() {
        return clients;
    }

    public void setClients(Set<Client> clients) {
        this.clients = clients;
    }

    public Client getClient() {
        Client cl = null;
        for (Client c : clients) {
            cl = c;
        }
        return cl;
    }

    public void setClient(Client client) {
        this.clients.clear();
        clients.add(client);
    }

    public Set<SystemId> getSystemids() {
        return systemids;
    }

    public void setSystemids(Set<SystemId> systemids) {
        this.systemids = systemids;
    }

    public OUser getUser_parent() {
        return userParent;
    }

    public void setUser_parent(OUser user_parent) {
        this.userParent = user_parent;
    }

    @JsonIgnore
    public Collection<OUser> getUserChildren() {
        return userChildren;
    }

    public void setUserChildren(Set<OUser> userChildren) {
        this.userChildren = userChildren;
    }

    public OUSER_TYPE getUserType() {
        return userType;
    }

    public void setUserType(OUSER_TYPE userType) {
        this.userType = userType;
    }

    public OUSER_TYPE_ORDINAL getUserTypeOrd() {
        return userTypeOrd;
    }

    public void setUserTypeOrd(OUSER_TYPE_ORDINAL userTypeOrd) {
        this.userTypeOrd = userTypeOrd;
    }

    public String getUserPassword() {
        return userPassword;
    }

    public void setUserPassword(String userPassword) {
        this.userPassword = userPassword;
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

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    public Set<ORole> getRoles() {
        return roles;
    }

    public void setRoles(Set<ORole> roles) {
        this.roles = roles;
    }

    public OUser getUserParent() {
        return userParent;
    }

    public void setUserParent(OUser userParent) {
        this.userParent = userParent;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public LocalDateTime getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(LocalDateTime createdDate) {
        this.createdDate = createdDate;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }

    public boolean isLocked() {
        return locked;
    }

    public void setLocked(boolean locked) {
        this.locked = locked;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof OUser)) return false;
        if (!super.equals(o)) return false;
        OUser oUser = (OUser) o;
        return locked == oUser.locked && passwordHash.equals(oUser.passwordHash) && createdDate.equals(oUser.createdDate) && createdBy.equals(oUser.createdBy) && roles.equals(oUser.roles) && userType == oUser.userType && userTypeOrd == oUser.userTypeOrd && Objects.equals(clients, oUser.clients) && Objects.equals(systemids, oUser.systemids) && Objects.equals(userParent, oUser.userParent) && Objects.equals(userChildren, oUser.userChildren) && userName.equals(oUser.userName) && Objects.equals(userLastname, oUser.userLastname) && userEmail.equals(oUser.userEmail) && userPassword.equals(oUser.userPassword);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), passwordHash, locked, createdDate, createdBy, roles, userType, userTypeOrd, clients, systemids, userParent, userChildren, userName, userLastname, userEmail, userPassword);
    }
}
