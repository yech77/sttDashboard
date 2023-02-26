package com.stt.dash.ui.views.bulksms;

import com.stt.dash.app.OProperties;
import com.stt.dash.app.security.CurrentUser;
import com.stt.dash.backend.data.entity.Agenda;
import com.stt.dash.backend.util.AgendaFileUtils;
import com.stt.dash.ui.crud.OnUIForm;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.component.upload.Receiver;
import com.vaadin.flow.component.upload.Upload;
import com.vaadin.flow.component.upload.receivers.MemoryBuffer;
import com.vaadin.flow.data.binder.BeanValidationBinder;
import com.vaadin.flow.data.binder.Binder;

import java.io.InputStream;
import java.io.OutputStream;

public class BulkSmsForm extends FormLayout implements OnUIForm {
    private static final String MOVE_FILE_MSG = "Arrastrar nuevo archivo aquí";
    Binder<Agenda> binder = new BeanValidationBinder<>(Agenda.class);
    /**/
    TextField nameBox = new TextField("Nombre de la agenda");
    TextField descriptionBox = new TextField("Descripción");
    /**/
    private InputStream stream;
    private final HorizontalLayout horizontalLayout = new HorizontalLayout();
    private final CurrentUser currentUser;
    private OProperties properties;
    private TextField fileName = new TextField();
    private TextField fileNameOriginal = new TextField();

    private boolean isFileUploaded = false;

    public BulkSmsForm(CurrentUser currentUser, OProperties properties) {
        this.currentUser = currentUser;
        this.properties = properties;
        setResponsiveSteps(
                new ResponsiveStep("25em", 1, ResponsiveStep.LabelsPosition.TOP),
                new ResponsiveStep("32em", 2, ResponsiveStep.LabelsPosition.TOP));
        binder.forField(nameBox)
                .asRequired("Debe colocar un nombre")
                .bind(Agenda::getName, Agenda::setName);
        binder.bind(descriptionBox, "description");
        binder.forField(fileName)
                .asRequired("La agenda debe tener un nombre")
                .bind(Agenda::getFileName, Agenda::setFileName);
        binder.bind(fileNameOriginal, "fileNameOriginal");
        /**/
        doUpload("Añadir archivo aquí", properties);
//        nameBox.addBlurListener(t -> {
//            fileName.setInvalid(isFileUploaded);
//        });
        add(nameBox, descriptionBox, horizontalLayout);
    }

    private void doUpload(String uploadLabel, OProperties properties) {
        Upload upload = confUpload(uploadLabel, properties);
        addUploadToUI(upload);
    }

    private Upload confUpload(String uploadLabel, OProperties properties) {
        MemoryBuffer fileUploader = new MemoryBuffer();
        Upload upload = new Upload(fileUploader);
        upload.setDropLabel(new Span(uploadLabel));
        /* TODO: Descablear */
        upload.setMaxFileSize(100 * 1024 * 1024);
        upload.addSucceededListener(event -> {
            stream = fileUploader.getInputStream();
            AgendaFileUtils.setBaseDir(properties.getAgendaFilePathUpload());
            String fileName = AgendaFileUtils.createUniqueFileName(fileUploader.getFileName());
            AgendaFileUtils.createAgendaFile(fileName, stream);
            this.fileName.setValue(fileName);
            this.fileNameOriginal.setValue(fileUploader.getFileName());
            isFileUploaded = true;
        });
        upload.addFinishedListener(event -> {
            System.out.println("Algo");
        });
        return upload;
    }

    private void addUploadToUI(Upload upload) {
        horizontalLayout.removeAll();
        horizontalLayout.add(upload);
    }

    public Binder<Agenda> getBinder() {
        return binder;
    }

    @Override
    public void onUI() {
        doUpload(MOVE_FILE_MSG, properties);
    }

    @Override
    public void onFieldUI() {
        nameBox.setErrorMessage("El nombre ya existe");
        nameBox.setInvalid(true);
    }
}
