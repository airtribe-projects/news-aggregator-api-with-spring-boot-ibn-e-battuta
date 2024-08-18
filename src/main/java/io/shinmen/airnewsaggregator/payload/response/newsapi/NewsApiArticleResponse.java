package io.shinmen.airnewsaggregator.payload.response.newsapi;

import java.time.ZonedDateTime;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class NewsApiArticleResponse {
    private String title;
    private String description;
    private String url;
    private String urlToImage;
    private String author;
    private ZonedDateTime publishedAt;
    private String content;
    private NewsApiSourceResponse source;
}
