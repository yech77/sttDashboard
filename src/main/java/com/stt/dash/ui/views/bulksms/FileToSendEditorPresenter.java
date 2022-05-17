package com.stt.dash.ui.views.bulksms;

import com.stt.dash.app.OProperties;
import com.stt.dash.app.session.ListGenericBean;
import com.stt.dash.backend.data.entity.User;
import com.stt.dash.backend.service.AgendaService;
import com.stt.dash.backend.util.ws.OWebClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class FileToSendEditorPresenter {
    private final static Logger log = LoggerFactory.getLogger(FileToSendEditorPresenter.class);
    private final FileToSendEditor view;
    private final AgendaService agendaService;
    private final ListGenericBean<User> userChildrenList;
    private final ListGenericBean<String> systemIdList;
    private final OProperties properties;
    private final WebClient webClient;

    public final static String yyyy_MM_dd = "yyyy-MM-dd";

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
        this.webClient = webClient;
    }

    public void setAgendaItems() {
        view.setComboAgendaItems(agendaService.getAllValidAgendasInFamily(userChildrenList.getList()));
    }

    public void setSystemIdItems() {
        view.setComboSystemidItems(systemIdList.getList());
    }

    public Integer callBalance(String systemid, LocalDateTime dateTime) throws IOException {
        String stringDate = dateTime.format(DateTimeFormatter.ofPattern(yyyy_MM_dd));
        OWebClient<Integer> we = new OWebClient<>(webClient, Integer.class);
        Mono<Integer> monoOResponse;
        try {
            log.info("Llamando: [{}]", properties.getOrinocoHost() + "orinoco-admin/ws/data/sid/balance/cod/" + systemid + "/date/" + stringDate);
            monoOResponse = we.getMonoOResponse(properties.getOrinocoHost() + "orinoco-admin/ws/data/sid/balance/cod/" + systemid + "/date/" + stringDate);
            return monoOResponse.block();
        } catch (Exception e) {
            log.error("", e);
            throw e;
        }
    }
}
