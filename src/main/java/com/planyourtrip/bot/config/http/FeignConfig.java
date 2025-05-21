package com.planyourtrip.bot.config.http;

import com.planyourtrip.bot.config.properties.HttpClientProperties;
import feign.Client;
import feign.Contract;
import feign.Logger;
import feign.codec.Decoder;
import feign.codec.Encoder;
import feign.codec.ErrorDecoder;
import feign.form.spring.SpringFormEncoder;
import feign.httpclient.ApacheHttpClient;
import feign.jackson.JacksonDecoder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.http.HttpMessageConverters;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.openfeign.support.SpringEncoder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
@RequiredArgsConstructor
@Slf4j
@EnableConfigurationProperties(HttpClientProperties.class)
public class FeignConfig {
    private final HttpClientProperties httpClientProperties;

    @Qualifier("feignHttpClientConnectionManager")
    private final PoolingHttpClientConnectionManager poolingHttpClientConnectionManager;

    @Bean
    public Encoder multipartFormEncoder() {
        return new SpringFormEncoder(new SpringEncoder(
                () -> new HttpMessageConverters(new RestTemplate().getMessageConverters())));
    }

    @Bean
    public Decoder feignDecoder() {
        return ((response, type) -> {
            if (type.equals(byte[].class)) {
                return IOUtils.toByteArray(response.body().asInputStream());
            }
            return new JacksonDecoder().decode(response, type);
        });
    }

    @Bean
    public Client inboundFeignClient() {
        return new ApacheHttpClient(buildHttpClient());
    }

    @Bean
    public Contract feignContract() {
        return new Contract.Default();
    }

    @Bean
    public ErrorDecoder feignErrorDecoder() {
        return null;
    }

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
