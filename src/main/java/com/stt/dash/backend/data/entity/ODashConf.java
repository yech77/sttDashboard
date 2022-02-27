package com.stt.dash.backend.data.entity;

import org.atmosphere.config.service.Get;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "odash_conf")
public class ODashConf extends AbstractEntity {
    @Column(name = "sync_id")
    private Integer syncId;

    public Integer getSyncId() {
        return syncId;
    }

    public void setSyncId(Integer syncId) {
        this.syncId = syncId;
    }

    @Column(name = "sync_data")
    private String syncData;

    public String getSyncData() {
        return syncData;
    }

    public void setSyncData(String syncData) {
        this.syncData = syncData;
    }
}