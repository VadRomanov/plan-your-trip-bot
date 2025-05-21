package com.planyourtrip.bot.config.properties;

import feign.Logger;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties("http.client")
public class HttpClientProperties {
  private PoolProperties pool;
  private Logger.Level feignLoggerLevel;

  @Data
  public static class PoolProperties {
    private Integer maxTotal;
    private Integer defaultMaxPerRoute;
  }
}
