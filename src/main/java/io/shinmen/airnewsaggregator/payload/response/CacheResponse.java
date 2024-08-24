package io.shinmen.airnewsaggregator.payload.response;

import java.util.Map;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CacheResponse {
    private String cacheName;
    private long size;
    private Map<?, ?> data;
}
