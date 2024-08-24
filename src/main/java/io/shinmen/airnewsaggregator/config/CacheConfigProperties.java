package io.shinmen.airnewsaggregator.config;

import java.util.List;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import lombok.Data;

@Configuration
@ConfigurationProperties(prefix = "cache-manager")
@Data
public class CacheConfigProperties {
    private List<CaffeineCache> caffeineCaches;

    @Data
    public static class CaffeineCache {
        private List<String> cacheNames;
        private String spec;
    }
}
