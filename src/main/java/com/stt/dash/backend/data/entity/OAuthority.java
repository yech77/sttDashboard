package com.stt.dash.backend.data.entity;

import javax.persistence.Entity;
import javax.persistence.ManyToMany;
import java.util.Set;

@Entity
public class OAuthority extends AbstractEntitySequence {

    @ManyToMany(mappedBy = "Authorities")
    private Set<ORole> roles;

    private String authName;
    private String authDesc;

    public String getAuthName() {
        return authName;
    }

    public void setAuthName(String authName) {
        this.authName = authName;
    }

    public String getAuthDesc() {
        return authDesc;
    }

    public void setAuthDesc(String authDesc) {
        this.authDesc = authDesc;
    }

    public Set<ORole> getRoles() {
        return roles;
    }

    public void setRoles(Set<ORole> roles) {
        this.roles = roles;
    }

}
