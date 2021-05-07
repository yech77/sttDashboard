package com.stt.dash.backend.data.entity;

import javax.persistence.*;
import java.util.Set;

@Entity
public class ORole extends AbstractEntitySequence {

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "role_has_authority",
            joinColumns = @JoinColumn(name = "orole_id"),
            inverseJoinColumns = @JoinColumn(name = "oauthority_id"))
    private Set<OAuthority> Authorities;

//    @ManyToMany(mappedBy = "roles")
//    private Set<OUser> users;

    private String rolName;

    public Set<OAuthority> getAuthorities() {
        return Authorities;
    }

    public void setAuthorities(Set<OAuthority> Authorities) {
        this.Authorities = Authorities;
    }

    public String getRolName() {
        return rolName;
    }

    public void setRolName(String rolName) {
        this.rolName = rolName;
    }
}
