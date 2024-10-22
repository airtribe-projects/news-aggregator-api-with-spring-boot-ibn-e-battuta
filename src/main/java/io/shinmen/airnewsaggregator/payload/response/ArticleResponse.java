package io.shinmen.airnewsaggregator.payload.response;

import java.time.ZonedDateTime;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ArticleResponse {
    private String title;

    private String url;

    private String author;

    private ZonedDateTime publishedAt;

    private String source;
}
