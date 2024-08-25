package io.shinmen.airnewsaggregator.service;

import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
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

    @Value("${air-news-aggregator.app.default-timezone:UTC}")
    private String defaultTimeZone;

    private static final String FORMAT_DATE = "yyyy-MM-dd";
    private static final String FORMAT_DATETIME = "yyyy-MM-dd'T'HH:mm:ss";

    private final NewsArticleRepository newsArticleRepository;
    private final UserRepository userRepository;

    private static final String READ = "read";
    private static final String FAVORITE = "favorite";

    public void markArticleStatus(String username, String url, String title, String author,
            String source, String publishedAt, String action) {

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UserNotFoundException(getUserNotFoundMessage(username)));

        NewsArticle newsArticle = newsArticleRepository.findByUserAndUrl(user, url)
                .orElse(new NewsArticle());

        newsArticle.setUser(user);
        newsArticle.setTitle(title);
        newsArticle.setAuthor(author);
        newsArticle.setSource(source);
        newsArticle.setUrl(url);
        newsArticle.setPublishedAt(parsePublishedAt(publishedAt));

        if (action.equals(READ))
            newsArticle.setRead(true);

        if (action.equals(FAVORITE)) {
            newsArticle.setFavorite(true);
        }

        newsArticleRepository.save(newsArticle);
    }

    public UserNewsArticleResponse getReadArticles(String username, int page, int size) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException(getUserNotFoundMessage(username)));

        Pageable pageable = PageRequest.of(page, size);

        Page<NewsArticle> readArticles = newsArticleRepository.findByUserAndIsRead(user, true, pageable);

        return getUserResponse(user, readArticles);
    }

    public UserNewsArticleResponse getFavoriteArticles(String username, int page, int size) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException(getUserNotFoundMessage(username)));

        Pageable pageable = PageRequest.of(page, size);
        
        Page<NewsArticle> favoriteArticles = newsArticleRepository.findByUserAndIsFavorite(user, true, pageable);

        return getUserResponse(user, favoriteArticles);
    }

    private UserNewsArticleResponse getUserResponse(User user, Page<NewsArticle> newsArticles) {
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
                .total(articleResponses == null ? 0 : articleResponses.size())
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

    private ZonedDateTime parsePublishedAt(String publishedAt) {
        try {
            DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern(FORMAT_DATE);
            LocalDate localDate = LocalDate.parse(publishedAt, dateFormatter);
            return localDate.atStartOfDay(ZoneId.of(defaultTimeZone));
        } catch (DateTimeParseException e) {
            try {
                DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern(FORMAT_DATETIME);
                return ZonedDateTime.parse(publishedAt, dateTimeFormatter.withZone(ZoneId.of(defaultTimeZone)));
            } catch (DateTimeParseException ex) {
                throw new IllegalArgumentException(
                        "Invalid 'publishedAt' format. Please use 'yyyy-MM-dd' or 'yyyy-MM-ddTHH:mm:ss'.");
            }
        }
    }
}
