package io.shinmen.airnewsaggregator.payload.request;

import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class TopHeadLinesSearchRequest {
    private String query;
    private String page;
    private String pageSize;
    private String category;
    private String country;
    private String sources;
    private boolean useSources;

    public String toCacheKey() {
        if (useSources)
            return Stream.of(query, sources, page, pageSize)
                    .filter(Objects::nonNull)
                    .map(String::valueOf)
                    .collect(Collectors.joining("-"));

        return Stream.of(query, category, country, page, pageSize)
                .filter(Objects::nonNull)
                .map(String::valueOf)
                .collect(Collectors.joining("-"));
    }
}
