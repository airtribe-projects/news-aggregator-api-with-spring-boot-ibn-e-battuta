package io.shinmen.airnewsaggregator.payload.response;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.Getter;

@Getter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class NewsResponse {
    private final int total;
    private final List<NewsArticlesResponse> articles;

    public NewsResponse(List<NewsArticlesResponse> articles) {
        this.articles = articles;
        this.total = articles.size();
    }
}
