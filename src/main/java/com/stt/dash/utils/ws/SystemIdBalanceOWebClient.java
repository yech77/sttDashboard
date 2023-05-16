/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.stt.dash.utils.ws;

import com.stt.dash.backend.util.ws.SystemIdBalanceOResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.io.IOException;

/**
 * @since @author yech77
 */
@Component
public class SystemIdBalanceOWebClient extends OWebClient<SystemIdBalanceOResponse> {

    public SystemIdBalanceOWebClient(WebClient webClient) {
        super(SystemIdBalanceOResponse.class, webClient);
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
}
