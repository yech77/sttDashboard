package com.stt.dash.backend.util.ws;

import com.stt.dash.backend.data.SystemIdCopycatDTO;
import com.stt.dash.backend.data.bean.SyncDTO;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;

import java.io.IOException;

public class SyncSystemIdWebClient extends OWebClient<SystemIdCopycatDTO> {

    public SyncSystemIdWebClient(WebClient webClient, Class<SystemIdCopycatDTO> c) {
        super(webClient, c);
    }

    public Flux<SystemIdCopycatDTO> callSyncData(Integer sys) throws IOException {
        String url = url_sync_sys.replace("{id}", String.valueOf(sys));
        return super.getFluxOResponse(url);
    }

}
