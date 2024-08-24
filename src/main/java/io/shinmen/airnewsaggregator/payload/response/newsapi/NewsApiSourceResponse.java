package io.shinmen.airnewsaggregator.payload.response.newsapi;

import com.fasterxml.jackson.annotation.JsonInclude;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class NewsApiSourceResponse {
    @NotBlank(message = "Source ID cannot be blank")
    private String id;

    @NotBlank(message = "Source name is mandatory")
    private String name;
}
