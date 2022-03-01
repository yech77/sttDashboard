package com.stt.dash.backend.data.entity;

import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

@Entity
@EntityListeners(AuditingEntityListener.class)
public class Client extends AbstractEntity {

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
    @Size(min = 3, max = 100)
    @Column(length = 100, unique = true)
    @Email
    private String email = "";

    @LastModifiedDate
    @Column(name = "last_modified_date")
    private LocalDateTime lastModifiedDate;

    @Enumerated(EnumType.STRING)
    @NotNull
    private Cuandrante cuadrante;

    public LocalDateTime getLastModifiedDate() {
        return lastModifiedDate;
    }

    public void setLastModifiedDate(LocalDateTime lastModifiedDate) {
        this.lastModifiedDate = lastModifiedDate;
    }

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

    public Cuandrante getCuadrante() {
        return cuadrante;
    }

    public void setCuadrante(Cuandrante client_cuadrante) {
        this.cuadrante = client_cuadrante;
    }

    @Override
    public String toString() {
        return "Client{clientCod=" + clientCod + ", clientName=" + clientName + ", email=" + email + ", cuadrante=" + cuadrante + '}';
    }
}
