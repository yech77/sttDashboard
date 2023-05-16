package com.stt.dash.backend.service;

import com.stt.dash.app.OProperties;
import com.stt.dash.backend.util.ws.SystemIdBalanceOResponse;
import com.stt.dash.utils.ws.SystemIdBalanceOWebClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import java.io.IOException;

@Component
@Slf4j
@CacheConfig(cacheNames = "systemid_balance")
public class SystemIdBalanceWebClientService {
    private final SystemIdBalanceOWebClient webClient;

    public SystemIdBalanceWebClientService(SystemIdBalanceOWebClient webClient, OProperties properties) {
        this.webClient = webClient;
    }

    @Cacheable(unless = "#result == null")
    public SystemIdBalanceOResponse findSystemIdBalance(String systemId) {
        try {
            log.info("######### LLAMANDO WEBCLIENT");
            return webClient.getSystemIdBalancetByCod(systemId);
        } catch (IOException e) {
            return null;
        }
    }
}
