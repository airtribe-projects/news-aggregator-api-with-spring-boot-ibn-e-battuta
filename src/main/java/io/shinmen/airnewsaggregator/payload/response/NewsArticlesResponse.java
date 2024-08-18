package io.shinmen.airnewsaggregator.payload.response;

import java.time.ZonedDateTime;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class NewsArticlesResponse {
    private String title;
    private String url;
    private String author;
    private ZonedDateTime publishedAt;
    private String source;
}
