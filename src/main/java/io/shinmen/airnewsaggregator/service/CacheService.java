package io.shinmen.airnewsaggregator.service;

import java.util.List;
import java.util.Map;
import java.util.Optional;

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
                    final var cache = cacheManager.getCache(cacheName);
                    if (cache == null) {
                        throw new CacheNotFoundException(cacheName);
                    }

                    try {
                        final Cache<?, ?> nativeCache = (Cache<?, ?>) cache.getNativeCache();
                        final Map<?, ?> cacheData = nativeCache.asMap();

                        return CacheResponse.builder()
                                .cacheName(cacheName)
                                .size(nativeCache.estimatedSize())
                                .data(cacheData)
                                .build();

                    } catch (ClassCastException e) {
                        throw new InvalidCacheTypeException(cacheName);
                    }
                })
                .toList();
    }

    public void clearCache(final String cacheName) {
        if (!cacheManager.getCacheNames().contains(cacheName)) {
            throw new CacheNotFoundException(cacheName);
        }

        final var cache = cacheManager.getCache(cacheName);
        if (cache == null) {
            throw new CacheNotFoundException(cacheName);
        }

        cache.clear();
    }

    public CacheResponse getCache(final String cacheName, final String key) {
        if (!cacheManager.getCacheNames().contains(cacheName)) {
            throw new CacheNotFoundException(cacheName);
        }

        final var cache = cacheManager.getCache(cacheName);
        if (cache == null) {
            throw new CacheNotFoundException(cacheName);
        }

        try {
            final Cache<?, ?> nativeCache = (Cache<?, ?>) cache.getNativeCache();

            if (key != null) {
                final Object value = Optional.ofNullable(cache.get(key, Object.class))
                        .orElseThrow(() -> new KeyNotFoundException(cacheName, key));

                return CacheResponse.builder()
                        .cacheName(cacheName)
                        .size(1)
                        .data(Map.of(key, value))
                        .build();
            }

            final Map<?, ?> cacheData = nativeCache.asMap();

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
