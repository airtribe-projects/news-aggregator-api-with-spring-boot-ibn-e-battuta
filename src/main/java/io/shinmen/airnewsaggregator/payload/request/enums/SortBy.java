package io.shinmen.airnewsaggregator.payload.request.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public enum SortBy {
    RELEVANCY("relevancy"), POPULARITY("popularity"), PUBLISHEDAT("publishedAt"), UNKNOWN("unknown");

    private final String value;

    SortBy(String value) {
        this.value = value;
    }

    @JsonValue
    public String toValue() {
        return this.value;
    }

    @JsonCreator
    public static SortBy fromValue(String value) {
        try {
            return SortBy.valueOf(value.toUpperCase());
        } catch (IllegalArgumentException e) {
            log.error("Illegal SortBy value: {}", value, e);
            return UNKNOWN;
        }
    }
}
