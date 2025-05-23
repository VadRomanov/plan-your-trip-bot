package com.planyourtrip.bot.config.http;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.planyourtrip.bot.config.properties.HttpClientProperties;
import feign.Client;
import feign.Contract;
import feign.Logger;
import feign.codec.Decoder;
import feign.codec.Encoder;
import feign.httpclient.ApacheHttpClient;
import feign.jackson.JacksonDecoder;
import feign.jackson.JacksonEncoder;
import feign.optionals.OptionalDecoder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
@Slf4j
@EnableConfigurationProperties(HttpClientProperties.class)
public class FeignConfig {
    private final ObjectMapper objectMapper;
  //  private final FeignErrorDecoder feignErrorDecoder;
    private final HttpClientProperties httpClientProperties;

    @Qualifier("feignHttpClientConnectionManager")
    private final PoolingHttpClientConnectionManager poolingHttpClientConnectionManager;

    @Bean
    public Encoder feignEncoder() {
        return new JacksonEncoder(objectMapper);
    }

    @Bean
    public Decoder feignDecoder() {
        return new OptionalDecoder(new JacksonDecoder(objectMapper));
    }

    @Bean
    public Client inboundFeignClient() {
        return new ApacheHttpClient(buildHttpClient());
    }

    @Bean
    public Contract feignContract() {
        return new Contract.Default();
    }

/*
    @Bean
    public ErrorDecoder feignErrorDecoder() {
        return feignErrorDecoder;
    }
*/

    @Bean
    public Logger.Level feignLoggerLevel() {
        return httpClientProperties.getFeignLoggerLevel();
    }

    private CloseableHttpClient buildHttpClient() {
        log.trace("Build http client with pooling connection manager");
        return HttpClients.custom()
                .setConnectionManager(poolingHttpClientConnectionManager)
                .build();
    }

}
