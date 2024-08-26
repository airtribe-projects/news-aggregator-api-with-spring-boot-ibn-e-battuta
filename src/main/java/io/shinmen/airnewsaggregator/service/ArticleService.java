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
import org.springframework.transaction.annotation.Transactional;

import io.shinmen.airnewsaggregator.exception.ArticleNotFoundException;
import io.shinmen.airnewsaggregator.exception.UserNotFoundException;
import io.shinmen.airnewsaggregator.model.Article;
import io.shinmen.airnewsaggregator.model.User;
import io.shinmen.airnewsaggregator.payload.response.ArticleResponse;
import io.shinmen.airnewsaggregator.payload.response.UserArticleResponse;
import io.shinmen.airnewsaggregator.payload.response.UserResponse;
import io.shinmen.airnewsaggregator.repository.ArticleRepository;
import io.shinmen.airnewsaggregator.repository.UserRepository;
import io.shinmen.airnewsaggregator.service.helper.ServiceHelper;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ArticleService {

    @Value("${air-news-aggregator.app.default-timezone:UTC}")
    private String defaultTimeZone;

    private final ArticleRepository articleRepository;
    private final UserRepository userRepository;

    private static final String READ = "read";
    private static final String FAVORITE = "favorite";

    @Transactional
    public void markArticleStatus(String username, String url, String title, String author,
            String source, String publishedAt, String action) {

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UserNotFoundException(
                        ServiceHelper.getEntityNotFoundMessage("User", "username", username)));

        Article article = articleRepository.findByUserAndUrl(user, url)
                .orElse(new Article());

        article.setUser(user);
        article.setTitle(title);
        article.setAuthor(author);
        article.setSource(source);
        article.setUrl(url);
        article.setPublishedAt(parsePublishedAt(publishedAt));

        if (action.equals(READ))
            article.setRead(true);

        if (action.equals(FAVORITE)) {
            article.setFavorite(true);
        }

        articleRepository.save(article);
    }

    @Transactional(readOnly = true)
    public UserArticleResponse getReadArticles(String username, int page, int size) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UserNotFoundException(
                        ServiceHelper.getEntityNotFoundMessage("User", "username", username)));

        Pageable pageable = PageRequest.of(page, size);

        Page<Article> readArticles = articleRepository.findByUserAndIsRead(user, true, pageable);

        return getUserArticleResponse(user, readArticles);
    }

    @Transactional(readOnly = true)
    public UserArticleResponse getFavoriteArticles(String username, int page, int size) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UserNotFoundException(
                        ServiceHelper.getEntityNotFoundMessage("User", "username", username)));

        Pageable pageable = PageRequest.of(page, size);

        Page<Article> favoriteArticles = articleRepository.findByUserAndIsFavorite(user, true, pageable);

        return getUserArticleResponse(user, favoriteArticles);
    }

    private UserArticleResponse getUserArticleResponse(User user, Page<Article> newsArticles) {
        List<ArticleResponse> articleResponses = newsArticles.stream()
                .map(article -> ArticleResponse.builder()
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

        return UserArticleResponse.builder()
                .articles(articleResponses)
                .user(userResponse)
                .total(articleResponses == null ? 0 : articleResponses.size())
                .build();
    }

    @Transactional
    public void unMarkArticleStatus(String username, Long id, String action) {

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UserNotFoundException(
                        ServiceHelper.getEntityNotFoundMessage("User", "username", username)));

        Article article = articleRepository.findByUserAndId(user, id)
                .orElseThrow(() -> new ArticleNotFoundException(
                        ServiceHelper.getEntityNotFoundMessage("Article", "id", String.valueOf(id))));

        if (action.equals(READ) && !article.isFavorite()) {
            articleRepository.delete(article);
            return;
        }

        if (action.equals(FAVORITE) && !article.isRead()) {
            articleRepository.delete(article);
            return;
        }

        if (action.equals(FAVORITE)) {
            article.setFavorite(false);
            articleRepository.save(article);
            return;
        }

        if (action.equals(READ)) {
            article.setRead(false);
            articleRepository.save(article);
        }
    }

    private ZonedDateTime parsePublishedAt(String publishedAt) {
        try {
            DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern(ServiceHelper.FORMAT_DATE);
            LocalDate localDate = LocalDate.parse(publishedAt, dateFormatter);
            return localDate.atStartOfDay(ZoneId.of(defaultTimeZone));
        } catch (DateTimeParseException e) {
            try {
                DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern(ServiceHelper.FORMAT_DATETIME);
                return ZonedDateTime.parse(publishedAt, dateTimeFormatter.withZone(ZoneId.of(defaultTimeZone)));
            } catch (DateTimeParseException ex) {
                throw new IllegalArgumentException(
                        "Invalid 'publishedAt' format. Please use 'yyyy-MM-dd' or 'yyyy-MM-ddTHH:mm:ss'");
            }
        }
    }
}
