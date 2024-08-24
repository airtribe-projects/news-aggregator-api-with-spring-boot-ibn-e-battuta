package io.shinmen.airnewsaggregator.payload.response.newsapi;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class NewsApiResponse {
    private String status;
    private int totalResults;
    private List<NewsApiArticleResponse> articles;
}
