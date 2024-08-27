package io.shinmen.airnewsaggregator.config;

import static io.shinmen.airnewsaggregator.utility.Constants.CACHE_MANAGER_PREFIX;

import java.util.List;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import lombok.Data;

@Configuration
@ConfigurationProperties(prefix = CACHE_MANAGER_PREFIX)
@Data
public class CacheConfigProperties {
    private final List<CaffeineCache> caffeineCaches;

    @Data
    public static class CaffeineCache {
        private final String cacheName;
        private final String spec;
    }
}
