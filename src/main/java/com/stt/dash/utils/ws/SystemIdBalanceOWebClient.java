/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.stt.dash.utils.ws;

import com.google.common.eventbus.EventBus;
import com.stt.dash.backend.util.ws.OWebClient;
import com.stt.dash.backend.util.ws.SystemIdBalanceOResponse;
import liquibase.pro.packaged.T;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.util.retry.Retry;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

/**
 * @since @author yech77
 */
@Component
public class SystemIdBalanceOWebClient extends OWebClient<SystemIdBalanceOResponse> {

    private final WebClient webClient;

    public SystemIdBalanceOWebClient(WebClient webClient) {
        super(webClient, SystemIdBalanceOResponse.class);
        this.webClient = webClient;
    }

    public Mono<SystemIdBalanceOResponse> getMonoSystemIdBalancetByCod(String charcode) throws IOException {
        return getMonoOResponse(url_systemBalanceCharcode + charcode);
    }

    public SystemIdBalanceOResponse getSystemIdBalancetByCod(String charcode) throws IOException {
        Mono<SystemIdBalanceOResponse> mono = getMonoOResponse(url_systemBalanceCharcode + charcode);
        return mono.block();
    }

    public Mono<SystemIdBalanceOResponse> updateMonoSystemIdBalancetByCod(String charcode) throws IOException {
        return super.getMonoOResponse(url_systemBalanceCharcode + charcode);
    }

    public SystemIdBalanceOResponse updateSystemIdBalancetByCod(String charcode) throws IOException {
        Mono<SystemIdBalanceOResponse> mono = getMonoOResponse(url_systemBalanceCharcode + charcode);
        return mono.block();
    }


    public Flux<SystemIdBalanceOResponse> getFluxSaveSmsAll(List<String> l) throws IOException {
        String[] nsns = new String[l.size()];
        Flux<SystemIdBalanceOResponse> SmsHandlerFlux = webClient.post()
                .uri(url_systemBalanceList)
                .body(Flux.just(l.toArray(nsns)), List.class)
                .retrieve()
                .bodyToFlux(SystemIdBalanceOResponse.class)
                .retryWhen(Retry.max(2));
        return SmsHandlerFlux;
    }
}
