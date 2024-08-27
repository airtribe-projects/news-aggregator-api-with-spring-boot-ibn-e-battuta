package io.shinmen.airnewsaggregator.payload.response.newsapi;

import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class NewsApiSourceResponse {
    private String status;
    private List<NewsApiSource> sources;
    private String code;
    private String message;
}
