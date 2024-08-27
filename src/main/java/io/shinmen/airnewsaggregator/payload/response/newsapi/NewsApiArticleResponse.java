package io.shinmen.airnewsaggregator.payload.response.newsapi;

import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class NewsApiArticleResponse {
    private String status;
    private int totalResults;
    private List<NewsApiArticle> articles;
    private String code;
    private String message;
}
