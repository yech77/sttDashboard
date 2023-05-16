/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.stt.dash.utils.ws;

import com.stt.dash.Application;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.util.retry.Retry;

import java.io.IOException;


/**
 * @param <T>
 * @author yech77
 */
@Slf4j
public class OWebClient<T> {

    protected Class<T> clazz;
    private final WebClient webClient;
    private static final String ORINOCO_WS_CONTEXT = "orinoco-admin/";
    // TODO: Descablear
    final static String url_smsSave = ORINOCO_WS_CONTEXT + "ws/data/sms/save/";
    final static String url_smsSaveAll = ORINOCO_WS_CONTEXT + "ws/data/sms/saveall/";
    final static String url_smsSaveAllBoolean = ORINOCO_WS_CONTEXT + "ws/data/sms/saveall/boolean/";
    final static String url_routingMOAll = ORINOCO_WS_CONTEXT + "ws/data/routingmo/all";
    final static String url_routingMO_find_keyword = ORINOCO_WS_CONTEXT + "ws/data/routingmo/";
    final static String url_routingAll = ORINOCO_WS_CONTEXT + "ws/data/routing/all";
    final static String url_carrierRangesCharcode = ORINOCO_WS_CONTEXT + "ws/data/range/carrier/cod/";
    final static String url_systemBalanceCharcode = ORINOCO_WS_CONTEXT + "ws/data/sid/balance/cod/";
    final static String url_smppClientCharcode = ORINOCO_WS_CONTEXT + "ws/data/clientport/cod/";
    final static String url_integrationPortIntCharcode = ORINOCO_WS_CONTEXT + "ws/data/port/integration/cod/";
    final static String url_integrationPortAll = ORINOCO_WS_CONTEXT + "ws/data/port/all";
    final static String url_integrationPortCharcode = ORINOCO_WS_CONTEXT + "ws/data/port/cod/";

    final static String url_integrationCharcode = ORINOCO_WS_CONTEXT + "ws/data/integration/cod/";
    /* AssignedShortcode*/
    protected final static String url_assignedShortcode_all = ORINOCO_WS_CONTEXT + "ws/data/sourceaddress/assigned/all";
    protected final static String url_assignedShortcode_systemid = ORINOCO_WS_CONTEXT + "ws/data/sourceaddress/systemid/";
    protected final static String url_assignedShortcode_sc = ORINOCO_WS_CONTEXT + "orinoco-admin/ws/data/sourceaddress/";

    final static String url_integrationAll = ORINOCO_WS_CONTEXT + "ws/data/integration/all";

    //    final static String url_portCharcode = ORINOCO_WS_CONTEXT + "ws/data/clientport/cod/";
    final static String url_carrierCharcode = ORINOCO_WS_CONTEXT + "ws/data/carrier/cod/";
    final static String url_carrierAll = ORINOCO_WS_CONTEXT + "ws/data/carrier/all";
    /* LOG4J2 */

    public OWebClient(Class<T> c, WebClient webClient) {
        this.clazz = c;
        this.webClient = webClient;
    }

    /**
     * @param charcode
     * @return
     * @throws IOException lanza una excepcion si vence el RetryMax, que se puede dar porque
     *                     no encontro el dato a buscar
     */
    public Mono<T> getMonoOResponse(String charcode) throws IOException {
        log.debug("[WS] [CALL] [{}] [{}]..", clazz.getName(), charcode);
        Mono<T> createdEmployee = webClient.get()
                .uri(ORINOCO_WS_CONTEXT + charcode)
                .retrieve()
                .onStatus(HttpStatus::is5xxServerError, response -> Mono.error(new RuntimeException("Probablemente no encontrado")))
                .bodyToMono(clazz)
                .retryWhen(Retry.max(2));

//                .retryWhen(Retry.backoff(2, Duration.ofSeconds(3))
//                        .filter(throwable -> throwable instanceof RuntimeException)
//                        .onRetryExhaustedThrow((retryBackoffSpec, retrySignal) -> {
//                            throw new RuntimeException("External Service failed to process after max retries");
//                        })
        log.debug("MONO {}", createdEmployee);
        return createdEmployee;
    }

    public Flux<T> getFluxOResponse(String charcode) throws IOException {
        log.info("[WS] [CALL] [{}] [{}]..", clazz.getName(), charcode);
        Flux<T> createdEmployee = webClient.get()
                .uri(ORINOCO_WS_CONTEXT + charcode)
                .retrieve()
                .bodyToFlux(clazz)
                .retryWhen(Retry.max(2));
        return createdEmployee;
    }
}
