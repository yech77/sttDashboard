package com.stt.dash.backend.data;

import java.util.HashSet;
import java.util.Set;

public class OClientSession {

    private Long id;

    private Set<OSystemIdSession> systemids = new HashSet<>();

    private String clientCod;

    public OClientSession() {
    }

    public String getClientCod() {
        return clientCod;
    }

    public void setClientCod(String clientCod) {
        this.clientCod = clientCod;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Set<OSystemIdSession> getSystemids() {
        return systemids;
    }

    public void setSystemids(Set<OSystemIdSession> systemids) {
        this.systemids = systemids;
    }

    @Override
    public String toString() {
        return "OClientSession{" + "id=" + id + ", systemids=" + systemids + ", clientCod=" + clientCod + '}';
    }

}
