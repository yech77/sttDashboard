package com.stt.dash.ui.views.bulksms;

import com.stt.dash.app.OProperties;
import com.stt.dash.app.security.CurrentUser;
import com.stt.dash.app.session.ListGenericBean;
import com.stt.dash.backend.data.entity.User;
import com.stt.dash.backend.service.AgendaService;
import com.stt.dash.backend.service.SystemIdBalanceWebClientService;
import com.stt.dash.backend.service.SystemIdWebClientService;
import com.stt.dash.backend.util.ws.OWebClient;
import com.stt.dash.backend.util.ws.SystemIdBalanceOResponse;
import com.stt.dash.backend.util.ws.SystemIdOResponse;
import com.stt.dash.utils.ws.UtilDto;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.io.IOException;
import java.text.Normalizer;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
public class FileToSendEditorViewPresenter {
    //    private final static Logger log = LoggerFactory.getLogger(FileToSendEditorViewPresenter.class);
    private final FileToSendEditorView view;
    private final AgendaService agendaService;
    private final ListGenericBean<User> userChildrenList;
    private final ListGenericBean<String> systemIdList;
    private final OProperties properties;
    private final WebClient webClient;
    private final SystemIdBalanceWebClientService webClientService;
    private final SystemIdWebClientService systemidService;
    private final CurrentUser currentUser;

    public final static String yyyy_MM_dd = "yyyy-MM-dd";

    public FileToSendEditorViewPresenter(FileToSendEditorView view,
                                         @Qualifier("getMyChildrenAndItsChildrenAndMe") ListGenericBean<User> userChildrenList,
                                         AgendaService agendaService,
                                         @Qualifier("getUserSystemIdString") ListGenericBean<String> systemIdList,
                                         WebClient webClient,
                                         OProperties properties,
                                         SystemIdBalanceWebClientService webClientService,
                                         SystemIdWebClientService systemidService,
                                         CurrentUser currentUser) {
        this.view = view;
        this.agendaService = agendaService;
        this.userChildrenList = userChildrenList;
        this.systemIdList = systemIdList;
        this.properties = properties;
        this.webClient = webClient;
        this.webClientService = webClientService;
        this.systemidService = systemidService;
        this.currentUser = currentUser;
    }

    public void setAgendaItems() {
        view.setComboAgendaItems(agendaService.getAllValidAgendasInFamily(currentUser, userChildrenList.getList()));
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

    public UtilDto findFilterWords(String text) throws IOException {
        OWebClient<UtilDto> we = new OWebClient<>(webClient, UtilDto.class);
        Mono<UtilDto> monoOResponse;
        try {
            log.info("Llamando: [{}]", properties.getOrinocoHost() + "orinoco-admin/ws/data/filterword/validate/" + text);
            monoOResponse = we.getMonoOResponse(properties.getOrinocoHost() + "orinoco-admin/ws/data/filterword/validate/" + text);
            return monoOResponse.block();
        } catch (Exception e) {
            log.error("", e);
            throw e;
        }
    }

    public SystemIdBalanceOResponse findSystemIdBalance(String systemid) {
        return webClientService.findSystemIdBalance(systemid);
    }

    public Optional<SystemIdOResponse> findSystemId(String systemid) {
        return systemidService.findSystemIdByName(systemid);
    }
}
