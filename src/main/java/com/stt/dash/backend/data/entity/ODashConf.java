package com.stt.dash.backend.data.entity;

import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.Table;
import java.time.LocalDateTime;

@Entity
@Table(name = "odash_conf")
@EntityListeners(AuditingEntityListener.class)
public class ODashConf extends AbstractEntitySequence {

    @Column(name = "sync_id")
    private Integer syncId;

    @Column(name = "sync_data")
    private String syncData;

    @LastModifiedDate
    @Column(name = "last_modified_date")
    private LocalDateTime lastModifiedDate;

    public LocalDateTime getLastModifiedDate() {
        return lastModifiedDate;
    }

    public void setLastModifiedDate(LocalDateTime lastModifiedDate) {
        this.lastModifiedDate = lastModifiedDate;
    }

    public String getSyncData() {
        return syncData;
    }

    public void setSyncData(String syncData) {
        this.syncData = syncData;
    }

    public Integer getSyncId() {
        return syncId;
    }

    public void setSyncId(Integer syncId) {
        this.syncId = syncId;
    }
}