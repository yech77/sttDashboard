package com.stt.dash.backend.util.ws;

import com.stt.dash.backend.data.bean.SyncDTO;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.io.IOException;

public class SyncDataOWebClient extends OWebClient<SyncDTO> {

    public SyncDataOWebClient(WebClient webClient, Class<SyncDTO> c) {
        super(webClient, c);
    }

    public Mono<SyncDTO> callSyncDataMono(Integer cli, Integer sys) throws IOException {
        String url = url_sync_data.replace("{idClient}", String.valueOf(cli));
        url = url.replace("{idSys}", String.valueOf(sys));
        return super.getMonoOResponse("orinoco-admin/ws/data/sync/cliid/0/sysid/0");
    }

    public Flux<SyncDTO> callSyncDataFlux(Integer cli, Integer sys) throws IOException {
        String url = url_sync_data.replace("{idClient}", String.valueOf(cli));
        url = url.replace("{idSys}", String.valueOf(sys));
        return super.getFluxOResponse("orinoco-admin//ws/data/sync/cliid/0/sysid/0");
    }

}
