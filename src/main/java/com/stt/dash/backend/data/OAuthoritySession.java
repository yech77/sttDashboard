package com.stt.dash.backend.data;

public class OAuthoritySession {

    private Long id;
    private String authName;

    public String getAuthName() {
        return authName;
    }

    public void setAuthName(String authName) {
        this.authName = authName;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return "OAuthoritySession{" + "id=" + id + ", authName=" + authName + '}';
    }
}
