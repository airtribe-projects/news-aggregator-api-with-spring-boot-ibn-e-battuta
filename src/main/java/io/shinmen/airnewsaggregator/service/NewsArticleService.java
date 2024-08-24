package io.shinmen.airnewsaggregator.service;

import java.time.ZonedDateTime;
import java.util.List;

import org.springframework.stereotype.Service;

import io.shinmen.airnewsaggregator.exception.NewsArticleNotFoundException;
import io.shinmen.airnewsaggregator.exception.UserNotFoundException;
import io.shinmen.airnewsaggregator.model.NewsArticle;
import io.shinmen.airnewsaggregator.model.User;
import io.shinmen.airnewsaggregator.payload.response.NewsArticlesResponse;
import io.shinmen.airnewsaggregator.payload.response.UserNewsArticleResponse;
import io.shinmen.airnewsaggregator.payload.response.UserResponse;
import io.shinmen.airnewsaggregator.repository.NewsArticleRepository;
import io.shinmen.airnewsaggregator.repository.UserRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class NewsArticleService {

    private final NewsArticleRepository newsArticleRepository;
    private final UserRepository userRepository;

    private static final String READ = "read";
    private static final String FAVORITE = "favorite";

    public void markArticleStatus(String username, String url, String title, String author,
            String source, ZonedDateTime publishedAt, String action) {

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UserNotFoundException(getUserNotFoundMessage(username)));

        NewsArticle newsArticle = newsArticleRepository.findByUserAndUrl(user, url)
                .orElse(new NewsArticle());

        newsArticle.setUser(user);
        newsArticle.setTitle(title);
        newsArticle.setAuthor(author);
        newsArticle.setSource(source);
        newsArticle.setUrl(url);
        newsArticle.setPublishedAt(publishedAt);

        if (action.equals(READ))
            newsArticle.setRead(true);

        if (action.equals(FAVORITE)) {
            newsArticle.setFavorite(true);
        }

        newsArticleRepository.save(newsArticle);
    }

    public UserNewsArticleResponse getReadArticles(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException(getUserNotFoundMessage(username)));

        List<NewsArticle> readArticles = newsArticleRepository.findByUserAndIsRead(user, true);

        return getUserResponse(user, readArticles);
    }

    public UserNewsArticleResponse getFavoriteArticles(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException(getUserNotFoundMessage(username)));

        List<NewsArticle> favoriteArticles = newsArticleRepository.findByUserAndIsFavorite(user, true);

        return getUserResponse(user, favoriteArticles);
    }

    private UserNewsArticleResponse getUserResponse(User user, List<NewsArticle> newsArticles) {
        List<NewsArticlesResponse> articleResponses = newsArticles.stream()
                .map(article -> NewsArticlesResponse.builder()
                        .title(article.getTitle())
                        .author(article.getAuthor())
                        .source(article.getSource())
                        .url(article.getUrl())
                        .publishedAt(article.getPublishedAt())
                        .build())
                .toList();

        UserResponse userResponse = UserResponse.builder()
                .email(user.getEmail())
                .userName(user.getUsername())
                .build();

        return UserNewsArticleResponse.builder()
                .newsArticles(articleResponses)
                .user(userResponse)
                .build();
    }

    public void unMarkArticleStatus(String username, Long articleId, String action) {

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UserNotFoundException(getUserNotFoundMessage(username)));

        NewsArticle newsArticle = newsArticleRepository.findByUserAndId(user, articleId)
                .orElseThrow(() -> new NewsArticleNotFoundException("Article with id: " + articleId + " not found"));

        if (action.equals(READ) && !newsArticle.isFavorite()) {
            newsArticleRepository.delete(newsArticle);
            return;
        }

        if (action.equals(FAVORITE) && !newsArticle.isRead()) {
            newsArticleRepository.delete(newsArticle);
            return;
        }

        if (action.equals(FAVORITE)) {
            newsArticle.setFavorite(false);
            newsArticleRepository.save(newsArticle);
            return;
        }

        if (action.equals(READ)) {
            newsArticle.setRead(false);
            newsArticleRepository.save(newsArticle);
        }
    }

    private String getUserNotFoundMessage(String username) {
        return "User with username: " + username + " not found";
    }
}
