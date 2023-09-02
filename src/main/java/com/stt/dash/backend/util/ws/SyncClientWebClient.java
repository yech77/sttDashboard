package com.stt.dash.backend.util.ws;

import com.stt.dash.backend.data.ClientCopycatDTO;
import com.stt.dash.backend.data.SystemIdCopycatDTO;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;

import java.io.IOException;

public class SyncClientWebClient extends OWebClient<ClientCopycatDTO> {

    public SyncClientWebClient(WebClient webClient, Class<ClientCopycatDTO> c) {
        super(webClient, c);
    }

    public Flux<ClientCopycatDTO> callSyncData(Integer sys) throws IOException {
        String url = url_sync_cli.replace("{id}", String.valueOf(sys));
        return super.getFluxOResponse(url);
    }
}
