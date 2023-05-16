package com.stt.dash.backend.service;

import com.stt.dash.backend.util.ws.SystemIdOResponse;
import com.stt.dash.backend.util.ws.SystemIdOWebClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Optional;

@Component
@Slf4j
@CacheConfig(cacheNames = "systemId", cacheManager = "cacheRefresh")
public class SystemIdWebClientService {
    private final String url_findSystemIdByName = "orinoco-admin/ws/data/systemid/cod/";
    private SystemIdOWebClient systemIdOWebClient;

    public SystemIdWebClientService(WebClient webClient) {
        this.systemIdOWebClient = new SystemIdOWebClient(webClient);
    }

    @Cacheable(sync = true)
    public Optional<SystemIdOResponse> findSystemIdByName(String finanzas1) {
        System.out.println("Paso por el cache....");
        SystemIdOResponse systemIdByName = null;
        try {
            systemIdByName = systemIdOWebClient.findSystemIdByName(url_findSystemIdByName + finanzas1);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return Optional.ofNullable(systemIdByName);
    }
}
