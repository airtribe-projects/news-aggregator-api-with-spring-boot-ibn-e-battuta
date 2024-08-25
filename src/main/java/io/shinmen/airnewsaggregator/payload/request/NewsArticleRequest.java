package io.shinmen.airnewsaggregator.payload.request;

import org.hibernate.validator.constraints.URL;

import io.shinmen.airnewsaggregator.payload.request.validator.ValidZonedDateTimeFormat;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class NewsArticleRequest {

    @NotBlank
    @URL(message = "Invalid URL format")
    private String url;

    @NotBlank
    private String title;

    @NotBlank
    private String source;

    @NotNull
    @ValidZonedDateTimeFormat(message = "Invalid 'publishedAt' date format. Use 'yyyy-MM-dd' or 'yyyy-MM-ddTHH:mm:ss'")
    private String publishedAt;

    @NotBlank
    private String author;
}
