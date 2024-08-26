package io.shinmen.airnewsaggregator.payload.response.newsapi;

import java.time.ZonedDateTime;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class NewsApiArticle {
    private NewsApiSource source;
    private String author;
    private String title;
    private String description;
    private String url;
    private String urlToImage;
    private ZonedDateTime publishedAt;
    private String content;
}
