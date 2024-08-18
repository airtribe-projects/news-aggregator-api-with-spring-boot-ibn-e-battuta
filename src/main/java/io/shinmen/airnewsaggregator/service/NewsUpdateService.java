package io.shinmen.airnewsaggregator.service;

import org.springframework.cache.CacheManager;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;

import io.shinmen.airnewsaggregator.model.enums.Country;
import io.shinmen.airnewsaggregator.payload.request.TopHeadLinesSearchRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class NewsUpdateService {

    private final NewsApiService newsApiService;
    private final CacheManager cacheManager;

    @Scheduled(fixedRate = 900000) // Run every 15 minutes
    public void updateCachedNews() throws JsonProcessingException {
        log.info("Starting scheduled update of cached news");

        cacheManager.getCache("topHeadlines").clear();

        TopHeadLinesSearchRequest request = TopHeadLinesSearchRequest.builder()
                .country(Country.US.toValue())
                .useSources(false)
                .page("1")
                .pageSize("20")
                .build();

        newsApiService.getTopHeadlines(request);

        log.info("Completed scheduled update of cached news");
    }
}
