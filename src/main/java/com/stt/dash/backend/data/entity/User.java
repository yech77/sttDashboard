package com.stt.dash.backend.data.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;

import javax.persistence.*;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Entity(name = "UserInfo")
public class User extends AbstractEntitySequence {
    public static enum OUSER_TYPE {
        IS, HAS, BY
    }

    public static enum OUSER_TYPE_ORDINAL {
        COMERCIAL, ADMIN_EMPRESAS, EMPRESA, USUARIO
    }

    @NotEmpty
    @Email
    @Size(max = 100)
    @Column(length = 100, unique = true)
    private String email;

    @NotNull
    @Size(min = 4, max = 255)
    private String passwordHash;

    @NotNull
    @NotEmpty
    @Size(max = 100)
    @Column(length = 100)
    private String firstName;

    //    @NotBlank
    @Size(max = 100)
    @Column(length = 100)
    private String lastName;

    //    @NotBlank
    @Size(max = 255)
    private String role;

    private boolean locked = false;

    private boolean active = true;

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


    @Enumerated(EnumType.ORDINAL)
    private User.OUSER_TYPE userType;

    @Enumerated(EnumType.ORDINAL)
    private User.OUSER_TYPE_ORDINAL userTypeOrd;

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
    private User userParent;

    @OneToMany(fetch = FetchType.EAGER, mappedBy = "userParent")
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private Set<User> userChildren;

    @PrePersist
    @PreUpdate
    private void prepareData() {
        this.email = email == null ? null : email.toLowerCase();
    }

    public User() {
        // An empty constructor is needed for all beans
    }

    public LocalDateTime getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(LocalDateTime createdDate) {
        this.createdDate = createdDate;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public Set<ORole> getRoles() {
        return roles;
    }

    public void setRoles(Set<ORole> roles) {
        this.roles = roles;
    }

    public User.OUSER_TYPE getUserType() {
        return userType;
    }

    public void setUserType(User.OUSER_TYPE userType) {
        this.userType = userType;
    }

    public User.OUSER_TYPE_ORDINAL getUserTypeOrd() {
        return userTypeOrd;
    }

    public void setUserTypeOrd(User.OUSER_TYPE_ORDINAL userTypeOrd) {
        this.userTypeOrd = userTypeOrd;
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
        if (client == null) return;
        clients.add(client);
    }

    public Set<Client> getClients() {
        return clients;
    }

    public void setClients(Set<Client> clients) {
        this.clients = clients;
    }

    public Set<SystemId> getSystemids() {
        return systemids;
    }

    public void setSystemids(Set<SystemId> systemids) {
        this.systemids = systemids;
    }

    public User getUserParent() {
        return userParent;
    }

    public void setUserParent(User userParent) {
        this.userParent = userParent;
    }

    public Set<User> getUserChildren() {
        return userChildren;
    }

    public void setUserChildren(Set<User> userChildren) {
        this.userChildren = userChildren;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public boolean isLocked() {
        return locked;
    }

    public void setLocked(boolean locked) {
        this.locked = locked;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        if (!super.equals(o)) {
            return false;
        }
        User that = (User) o;
        return locked == that.locked &&
                Objects.equals(email, that.email) &&
                Objects.equals(firstName, that.firstName) &&
                Objects.equals(lastName, that.lastName) &&
                Objects.equals(role, that.role);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), email, firstName, lastName, role, locked);
    }
}
