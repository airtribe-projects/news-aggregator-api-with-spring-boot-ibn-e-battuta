package io.shinmen.airnewsaggregator.payload.request;

import org.hibernate.validator.constraints.URL;

import io.shinmen.airnewsaggregator.payload.request.validator.annotation.ValidZonedDateTime;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@RequiredArgsConstructor
public class ArticleRequest {

    @NotBlank
    @URL(message = "Invalid URL format")
    private final String url;

    @NotBlank
    private final String title;

    @NotBlank
    private final String source;

    @NotNull
    @ValidZonedDateTime(message = "Invalid 'publishedAt' date format. Use 'yyyy-MM-dd' or 'yyyy-MM-ddTHH:mm:ss'")
    private final String publishedAt;

    @NotBlank
    private final String author;
}
