package io.shinmen.airnewsaggregator.payload.response;

import java.util.Map;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CacheResponse {
    private String cacheName;

    private long size;

    @JsonProperty
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private Map<?, ?> data;
}
