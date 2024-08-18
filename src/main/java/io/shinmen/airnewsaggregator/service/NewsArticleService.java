package io.shinmen.airnewsaggregator.service;

import java.util.Date;
import java.util.List;

import org.modelmapper.ModelMapper;
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
    private final ModelMapper modelMapper;

    public void markArticleStatus(String username, String articleUrl, String articleTitle, String articleAuthor,
            String articleSource,
            Date articlePublishedDate, String action) {

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UserNotFoundException("User with username: " + username + " not found"));

        NewsArticle newsArticle = newsArticleRepository.findByUserAndArticleUrl(user, articleUrl)
                .orElse(new NewsArticle());

        newsArticle.setUser(user);
        newsArticle.setArticleTitle(articleTitle);
        newsArticle.setArticleAuthor(articleAuthor);
        newsArticle.setArticleSource(articleSource);
        newsArticle.setArticleUrl(articleUrl);
        newsArticle.setArticlePublishedDate(articlePublishedDate);
        if (action.equals("read"))
            newsArticle.setRead(true);

        if (action.equals("favorite")) {
            newsArticle.setFavorite(true);
        }

        newsArticleRepository.save(newsArticle);
    }

    public UserNewsArticleResponse getReadArticles(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User with username: " + username + " not found"));

        List<NewsArticle> readArticles = newsArticleRepository.findByUserAndIsRead(user, true);

        List<NewsArticlesResponse> articleResponses = readArticles.stream()
                .map(article -> modelMapper.map(article, NewsArticlesResponse.class))
                .toList();
        UserResponse userResponse = modelMapper.map(user, UserResponse.class);

        return new UserNewsArticleResponse(userResponse, articleResponses);
    }

    public UserNewsArticleResponse getFavoriteArticles(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User with username: " + username + " not found"));

        List<NewsArticle> favoriteArticles = newsArticleRepository.findByUserAndIsFavorite(user, true);

        List<NewsArticlesResponse> articleResponses = favoriteArticles.stream()
                .map(article -> modelMapper.map(article, NewsArticlesResponse.class))
                .toList();
        UserResponse userResponse = modelMapper.map(user, UserResponse.class);

        return new UserNewsArticleResponse(userResponse, articleResponses);
    }

    public void unMarkArticleStatus(String username, Long articleId, String action) {

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UserNotFoundException("User with username: " + username + " not found"));

        NewsArticle newsArticle = newsArticleRepository.findByUserAndId(user, articleId)
                .orElseThrow(() -> new NewsArticleNotFoundException("Article with id: " + articleId + " not found"));

        if (action.equals("read") && !newsArticle.isFavorite()) {
            newsArticleRepository.delete(newsArticle);
            return;
        }

        if (action.equals("favorite") && !newsArticle.isRead()) {
            newsArticleRepository.delete(newsArticle);
            return;
        }

        if (action.equals("favorite")) {
            newsArticle.setFavorite(false);
            newsArticleRepository.save(newsArticle);
            return;
        }

        if (action.equals("read")) {
            newsArticle.setRead(false);
            newsArticleRepository.save(newsArticle);
        }
    }
}
