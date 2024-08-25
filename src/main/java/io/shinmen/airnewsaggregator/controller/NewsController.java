package io.shinmen.airnewsaggregator.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.core.JsonProcessingException;

import io.shinmen.airnewsaggregator.payload.request.EverythingQueryRequest;
import io.shinmen.airnewsaggregator.payload.request.NewsArticleRequest;
import io.shinmen.airnewsaggregator.payload.request.TopHeadLinesQueryRequest;
import io.shinmen.airnewsaggregator.payload.response.MessageResponse;
import io.shinmen.airnewsaggregator.payload.response.NewsResponse;
import io.shinmen.airnewsaggregator.payload.response.UserNewsArticleResponse;
import io.shinmen.airnewsaggregator.security.UserDetailsImpl;
import io.shinmen.airnewsaggregator.service.NewsArticleService;
import io.shinmen.airnewsaggregator.service.NewsService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/news")
@RequiredArgsConstructor
@Validated
public class NewsController {

    private final NewsArticleService newsArticleService;
    private final NewsService newsService;

    @GetMapping("/top-headlines")
    public ResponseEntity<NewsResponse> getNews(
            @Valid @ModelAttribute TopHeadLinesQueryRequest topHeadlinesQueryRequest,
            @AuthenticationPrincipal UserDetailsImpl userDetails) throws JsonProcessingException {
        NewsResponse newsResponse = newsService
                .getTopHeadLines(topHeadlinesQueryRequest, userDetails.getUsername());
        return ResponseEntity.ok(newsResponse);
    }

    @GetMapping("/search/{keyword}")
    public ResponseEntity<NewsResponse> searchNews(@PathVariable String keyword,
            @RequestParam(defaultValue = "1") @Min(1) int page,
            @RequestParam(defaultValue = "20") @Min(1) @Max(100) int pageSize) throws JsonProcessingException {
        NewsResponse newsResponse = newsService.searchNews(keyword, page, pageSize);
        return ResponseEntity.ok(newsResponse);
    }

    @GetMapping("/everything")
    public ResponseEntity<NewsResponse> getEverythingNews(
            @Valid @ModelAttribute EverythingQueryRequest everythingQueryRequest,
            @AuthenticationPrincipal UserDetailsImpl userDetails) throws JsonProcessingException {
        NewsResponse newsResponse = newsService.getEverything(everythingQueryRequest,
                userDetails.getUsername());
        return ResponseEntity.ok(newsResponse);
    }

    @PostMapping("/read")
    public ResponseEntity<MessageResponse> markAsRead(@AuthenticationPrincipal UserDetailsImpl userDetails,
            @Valid @RequestBody NewsArticleRequest request) {
        newsArticleService.markArticleStatus(userDetails.getUsername(), request.getUrl(), request.getTitle(),
                request.getAuthor(), request.getSource(),
                request.getPublishedAt(), "read");
        return ResponseEntity.ok(MessageResponse.builder().message("Article marked as read").build());
    }

    @PostMapping("/favorite")
    public ResponseEntity<MessageResponse> markAsFavorite(@AuthenticationPrincipal UserDetailsImpl userDetails,
            @Valid @RequestBody NewsArticleRequest request) {
        newsArticleService.markArticleStatus(userDetails.getUsername(), request.getUrl(), request.getTitle(),
                request.getAuthor(), request.getSource(),
                request.getPublishedAt(), "favorite");
        return ResponseEntity.ok(MessageResponse.builder().message("Article marked as favorite").build());
    }

    @PostMapping("/unread/{articleId}")
    public ResponseEntity<MessageResponse> markAsUnRead(@AuthenticationPrincipal UserDetailsImpl userDetails,
            @PathVariable Long articleId) {
        newsArticleService.unMarkArticleStatus(userDetails.getUsername(), articleId, "read");
        return ResponseEntity.ok(MessageResponse.builder().message("Article marked as unread").build());
    }

    @PostMapping("/unfavorite/{articleId}")
    public ResponseEntity<MessageResponse> markAsUnFavorite(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @PathVariable Long articleId) {
        newsArticleService.unMarkArticleStatus(userDetails.getUsername(), articleId, "favorite");
        return ResponseEntity.ok(MessageResponse.builder().message("Article marked as unfavorite").build());
    }

    @GetMapping("/read")
    public ResponseEntity<UserNewsArticleResponse> getReadArticles(@AuthenticationPrincipal UserDetailsImpl userDetails,
            @RequestParam(defaultValue = "1") @Min(value = 1, message = "Page must be at least 1") int page,
            @RequestParam(defaultValue = "20") @Min(value = 1, message = "PageSize must be at least 1") @Max(value = 100, message = "PageSize must not exceed 100") int pageSize) {

        UserNewsArticleResponse readArticles = newsArticleService.getReadArticles(userDetails.getUsername(), page - 1,
                pageSize);
        return ResponseEntity.ok(readArticles);
    }

    @GetMapping("/favorites")
    public ResponseEntity<UserNewsArticleResponse> getFavoriteArticles(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @RequestParam(defaultValue = "1") @Min(value = 1, message = "Page must be at least 1") int page,
            @RequestParam(defaultValue = "20") @Min(value = 1, message = "PageSize must be at least 1") @Max(value = 100, message = "PageSize must not exceed 100") int pageSize) {

        UserNewsArticleResponse favoriteArticles = newsArticleService.getFavoriteArticles(userDetails.getUsername(),
                page - 1, pageSize);
        return ResponseEntity.ok(favoriteArticles);
    }
}
