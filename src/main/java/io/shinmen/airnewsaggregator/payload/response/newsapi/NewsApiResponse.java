package io.shinmen.airnewsaggregator.payload.response.newsapi;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class NewsApiResponse {
    private String status;
    private int totalResults;
    private List<NewsApiArticleResponse> articles;
}
