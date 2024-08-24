package io.shinmen.airnewsaggregator.payload.response;

import java.util.Map;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CacheResponse {
    private String cacheName;
    private long size;
    private Map<?, ?> data;
}
