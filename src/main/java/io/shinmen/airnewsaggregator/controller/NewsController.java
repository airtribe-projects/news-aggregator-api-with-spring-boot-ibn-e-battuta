package io.shinmen.airnewsaggregator.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
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
import io.shinmen.airnewsaggregator.payload.request.EverythingSearchRequest;
import io.shinmen.airnewsaggregator.payload.request.NewsArticleRequest;
import io.shinmen.airnewsaggregator.payload.request.TopHeadLinesQueryRequest;
import io.shinmen.airnewsaggregator.payload.request.TopHeadLinesSearchRequest;
import io.shinmen.airnewsaggregator.payload.response.MessageResponse;
import io.shinmen.airnewsaggregator.payload.response.NewsResponse;
import io.shinmen.airnewsaggregator.payload.response.UserNewsArticleResponse;
import io.shinmen.airnewsaggregator.security.UserDetailsImpl;
import io.shinmen.airnewsaggregator.service.NewsApiService;
import io.shinmen.airnewsaggregator.service.NewsArticleService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/news")
@RequiredArgsConstructor
public class NewsController {

    private final NewsApiService newsApiService;
    private final NewsArticleService newsArticleService;

    @GetMapping("/top-headlines")
    public ResponseEntity<NewsResponse> getNews(
            @Valid @ModelAttribute TopHeadLinesQueryRequest topHeadlinesQueryRequest,
            @AuthenticationPrincipal UserDetailsImpl userDetails) throws JsonProcessingException {
        TopHeadLinesSearchRequest topHeadLinesSearchRequest = newsApiService
                .getTopHeadLinesRequest(topHeadlinesQueryRequest, userDetails.getUsername());
        NewsResponse newsResponse = newsApiService.getTopHeadlines(topHeadLinesSearchRequest);
        return ResponseEntity.ok(newsResponse);
    }

    @GetMapping("/search/{keyword}")
    public ResponseEntity<NewsResponse> searchNews(@PathVariable String keyword,
            @RequestParam(defaultValue = "1") @Min(1) int page,
            @RequestParam(defaultValue = "20") @Min(1) @Max(100) int pageSize) throws JsonProcessingException {
        NewsResponse newsResponse = newsApiService.search(keyword, page, pageSize);
        return ResponseEntity.ok(newsResponse);
    }

    @GetMapping("/everything")
    public ResponseEntity<NewsResponse> getEverythingNews(
            @Valid @ModelAttribute EverythingQueryRequest everythingQueryRequest,
            @AuthenticationPrincipal UserDetailsImpl userDetails) throws JsonProcessingException {
        EverythingSearchRequest everythingSearchRequest = newsApiService.getEverythingRequest(everythingQueryRequest,
                userDetails.getUsername());
        NewsResponse newsResponse = newsApiService.getEverything(everythingSearchRequest);
        return ResponseEntity.ok(newsResponse);
    }

    @PostMapping("/read")
    public ResponseEntity<MessageResponse> markAsRead(@AuthenticationPrincipal UserDetailsImpl userDetails,
            @RequestBody NewsArticleRequest request) {
        newsArticleService.markArticleStatus(userDetails.getUsername(), request.getUrl(), request.getTitle(),
                request.getAuthor(), request.getSource(),
                request.getPublishedAt(), "read");
        return ResponseEntity.ok(new MessageResponse("Article mark as read"));
    }

    @PostMapping("/favorite")
    public ResponseEntity<MessageResponse> markAsFavorite(@AuthenticationPrincipal UserDetailsImpl userDetails,
            @RequestBody NewsArticleRequest request) {
        newsArticleService.markArticleStatus(userDetails.getUsername(), request.getUrl(), request.getTitle(),
                request.getAuthor(), request.getSource(),
                request.getPublishedAt(), "favorite");
        return ResponseEntity.ok(new MessageResponse("Article marked as favorite"));
    }

    @PostMapping("/un-read/{articleId}")
    public ResponseEntity<MessageResponse> markAsUnRead(@AuthenticationPrincipal UserDetailsImpl userDetails,
            @PathVariable("articleId") Long articleId) {
        newsArticleService.unMarkArticleStatus(userDetails.getUsername(), articleId, "read");
        return ResponseEntity.ok(new MessageResponse("Article mark as un-read"));
    }

    @PostMapping("/un-favorite/{articleId}")
    public ResponseEntity<MessageResponse> markAsUnFavorite(@AuthenticationPrincipal UserDetailsImpl userDetails,
            @PathVariable("articleId") Long articleId) {
        newsArticleService.unMarkArticleStatus(userDetails.getUsername(), articleId, "favorite");
        return ResponseEntity.ok(new MessageResponse("Article marked as un-favorite"));
    }

    @GetMapping("/read")
    public ResponseEntity<UserNewsArticleResponse> getReadArticles(
            @AuthenticationPrincipal UserDetailsImpl userDetails) {
        UserNewsArticleResponse readArticles = newsArticleService.getReadArticles(userDetails.getUsername());
        return ResponseEntity.ok(readArticles);
    }

    @GetMapping("/favorites")
    public ResponseEntity<UserNewsArticleResponse> getFavoriteArticles(
            @AuthenticationPrincipal UserDetailsImpl userDetails) {
        UserNewsArticleResponse favoriteArticles = newsArticleService.getFavoriteArticles(userDetails.getUsername());
        return ResponseEntity.ok(favoriteArticles);
    }
}
