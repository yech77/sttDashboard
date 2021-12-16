package com.stt.dash.ui.views.bulksms;

import com.stt.dash.app.OProperties;
import com.stt.dash.app.security.CurrentUser;
import com.stt.dash.backend.data.entity.Agenda;
import com.stt.dash.backend.util.AgendaFileUtils;
import com.stt.dash.ui.crud.OnUI;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.component.upload.Upload;
import com.vaadin.flow.component.upload.receivers.MemoryBuffer;
import com.vaadin.flow.data.binder.BeanValidationBinder;
import com.vaadin.flow.data.binder.Binder;

import java.io.InputStream;

public class BulkSmsForm extends FormLayout implements OnUI {
    Binder<Agenda> binder = new BeanValidationBinder<>(Agenda.class);
    /**/
    TextField nameBox = new TextField("Nombre de la Agenda");
    TextField descriptionBox = new TextField("Descripcion");
    /**/
    private InputStream stream;
    private MemoryBuffer fileUploader = new MemoryBuffer();
    private Upload upload = new Upload(fileUploader);
    /**/
    HorizontalLayout horizontalLayout = new HorizontalLayout();
    private final CurrentUser currentUser;
    private OProperties properties;
    private TextField fileName = new TextField();
    private TextField fileNameOriginal = new TextField();

    public BulkSmsForm(CurrentUser currentUser, OProperties properties) {
        this.currentUser = currentUser;
        this.properties = properties;
        setResponsiveSteps(
                new ResponsiveStep("25em", 1, ResponsiveStep.LabelsPosition.TOP),
                new ResponsiveStep("32em", 2, ResponsiveStep.LabelsPosition.TOP));
        binder.forField(nameBox)
                .asRequired().bind(Agenda::getName, Agenda::setName);
        binder.bind(descriptionBox, "description");
        binder.bind(fileName, "fileName");
        binder.bind(fileNameOriginal, "fileNameOriginal");
        /**/
        upload.setDropLabel(new Span("Añadir archivo aquí"));
        upload.setMaxFileSize(83840000);
        upload.addSucceededListener(event -> {
            stream = fileUploader.getInputStream();
            AgendaFileUtils.setBaseDir(properties.getAgendaFilePathUpload());
            String fileName = AgendaFileUtils.createUniqueFileName(fileUploader.getFileName());
            AgendaFileUtils.createAgendaFile(fileName, stream);
            this.fileName.setValue(fileName);
            this.fileNameOriginal.setValue(fileUploader.getFileName());
        });
        horizontalLayout.add(upload);
        add(nameBox, descriptionBox, horizontalLayout);
    }

    public Binder<Agenda> getBinder() {
        return binder;
    }

    @Override
    public void onUI() {
        MemoryBuffer fileUploader = new MemoryBuffer();
        Upload upload = new Upload(fileUploader);  /**/
        upload.setDropLabel(new Span("Añadir archivo aquí"));
        upload.setMaxFileSize(83840000);
        upload.addSucceededListener(event -> {
            stream = fileUploader.getInputStream();
            AgendaFileUtils.setBaseDir(properties.getAgendaFilePathUpload());
            String fileName = AgendaFileUtils.createUniqueFileName(fileUploader.getFileName());
            AgendaFileUtils.createAgendaFile(fileName, stream);
            this.fileName.setValue(fileName);
            this.fileNameOriginal.setValue(fileUploader.getFileName());
        });
        horizontalLayout.removeAll();
        horizontalLayout.add(upload);
    }
}
