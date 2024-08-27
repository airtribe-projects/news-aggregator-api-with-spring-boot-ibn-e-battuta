package io.shinmen.airnewsaggregator.controller;

import static io.shinmen.airnewsaggregator.utility.Constants.API_CACHE;
import static io.shinmen.airnewsaggregator.utility.Constants.API_CACHE_NAME_PATH;
import static io.shinmen.airnewsaggregator.utility.Messages.CACHE_CLEARED_MESSAGE;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import io.shinmen.airnewsaggregator.payload.response.CacheResponse;
import io.shinmen.airnewsaggregator.payload.response.MessageResponse;
import io.shinmen.airnewsaggregator.service.CacheService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping(API_CACHE)
@RequiredArgsConstructor
public class CacheController {

    private final CacheService cacheService;

    @GetMapping
    public ResponseEntity<List<CacheResponse>> getCacheStats() {

        log.info("Received request to get stats of all caches");

        final List<CacheResponse> response = cacheService.getAllCache();

        return ResponseEntity.ok(response);
    }

    @DeleteMapping(API_CACHE_NAME_PATH)
    public ResponseEntity<MessageResponse> clearCache(@PathVariable final String cacheName) {

        log.info("Received request to clear cache: {}", cacheName);

        cacheService.clearCache(cacheName);

        return ResponseEntity.ok(MessageResponse.builder()
                .message(CACHE_CLEARED_MESSAGE)
                .build());
    }

    @GetMapping(API_CACHE_NAME_PATH)
    public ResponseEntity<CacheResponse> getCacheData(@PathVariable final String cacheName,
            @RequestParam(required = false) final String key) {

        log.info("Received request to get cache: {} with optional key: {}", cacheName,
                key == null ? "no key provided" : key);

        final CacheResponse response = cacheService.getCache(cacheName, key);

        return ResponseEntity.ok(response);
    }
}
