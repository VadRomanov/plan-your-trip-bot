package com.planyourtrip.bot.config;

import feign.RetryableException;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.retry.RetryPolicy;
import org.springframework.retry.backoff.ExponentialRandomBackOffPolicy;
import org.springframework.retry.policy.ExceptionClassifierRetryPolicy;
import org.springframework.retry.policy.SimpleRetryPolicy;
import org.springframework.retry.support.RetryTemplate;

import java.util.Map;

@Configuration
public class RetryConfig {

    @Bean
    public ExceptionClassifierRetryPolicy exceptionClassifierRetryPolicy() {
        var simpleRetryPolicyUp10Attempts = new SimpleRetryPolicy(10);
        var retryPolicy = new ExceptionClassifierRetryPolicy();
        Map<Class<? extends Throwable>, RetryPolicy> policyMap = Map.of(
                //      TokenExpiredException.class, new SimpleRetryPolicy(2),
                RetryableException.class, simpleRetryPolicyUp10Attempts);
        retryPolicy.setPolicyMap(policyMap);
        return retryPolicy;
    }

    @Bean
    public RetryTemplate retryTemplate() {
        var retryTemplate = new RetryTemplate();
        retryTemplate.setRetryPolicy(exceptionClassifierRetryPolicy());
        var backOffPolicy = new ExponentialRandomBackOffPolicy();
        backOffPolicy.setMaxInterval(15000);
        backOffPolicy.setMultiplier(2);
        backOffPolicy.setInitialInterval(1000);
        retryTemplate.setBackOffPolicy(backOffPolicy);
        return retryTemplate;
    }

}
