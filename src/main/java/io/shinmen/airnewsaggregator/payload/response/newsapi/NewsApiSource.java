package io.shinmen.airnewsaggregator.payload.response.newsapi;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class NewsApiSource {
    private String id;
    private String name;
    private String description;
    private String url;
    private String category;
    private String language;
    private String country;
}
