package io.shinmen.airnewsaggregator.payload.response;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.Builder;
import lombok.Getter;
import java.util.List;

@Getter
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UserNewsArticleResponse {
    private UserResponse user;
    private List<NewsArticlesResponse> newsArticles;
    private int total;
}
