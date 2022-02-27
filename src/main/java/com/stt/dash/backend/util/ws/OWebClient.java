/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.stt.dash.backend.util.ws;

import org.apache.logging.log4j.LogManager;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.util.retry.Retry;

import java.io.IOException;

/**
 * @param <T>
 * @author yech77
 */
public class OWebClient<T> {

    //    public final static WebClient webClient = WebClient.builder()
////            .baseUrl("http://localhost:8080")
//            .baseUrl("http://ttvm3.eastus2.cloudapp.azure.com:8080")
//            .defaultHeaders(header -> header.setBasicAuth("orinoco", "0R1n0coRIv3r$"))
//            .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
//            .build();
    protected Class<T> xClass;
    protected WebClient webClient;
    final static String url_sync_cli = "/ws/data/sync/cli/{id}";
    final static String url_sync_sys = "/ws/data/sync/sys/{id}";
    public final static String url_sync_data = "/ws/data/sync/cliid/{idClient}/sysid/{idSys}";


    // TODO: Descablear
    final static String url_smsSave = "orinoco-admin/ws/data/sms/save/";
    final static String url_smsSaveAll = "orinoco-admin/ws/data/sms/saveall/";
    final static String url_smsSaveAllBoolean = "orinoco-admin/ws/data/sms/saveall/boolean/";
    final static String url_routingMOAll = "orinoco-admin/ws/data/routingmo/all";
    final static String url_routingAll = "orinoco-admin/ws/data/routing/all";
    final static String url_carrierRangesCharcode = "orinoco-admin/ws/data/range/carrier/cod/";
    final static String url_systemBalanceSet = "orinoco-admin/ws/data/sid/balance/cod/";
    final static String url_systemBalanceCharcode = "orinoco-admin/ws/data/sid/balance/cod/";
    final static String url_smppClientCharcode = "orinoco-admin/ws/data/clientport/cod/";
    final static String url_integrationPortIntCharcode = "orinoco-admin/ws/data/port/integration/cod/";
    final static String url_integrationPortAll = "orinoco-admin/ws/data/port/all";
    final static String url_integrationPortCharcode = "orinoco-admin/ws/data/port/cod/";
    final static String url_oprocessorCharcode = "orinoco-admin/ws/data/processor/cod/";
    final static String url_integrationCharcode = "orinoco-admin/ws/data/integration/cod/";
    /* AssignedShortcode*/
    protected final static String url_assignedShortcode_all = "orinoco-admin/ws/data/sourceaddress/assigned/all";
    protected final static String url_assignedShortcode_systemid = "orinoco-admin/ws/data/sourceaddress/systemid/";
    protected final static String url_assignedShortcode_sc = "orinoco-admin/orinoco-admin/ws/data/sourceaddress/";

    final static String url_integrationAll = "orinoco-admin/ws/data/integration/all";
    final static String url_countryIso2 = "orinoco-admin/ws/data/country/iso2/";
    final static String url_countryAll = "orinoco-admin/ws/data/country/all";

    //    final static String url_portCharcode = "orinoco-admin/ws/data/clientport/cod/";
    final static String url_carrierCharcode = "orinoco-admin/ws/data/carrier/cod/";
    final static String url_carrierAll = "orinoco-admin/ws/data/carrier/all";
    final static String url_carrierAllWithRanges = "orinoco-admin/ws/data/range/carrier/all";
    /* LOG4J2 */
    private static org.apache.logging.log4j.Logger log
            = LogManager.getLogger(OWebClient.class);

    @Deprecated
    public OWebClient(Class<T> c) {
        this.xClass = c;
    }

    public OWebClient(WebClient webClient, Class<T> c) {
        this.webClient = webClient;
        this.xClass = c;
    }

    public Mono<T> getMonoOResponse(String charcode) throws IOException {
        log.info("[WS] [CALL] [{}] [{}]..", xClass.getName(), charcode);
        T ir;
        Mono<T> createdEmployee = webClient.get()
                .uri(charcode)
                .retrieve()
                .bodyToMono(xClass)
                .retryWhen(Retry.max(2));
        log.info("MONO {}", createdEmployee);
        return createdEmployee;
    }

    public Flux<T> getFluxOResponse(String charcode) throws IOException {
        log.info("[WS] [CALL] [{}] [{}]..", xClass.getName(), charcode);
        T ir;
        Flux<T> createdEmployee = webClient.get()
                .uri(charcode)
                .retrieve()
                .bodyToFlux(xClass)
                .retryWhen(Retry.max(2));
        return createdEmployee;
    }
}
