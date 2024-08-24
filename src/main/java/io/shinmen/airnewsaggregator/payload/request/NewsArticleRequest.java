package io.shinmen.airnewsaggregator.payload.request;

import java.time.ZonedDateTime;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class NewsArticleRequest {
    @NotBlank
    private String url;
    @NotBlank
    private String title;
    @NotBlank
    private String source;
    @NotBlank
    private ZonedDateTime publishedAt;
    @NotBlank
    private String author;
}
