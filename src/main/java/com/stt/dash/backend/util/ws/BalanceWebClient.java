package com.stt.dash.backend.util.ws;

import com.stt.dash.backend.data.ClientCopycatDTO;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.io.IOException;

public class BalanceWebClient extends OWebClient<SystemIdBalanceOResponse> {

    public BalanceWebClient(WebClient webClient, Class<SystemIdBalanceOResponse> c) {
        super(webClient, c);
    }

    public Mono<SystemIdBalanceOResponse> callSyncData(String systemid, Integer creditUsed) throws IOException {
        String url = url_update_lockbalance.replace("{system_id}", systemid).replace("{credit_used}", String.valueOf(creditUsed));
        return super.getMonoOResponse(url);
    }

}
