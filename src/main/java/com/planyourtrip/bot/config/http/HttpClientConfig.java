package com.planyourtrip.bot.config.http;

import com.planyourtrip.bot.config.properties.HttpClientProperties;
import lombok.RequiredArgsConstructor;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class HttpClientConfig {
    private final HttpClientProperties httpClientProperties;

    @Bean
    public PoolingHttpClientConnectionManager feignHttpClientConnectionManager() {
        PoolingHttpClientConnectionManager connectionManager = new PoolingHttpClientConnectionManager();
        connectionManager.setMaxTotal(httpClientProperties.getPool().getMaxTotal());
        connectionManager.setDefaultMaxPerRoute(httpClientProperties.getPool().getDefaultMaxPerRoute());
        return connectionManager;
    }
}
