package com.stt.dash.backend.data;

public class ClientCopycatDTO {
    private Long id;

    private Long clientId;

    private String clientCod;

    private String clientName;

    private String cuadrante;

    private String email;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getClientId() {
        return clientId;
    }

    public void setClientId(Long clientId) {
        this.clientId = clientId;
    }

    public String getClientCod() {
        return clientCod;
    }

    public void setClientCod(String clientCod) {
        this.clientCod = clientCod;
    }

    public String getClientName() {
        return clientName;
    }

    public void setClientName(String clientName) {
        this.clientName = clientName;
    }

    public String getCuadrante() {
        return cuadrante;
    }

    public void setCuadrante(String cuadrante) {
        this.cuadrante = cuadrante;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    @Override
    public String toString() {
        return "ClientCopycatDTO{" +
                "id=" + id +
                ", clientId=" + clientId +
                ", clientCod='" + clientCod + '\'' +
                ", clientName='" + clientName + '\'' +
                ", cuadrante='" + cuadrante + '\'' +
                ", email='" + email + '\'' +
                '}';
    }
}