package com.stt.dash.ui.views.bulksms;

import com.stt.dash.app.OProperties;
import com.stt.dash.app.session.ListGenericBean;
import com.stt.dash.backend.data.entity.User;
import com.stt.dash.backend.service.AgendaService;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

public class FileToSendEditorPresenter {
    private final FileToSendEditor view;
    private final AgendaService agendaService;
    private final ListGenericBean<User> userChildrenList;
    private final ListGenericBean<String> systemIdList;
    private final OProperties properties;

    /* TODO: ACA ESTOY AGREGANDO PARA BUSCAR LA URL DEL SERVICO DE SALDO DE CREDENCIAL */
    public FileToSendEditorPresenter(FileToSendEditor view,
                                     @Qualifier("getUserMeAndChildren") ListGenericBean<User> userChildrenList,
                                     AgendaService agendaService,
                                     @Qualifier("getUserSystemIdString") ListGenericBean<String> systemIdList,
                                     WebClient webClient,
                                     OProperties properties) {
        this.view = view;
        this.agendaService = agendaService;
        this.userChildrenList = userChildrenList;
        this.systemIdList = systemIdList;
        this.properties = properties;
    }

    public void setAgendaItems() {
        view.setComboAgendaItems(agendaService.getAllValidAgendasInFamily(userChildrenList.getList()));
    }

    public void setSystemIdItems() {
        view.setComboSystemidItems(systemIdList.getList());
    }
}
