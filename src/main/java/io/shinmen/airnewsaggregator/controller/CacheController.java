package io.shinmen.airnewsaggregator.controller;

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

@RestController
@RequestMapping("/api/cache")
@RequiredArgsConstructor
public class CacheController {

    private final CacheService cacheService;

    @GetMapping
    public ResponseEntity<List<CacheResponse>> getCacheStats() {
        List<CacheResponse> response = cacheService.getAllCache();
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{cacheName}")
    public ResponseEntity<MessageResponse> clearCache(@PathVariable String cacheName) {
        cacheService.clearCache(cacheName);
        return ResponseEntity.ok(MessageResponse.builder()
                .message("Cache cleared successfully")
                .build());
    }

    @GetMapping("/{cacheName}")
    public ResponseEntity<CacheResponse> getCacheData(@PathVariable String cacheName,
            @RequestParam(required = false) String key) {
        CacheResponse response = cacheService.getCache(cacheName, key);
        return ResponseEntity.ok(response);
    }
}
