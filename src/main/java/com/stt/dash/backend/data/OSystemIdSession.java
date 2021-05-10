package com.stt.dash.backend.data;

public class OSystemIdSession {

    private Long id;

    private String systemId = "";

    public String getSystemId() {
        return systemId;
    }

    public void setSystemId(String systemId) {
        this.systemId = systemId;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return "OSystemIdSession{" + "id=" + id + ", systemId=" + systemId + '}';
    }

}
