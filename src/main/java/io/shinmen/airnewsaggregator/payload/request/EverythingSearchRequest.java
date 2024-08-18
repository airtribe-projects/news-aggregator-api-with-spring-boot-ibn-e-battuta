package io.shinmen.airnewsaggregator.payload.request;

import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class EverythingSearchRequest {
    private String query;
    private String sources;
    private String domains;
    private String from;
    private String to;
    private String language;
    private String sortBy;
    private String pageSize;
    private String page;

    public String toCacheKey() {
        return Stream.of(query, sources, domains, from, to, language, sortBy, page, pageSize)
                .filter(Objects::nonNull)
                .map(String::valueOf)
                .collect(Collectors.joining("-"));
    }
}
