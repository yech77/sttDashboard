package com.stt.dash.backend.data.bean;

import com.stt.dash.backend.data.ClientCopycatDTO;
import com.stt.dash.backend.data.SystemIdCopycatDTO;

import java.util.ArrayList;
import java.util.List;

public class SyncDTO {
    private List<ClientCopycatDTO> clients = new ArrayList<>();
    private List<SystemIdCopycatDTO> systemIdCopycat = new ArrayList<>();

    public SyncDTO(List<ClientCopycatDTO> clients, List<SystemIdCopycatDTO> systemIdCopycat) {
        this.clients = clients;
        this.systemIdCopycat = systemIdCopycat;
    }

    public List<ClientCopycatDTO> getClients() {
        return clients;
    }

    public void setClients(List<ClientCopycatDTO> clients) {
        this.clients = clients;
    }

    public List<SystemIdCopycatDTO> getSystemIdCopycatDTO() {
        return systemIdCopycat;
    }

    public void setSystemIdCopycatDTO(List<SystemIdCopycatDTO> systemIdCopycat) {
        this.systemIdCopycat = systemIdCopycat;
    }

    @Override
    public String toString() {
        return "SyncDTO{" +
                "clients=" + clients +
                ", systemIdCopycat=" + systemIdCopycat +
                '}';
    }
}
