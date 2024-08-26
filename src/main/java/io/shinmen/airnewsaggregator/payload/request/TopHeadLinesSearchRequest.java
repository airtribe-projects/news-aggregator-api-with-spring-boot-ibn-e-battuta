package io.shinmen.airnewsaggregator.payload.request;

import java.util.Arrays;
import java.util.List;
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

    public String toCacheKey() {
        List<String> keys = Stream.of(
                normalize(query).replace(" ", "-"),
                normalize(sources),
                normalize(country),
                normalize(category),
                normalize(pageSize),
                normalize(page))
                .filter(s -> !s.isEmpty())
                .flatMap(s -> Arrays.stream(s.split(",")))
                .map(s -> s.trim().replace(" ", "-"))
                .sorted()
                .toList();

        return String.join("-", keys);
    }

    private String normalize(String value) {
        return value != null ? value : "";
    }
}
