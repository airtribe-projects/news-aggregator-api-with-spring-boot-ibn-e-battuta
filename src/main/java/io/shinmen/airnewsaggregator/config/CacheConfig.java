package io.shinmen.airnewsaggregator.config;

import org.springframework.cache.CacheManager;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;

import lombok.RequiredArgsConstructor;

@Configuration
@RequiredArgsConstructor
public class CacheConfig {

    private final CacheConfigProperties cacheConfigProperties;

    @Bean
    CacheManager cacheManager() {
        final CaffeineCacheManager manager = new CaffeineCacheManager();
        cacheConfigProperties.getCaffeineCaches().forEach(cache -> createCaffeineCache(manager, cache));
        return manager;
    }

    private void createCaffeineCache(CaffeineCacheManager manager, CacheConfigProperties.CaffeineCache caffeineCache) {
        Cache<Object, Object> cache = Caffeine.from(caffeineCache.getSpec()).build();
        manager.registerCustomCache(caffeineCache.getCacheName(), cache);
    }
}