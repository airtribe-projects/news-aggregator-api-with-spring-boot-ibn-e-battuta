package io.shinmen.airnewsaggregator.payload.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UserNewsArticleResponse {
    private UserResponse user;
    private List<NewsArticlesResponse> articles;
    private int total;

    public UserNewsArticleResponse(UserResponse user, List<NewsArticlesResponse> articles) {
        this.user = user;
        this.articles = articles;
        this.total = articles.size();
    }
}
