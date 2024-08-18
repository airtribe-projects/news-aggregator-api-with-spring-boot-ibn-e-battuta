package io.shinmen.airnewsaggregator.payload.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

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
    private Date publishedAt;
    @NotBlank
    private String author;
}
