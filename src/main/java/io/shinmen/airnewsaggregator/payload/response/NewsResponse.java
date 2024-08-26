package io.shinmen.airnewsaggregator.payload.response;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class NewsResponse {
    private int total;

    @JsonProperty
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private List<ArticleResponse> articles;
}
