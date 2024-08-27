package io.shinmen.airnewsaggregator.controller;

import static io.shinmen.airnewsaggregator.utility.Constants.FAVORITE;
import static io.shinmen.airnewsaggregator.utility.Constants.API_NEWS;
import static io.shinmen.airnewsaggregator.utility.Constants.API_NEWS_EVERYTHING;
import static io.shinmen.airnewsaggregator.utility.Constants.API_NEWS_FAVORITE;
import static io.shinmen.airnewsaggregator.utility.Constants.API_NEWS_READ;
import static io.shinmen.airnewsaggregator.utility.Constants.API_NEWS_SEARCH_KEYWORD;
import static io.shinmen.airnewsaggregator.utility.Constants.API_NEWS_TOP_HEADLINES;
import static io.shinmen.airnewsaggregator.utility.Constants.API_NEWS_UNFAVORITE_ARTICLE_ID;
import static io.shinmen.airnewsaggregator.utility.Constants.API_NEWS_UNREAD_ARTICLE_ID;
import static io.shinmen.airnewsaggregator.utility.Constants.PAGE_DEFAULT;
import static io.shinmen.airnewsaggregator.utility.Constants.PAGE_MIN;
import static io.shinmen.airnewsaggregator.utility.Constants.PAGE_SIZE_DEFAULT;
import static io.shinmen.airnewsaggregator.utility.Constants.PAGE_SIZE_MAX;
import static io.shinmen.airnewsaggregator.utility.Constants.PAGE_SIZE_MIN;
import static io.shinmen.airnewsaggregator.utility.Constants.READ;
import static io.shinmen.airnewsaggregator.utility.Messages.ARTICLE_FAVORITE_MESSAGE;
import static io.shinmen.airnewsaggregator.utility.Messages.ARTICLE_READ_MESSAGE;
import static io.shinmen.airnewsaggregator.utility.Messages.ARTICLE_UNFAVORITE_MESSAGE;
import static io.shinmen.airnewsaggregator.utility.Messages.ARTICLE_UNREAD_MESSAGE;
import static io.shinmen.airnewsaggregator.utility.Messages.PAGE_MIN_MESSAGE;
import static io.shinmen.airnewsaggregator.utility.Messages.PAGE_SIZE_MAX_MESSAGE;
import static io.shinmen.airnewsaggregator.utility.Messages.PAGE_SIZE_MIN_MESSAGE;

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

import io.shinmen.airnewsaggregator.payload.request.ArticleRequest;
import io.shinmen.airnewsaggregator.payload.request.EverythingQueryRequest;
import io.shinmen.airnewsaggregator.payload.request.TopHeadLinesQueryRequest;
import io.shinmen.airnewsaggregator.payload.response.MessageResponse;
import io.shinmen.airnewsaggregator.payload.response.NewsResponse;
import io.shinmen.airnewsaggregator.payload.response.UserArticleResponse;
import io.shinmen.airnewsaggregator.security.UserDetailsImpl;
import io.shinmen.airnewsaggregator.service.ArticleService;
import io.shinmen.airnewsaggregator.service.NewsService;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Validated
@RestController
@RequestMapping(API_NEWS)
@RequiredArgsConstructor
public class NewsController {

        private final NewsService newsService;
        private final ArticleService newsArticleService;

        @GetMapping(API_NEWS_TOP_HEADLINES)
        public ResponseEntity<NewsResponse> getNews(@Valid @ModelAttribute final TopHeadLinesQueryRequest request,
                        @AuthenticationPrincipal final UserDetailsImpl userDetails) {

                log.info("Received request to get headlines for user: {}", userDetails.getUsername());

                final NewsResponse response = newsService.getTopHeadLines(request, userDetails.getUsername());

                return ResponseEntity.ok(response);
        }

        @GetMapping(API_NEWS_SEARCH_KEYWORD)
        public ResponseEntity<NewsResponse> searchNews(@PathVariable final String keyword,
                        @RequestParam(defaultValue = PAGE_DEFAULT) @Min(value = PAGE_MIN, message = PAGE_MIN_MESSAGE) final int page,
                        @RequestParam(defaultValue = PAGE_SIZE_DEFAULT) @Min(value = PAGE_SIZE_MIN, message = PAGE_SIZE_MIN_MESSAGE) @Max(value = PAGE_SIZE_MAX, message = PAGE_SIZE_MAX_MESSAGE) final int pageSize) {

                log.info("Received request to search for keyword: {}", keyword);

                final NewsResponse response = newsService.searchNews(keyword, page, pageSize);

                return ResponseEntity.ok(response);
        }

        @GetMapping(API_NEWS_EVERYTHING)
        public ResponseEntity<NewsResponse> getEverythingNews(
                        @Valid @ModelAttribute final EverythingQueryRequest request,
                        @AuthenticationPrincipal final UserDetailsImpl userDetails) throws JsonProcessingException {

                log.info("Received request to get everything for user: {}", userDetails.getUsername());

                final NewsResponse newsResponse = newsService.getEverything(request, userDetails.getUsername());

                return ResponseEntity.ok(newsResponse);
        }

        @PostMapping(API_NEWS_READ)
        public ResponseEntity<MessageResponse> markAsRead(@AuthenticationPrincipal final UserDetailsImpl userDetails,
                        @Valid @RequestBody final ArticleRequest request) {

                log.info("Received request to mark article as read for user: {}", userDetails.getUsername());

                newsArticleService.markArticleStatus(userDetails.getUsername(), request.getUrl(), request.getTitle(),
                                request.getAuthor(), request.getSource(),
                                request.getPublishedAt(), READ);

                return ResponseEntity.ok(MessageResponse.builder()
                                .message(ARTICLE_READ_MESSAGE)
                                .build());
        }

        @PostMapping(API_NEWS_FAVORITE)
        public ResponseEntity<MessageResponse> markAsFavorite(
                        @AuthenticationPrincipal final UserDetailsImpl userDetails,
                        @Valid @RequestBody final ArticleRequest request) {

                log.info("Received request to mark article as favorite for user: {}", userDetails.getUsername());

                newsArticleService.markArticleStatus(userDetails.getUsername(), request.getUrl(), request.getTitle(),
                                request.getAuthor(), request.getSource(),
                                request.getPublishedAt(), FAVORITE);

                return ResponseEntity.ok(MessageResponse.builder()
                                .message(ARTICLE_FAVORITE_MESSAGE)
                                .build());
        }

        @PostMapping(API_NEWS_UNREAD_ARTICLE_ID)
        public ResponseEntity<MessageResponse> markAsUnRead(@AuthenticationPrincipal final UserDetailsImpl userDetails,
                        @PathVariable final long articleId) {

                log.info("Received request to mark article as unread for user: {}", userDetails.getUsername());

                newsArticleService.unMarkArticleStatus(userDetails.getUsername(), articleId, READ);

                return ResponseEntity.ok(MessageResponse.builder()
                                .message(ARTICLE_UNREAD_MESSAGE)
                                .build());
        }

        @PostMapping(API_NEWS_UNFAVORITE_ARTICLE_ID)
        public ResponseEntity<MessageResponse> markAsUnFavorite(
                        @AuthenticationPrincipal final UserDetailsImpl userDetails,
                        @PathVariable final long articleId) {

                log.info("Received request to mark article as unfavorite for user: {}", userDetails.getUsername());

                newsArticleService.unMarkArticleStatus(userDetails.getUsername(), articleId, FAVORITE);

                return ResponseEntity.ok(MessageResponse.builder()
                                .message(ARTICLE_UNFAVORITE_MESSAGE)
                                .build());
        }

        @GetMapping(API_NEWS_READ)
        public ResponseEntity<UserArticleResponse> getReadArticles(
                        @AuthenticationPrincipal final UserDetailsImpl userDetails,
                        @RequestParam(defaultValue = PAGE_DEFAULT) @Min(value = PAGE_MIN, message = PAGE_MIN_MESSAGE) final int page,
                        @RequestParam(defaultValue = PAGE_SIZE_DEFAULT) @Min(value = PAGE_SIZE_MIN, message = PAGE_SIZE_MIN_MESSAGE) @Max(value = PAGE_SIZE_MAX, message = PAGE_SIZE_MAX_MESSAGE) final int pageSize) {

                log.info("Received request to get read articles for user: {}", userDetails.getUsername());

                final UserArticleResponse response = newsArticleService.getReadArticles(userDetails.getUsername(),
                                page - 1,
                                pageSize);

                return ResponseEntity.ok(response);
        }

        @GetMapping(API_NEWS_FAVORITE)
        public ResponseEntity<UserArticleResponse> getFavoriteArticles(
                        @AuthenticationPrincipal final UserDetailsImpl userDetails,
                        @RequestParam(defaultValue = PAGE_DEFAULT) @Min(value = PAGE_MIN, message = PAGE_MIN_MESSAGE) final int page,
                        @RequestParam(defaultValue = PAGE_SIZE_DEFAULT) @Min(value = PAGE_SIZE_MIN, message = PAGE_SIZE_MIN_MESSAGE) @Max(value = PAGE_SIZE_MAX, message = PAGE_SIZE_MAX_MESSAGE) final int pageSize) {

                log.info("Received request to get favorite articles for user: {}", userDetails.getUsername());

                final UserArticleResponse response = newsArticleService.getFavoriteArticles(userDetails.getUsername(),
                                page - 1,
                                pageSize);

                return ResponseEntity.ok(response);
        }
}
