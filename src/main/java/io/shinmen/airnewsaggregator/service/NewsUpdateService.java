package io.shinmen.airnewsaggregator.service;

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

    private final CacheService cacheService;
    private final NewsApiService newsApiService;

    @Scheduled(fixedRate = 900000)
    public void updateCachedNews() throws JsonProcessingException {
        log.info("Starting scheduled update and cleanup of cached news");

        cacheService.cleanUpCache();

        TopHeadLinesSearchRequest request = TopHeadLinesSearchRequest.builder()
                .country(Country.US.toValue())
                .useSources(false)
                .page("1")
                .pageSize("20")
                .build();

        newsApiService.getTopHeadlines(request);

        log.info("Completed scheduled update and cleanup of cached news");
    }
}
