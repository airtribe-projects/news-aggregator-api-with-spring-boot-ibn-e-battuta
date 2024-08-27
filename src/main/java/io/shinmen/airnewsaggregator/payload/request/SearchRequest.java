package io.shinmen.airnewsaggregator.payload.request;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

import lombok.Builder;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Getter
@Builder
public class SearchRequest {
    private String query;
    private String page;
    private String pageSize;

    public String toCacheKey() {
        List<String> keys = Stream.of(
                normalize(query).replace(" ", "-"),
                normalize(pageSize),
                normalize(page))
                .filter(s -> !s.isEmpty())
                .flatMap(s -> Arrays.stream(s.split(",")))
                .map(s -> s.trim().replace(" ", "-"))
                .sorted()
                .toList();

        String cacheKey = String.join("-", keys);

        log.info("Cache key: {}", cacheKey);

        return cacheKey;
    }

    private String normalize(String value) {
        return value != null ? value : "";
    }
}
