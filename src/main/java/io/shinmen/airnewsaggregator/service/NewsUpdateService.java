package io.shinmen.airnewsaggregator.service;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;

import io.shinmen.airnewsaggregator.model.enums.Country;
import io.shinmen.airnewsaggregator.payload.request.TopHeadLinesSearchRequest;
import io.shinmen.airnewsaggregator.utility.Constants;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class NewsUpdateService {

    private final CacheService cacheService;
    private final NewsApiService newsApiService;

    @Scheduled(fixedRate = 30 * 60 * 1000)
    public void updateCachedNews() throws JsonProcessingException {
        log.info("Starting scheduled update and cleanup of cached news");

        cacheService.cleanUpCache();

        final TopHeadLinesSearchRequest request = TopHeadLinesSearchRequest.builder()
                .country(Country.US.toValue())
                .page(Constants.PAGE_DEFAULT)
                .pageSize(Constants.PAGE_SIZE_DEFAULT)
                .build();

        newsApiService.getTopHeadlines(request);

        log.info("Completed scheduled update and cleanup of cached news");
    }

    @Scheduled(fixedRate = 8 * 60 * 60 * 1000)
    public void clearSources() {
        log.info("Starting cleanup of cached sources");

        cacheService.clearCache("sources");

        log.info("Completed cleanup of cached sources");
    }
}
