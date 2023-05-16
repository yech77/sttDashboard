/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.stt.dash.backend.util.ws;

import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

/**
 * @author yech77
 * @since
 */
public class SystemIdOWebClient extends OWebClient<SystemIdOResponse> {

    public SystemIdOWebClient(WebClient webClient) {
        super(webClient, SystemIdOResponse.class);
    }

    public SystemIdOResponse findSystemIdByName(String charcode) throws Exception {
        Mono<SystemIdOResponse> mono = super.getMonoOResponse(charcode);
        SystemIdOResponse block = mono.block();
        return block;
    }
}
