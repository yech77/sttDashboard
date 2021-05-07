package com.stt.dash.backend.data.entity;

import javax.persistence.*;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

@Entity
public class Client extends AbstractEntity{

    public static enum Cuandrante {
        ALIADO, EMPRESAS, FINANZAS, SEGUROS, UTILITIES
    }

    @OneToMany(mappedBy = "client", fetch = FetchType.EAGER)
    private Set<SystemId> systemids = new HashSet<>();

    @NotBlank
    @Size(min = 3, max = 20)
    @Column(length = 20, unique = true)
    private String clientCod;

    @Size(min = 5, max = 100)
    private String clientName;

    @NotBlank
    @Size(min = 3, max = 75)
    @Column(length = 75, unique = true)
    @Email
    private String email = "";

    @Enumerated(EnumType.STRING)

    @NotNull
    private Cuandrante cuadrante;

    public Client() {
    }

    public Client(String clientName) {
        this.clientName = clientName;
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

    public Collection<SystemId> getSystemids() {
        return systemids;
    }

    public void setSystemids(Set<SystemId> systemids) {
        this.systemids = systemids;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

//    public String getClient_rif() {
//        return client_rif;
//    }
//
//    public void setClient_rif(String client_rif) {
//        this.client_rif = client_rif;
//    }
//
//    public String getClient_addr_fis() {
//        return client_addr_fis;
//    }
//
//    public void setClient_addr_fis(String client_addr_fis) {
//        this.client_addr_fis = client_addr_fis;
//    }
//
//    public String getClient_contact() {
//        return client_contact;
//    }
//
//    public void setClient_contact(String client_contact) {
//        this.client_contact = client_contact;
//    }
//
//    public String getClient_contact_phone() {
//        return client_contact_phone;
//    }
//
//    public void setClient_contact_phone(String client_contact_phone) {
//        this.client_contact_phone = client_contact_phone;
//    }
//
//    public Status getStatus() {
//        return status;
//    }
//
//    public void setStatus(Status status) {
//        this.status = status;
//    }

    public Cuandrante getCuadrante() {
        return cuadrante;
    }

    public void setCuadrante(Cuandrante client_cuadrante) {
        this.cuadrante = client_cuadrante;
    }

//    @Override
//    public String toString() {
//        return "Client{" + "clientCod=" + clientCod + ", clientName=" + clientName + ", email=" + email + '}';
//    }

    @Override
    public String toString() {
        return "Client{clientCod=" + clientCod + ", clientName=" + clientName + ", email=" + email + ", cuadrante=" + cuadrante + '}';
    }
}
