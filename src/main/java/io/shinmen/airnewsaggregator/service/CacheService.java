package io.shinmen.airnewsaggregator.service;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Service;

import com.github.benmanes.caffeine.cache.Cache;

import io.shinmen.airnewsaggregator.exception.CacheNotFoundException;
import io.shinmen.airnewsaggregator.exception.InvalidCacheTypeException;
import io.shinmen.airnewsaggregator.exception.KeyNotFoundException;
import io.shinmen.airnewsaggregator.payload.response.CacheResponse;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CacheService {

    private final CacheManager cacheManager;

    public List<CacheResponse> getAllCache() {

        return cacheManager.getCacheNames().stream()
                .map(cacheName -> {
                    org.springframework.cache.Cache cache = cacheManager.getCache(cacheName);
                    if (cache == null) {
                        throw new CacheNotFoundException(cacheName);
                    }

                    try {
                        Cache<?, ?> nativeCache = (Cache<?, ?>) cache.getNativeCache();
                        Map<?, ?> cacheData = nativeCache.asMap();

                        return CacheResponse.builder()
                                .cacheName(cacheName)
                                .size(nativeCache.estimatedSize())
                                .data(cacheData)
                                .build();

                    } catch (ClassCastException e) {
                        throw new InvalidCacheTypeException(cacheName);
                    }
                })
                .collect(Collectors.toList());
    }

    public void clearCache(String cacheName) {
        if (!cacheManager.getCacheNames().contains(cacheName)) {
            throw new CacheNotFoundException(cacheName);
        }

        org.springframework.cache.Cache cache = cacheManager.getCache(cacheName);
        if (cache == null) {
            throw new CacheNotFoundException(cacheName);
        }

        cache.clear();
    }

    public CacheResponse getCache(String cacheName, String key) {
        if (!cacheManager.getCacheNames().contains(cacheName)) {
            throw new CacheNotFoundException(cacheName);
        }

        org.springframework.cache.Cache cache = cacheManager.getCache(cacheName);
        if (cache == null) {
            throw new CacheNotFoundException(cacheName);
        }

        try {
            Cache<?, ?> nativeCache = (Cache<?, ?>) cache.getNativeCache();

            if (key != null) {
                Object value = Optional.ofNullable(cache.get(key, Object.class))
                        .orElseThrow(() -> new KeyNotFoundException(key, cacheName));

                return CacheResponse.builder()
                        .cacheName(cacheName)
                        .size(1)
                        .data(Map.of(key, value))
                        .build();
            }

            Map<?, ?> cacheData = nativeCache.asMap();

            return CacheResponse.builder()
                    .cacheName(cacheName)
                    .size(nativeCache.estimatedSize())
                    .data(cacheData)
                    .build();

        } catch (ClassCastException e) {
            throw new InvalidCacheTypeException(cacheName);
        }
    }

    public void cleanUpCache() {
        cacheManager.getCacheNames().stream()
                .map(cacheManager::getCache)
                .filter(cache -> cache != null && cache.getNativeCache() instanceof Cache)
                .map(cache -> (Cache<?, ?>) cache.getNativeCache())
                .forEach(Cache::cleanUp);
    }
}
