package io.shinmen.airnewsaggregator.payload.response;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class NewsResponse {
    private int total;
    private List<NewsArticlesResponse> articles;
}
