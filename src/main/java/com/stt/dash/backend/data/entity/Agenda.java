package com.stt.dash.backend.data.entity;

import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.util.Calendar;
import java.util.Date;
import java.util.Objects;

@Entity
@EntityListeners(AuditingEntityListener.class)
public class Agenda extends AbstractEntitySequence {

    public enum Status {
        BLANK,
        VALIDATING,
        READY_TO_USE,
        HAS_WARNINGS,
        CORRUPT_OR_LOST
    }

    @Column(unique = true)
    private String name;
    private String description;
    private Date dateCreated;
    private int itemCount;

    @ManyToOne(fetch = FetchType.LAZY)
    private User creator;
    @CreatedBy
    private String creatorEmail;
    private Status status;
    private String fileName;

    private String firstLine;
    private int invalidItemCount;

    public Agenda() {
        name = "";
        description = "";
        dateCreated = Calendar.getInstance().getTime();
        itemCount = 0;
        status = Status.BLANK;
        fileName = "";
        firstLine = "";
        invalidItemCount = 0;
    }

    public Agenda(User creator, String name, String description, String fileName) {
        this.creator = creator;
        this.creatorEmail = creator.getEmail();
        this.name = name;
        this.description = description;
        dateCreated = Calendar.getInstance().getTime();
        itemCount = 0;
        status = Status.BLANK;
        firstLine = "";
        invalidItemCount = 0;
        this.fileName = fileName;
    }

    public int getInvalidItemCount() {
        return invalidItemCount;
    }

    public void setInvalidItemCount(int invalidItemCount) {
        this.invalidItemCount = invalidItemCount;
    }

    public int getVarCount() {
        return firstLine.split(",").length - 1;
    }

    public String getFirstLine() {
        return firstLine;
    }

    public void setFirstLine(String firstLine) {
        this.firstLine = firstLine;
    }

    public String getCreatorEmail() {
        if(creator !=null){
            return creatorEmail;
        }
        return "-";
    }

    public void setCreatorEmail(String creatorEmail){
        this.creatorEmail = creatorEmail;
    }

    public String getCreatorName() {
        if(creator != null){
            return creator.getLastName() + ", " + creator.getFirstName();
        }
        return "-";
    }

    public String getName() {
        if(name == null || name.length() <1){
            return "-";
        }
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        if(description == null || description.length() <1){
            return "-";
        }
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Date getDateCreated() {
        if(dateCreated == null){
            dateCreated = Calendar.getInstance().getTime();
        }
        return dateCreated;
    }

    public void setDateCreated(Date dateCreated) {
        this.dateCreated = dateCreated;
    }

    public int getItemCount() {
        return itemCount;
    }

    public void setItemCount(int itemCount) {
        this.itemCount = itemCount;
    }

    public User getCreator() {
        return creator;
    }

    public void setCreator(User creator) {
        if(creator != null){
            this.creator = creator;
            this.creatorEmail = creator.getEmail();
        }
    }

    public String getStringStatus() {
        switch(status){
            case BLANK:
                return "Sin Crear";
            case VALIDATING:
                return "Validando";
            case HAS_WARNINGS:
                return "Tiene Discrepancias";
            case READY_TO_USE:
                return "Válido";
            case CORRUPT_OR_LOST:
                return "No Existe";
        }

        System.out.println("Status de Agenda no encontrado!!!");
        return "-";
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Agenda)) return false;
        if (!super.equals(o)) return false;
        Agenda agenda = (Agenda) o;
        return itemCount == agenda.itemCount && Objects.equals(name, agenda.name) && Objects.equals(description, agenda.description) && Objects.equals(dateCreated, agenda.dateCreated) && Objects.equals(creatorEmail, agenda.creatorEmail) && status == agenda.status && Objects.equals(fileName, agenda.fileName) && Objects.equals(firstLine, agenda.firstLine);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), name, description, dateCreated, itemCount, creatorEmail, status, fileName, firstLine);
    }
}
