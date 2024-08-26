package io.shinmen.airnewsaggregator.service;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.HttpStatus;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.shinmen.airnewsaggregator.exception.NewsApiException;
import io.shinmen.airnewsaggregator.model.Source;
import io.shinmen.airnewsaggregator.payload.request.EverythingSearchRequest;
import io.shinmen.airnewsaggregator.payload.request.TopHeadLinesSearchRequest;
import io.shinmen.airnewsaggregator.payload.response.ArticleResponse;
import io.shinmen.airnewsaggregator.payload.response.newsapi.NewsApiArticle;
import io.shinmen.airnewsaggregator.payload.response.newsapi.NewsApiArticleResponse;
import io.shinmen.airnewsaggregator.payload.response.newsapi.NewsApiSource;
import io.shinmen.airnewsaggregator.payload.response.newsapi.NewsApiSourceResponse;
import io.shinmen.airnewsaggregator.repository.SourceRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class NewsApiService {

    @Value("${news-api.apiKey}")
    private String apiKey;

    @Value("${news-api.url}")
    private String newsApiUrl;

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;
    private final SourceRepository sourceRepository;

    private static final String API_KEY = "apiKey";

    @Cacheable(value = "topHeadlines", key = "#request.toCacheKey")
    public List<ArticleResponse> getTopHeadlines(TopHeadLinesSearchRequest request) {
        String topHeadlinesUrl = buildUrl("top-headlines",
                "q", request.getQuery(),
                "country", request.getCountry(),
                "category", request.getCategory(),
                "sources", request.getSources(),
                "page", request.getPage(),
                "pageSize", request.getPageSize());

        log.info("Headlines url {}", topHeadlinesUrl);
        return getArticleResponses(topHeadlinesUrl);
    }

    @Cacheable(value = "search", key = "#query + '-' + #page + '-' + #pageSize")
    public List<ArticleResponse> search(String query, int page, int pageSize) {
        String searchUrl = buildUrl("everything",
                "q", query,
                "page", String.valueOf(page),
                "pageSize", String.valueOf(pageSize));

        log.info("search url: {}", searchUrl);
        return getArticleResponses(searchUrl);
    }

    @Cacheable(value = "everything", key = "#request.toCacheKey()")
    public List<ArticleResponse> getEverything(EverythingSearchRequest request) {
        String everythingUrl = buildUrl("everything",
                "q", request.getQuery(),
                "sources", request.getSources(),
                "domains", request.getDomains(),
                "from", request.getFrom(),
                "to", request.getTo(),
                "language", request.getLanguage(),
                "sortBy", request.getSortBy(),
                "page", request.getPage(),
                "pageSize", request.getPageSize());

        log.info("Everything url {}", everythingUrl);
        return getArticleResponses(everythingUrl);
    }

    @Transactional
    @Scheduled(fixedRate = 24 * 60 * 60 * 1000)
    public void fetchAndUpdateNewsSources() throws JsonProcessingException {
        String sourcesUrl = buildUrl("top-headlines/sources");
        log.info("Sources url {}", sourcesUrl);
        NewsApiSourceResponse newsApiSourceResponse = getNewsApiSourceResponse(sourcesUrl);
        saveSources(newsApiSourceResponse);
    }

    private void saveSources(NewsApiSourceResponse newsApiSourceResponse) {
        try {
            if (newsApiSourceResponse != null && "ok".equals(newsApiSourceResponse.getStatus())) {
                List<NewsApiSource> newsApiSources = newsApiSourceResponse.getSources();
                for (NewsApiSource newsApiSource : newsApiSources) {
                    Optional<Source> source = sourceRepository.findById(newsApiSource.getId());
                    if (source.isPresent()) {
                        Source existingSource = getSource(newsApiSource, source.get());
                        sourceRepository.save(existingSource);
                    } else {
                        Source newSource = Source.builder()
                                .id(newsApiSource.getId())
                                .name(newsApiSource.getName())
                                .description(newsApiSource.getDescription())
                                .url(newsApiSource.getUrl())
                                .category(newsApiSource.getCategory())
                                .language(newsApiSource.getLanguage())
                                .country(newsApiSource.getCountry())
                                .build();
                        sourceRepository.save(newSource);
                    }
                }
            }
        } catch (Exception e) {
            log.error("Error fetching and updating news sources: {}", e.getMessage(), e);
        }
    }

    private Source getSource(NewsApiSource newsApiSource, Source source) {
        source.setName(newsApiSource.getName());
        source.setDescription(newsApiSource.getDescription());
        source.setUrl(newsApiSource.getUrl());
        source.setCategory(newsApiSource.getCategory());
        source.setLanguage(newsApiSource.getLanguage());
        source.setCountry(newsApiSource.getCountry());
        return source;
    }

    private String buildUrl(String endpoint, String... queryParams) {
        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(newsApiUrl + "/" + endpoint);
        for (int i = 0; i < queryParams.length; i += 2) {
            addQueryParamIfPresent(builder, queryParams[i], queryParams[i + 1]);
        }
        builder.queryParam(API_KEY, apiKey);
        return builder.toUriString();
    }

    private void addQueryParamIfPresent(UriComponentsBuilder builder, String paramName, String paramValue) {
        Optional.ofNullable(paramValue).ifPresent(value -> builder.queryParam(paramName, value));
    }

    private List<ArticleResponse> getArticleResponses(String url) {
        try {
            String response = restTemplate.getForObject(url, String.class);
            NewsApiArticleResponse articleResponse = objectMapper.readValue(response, NewsApiArticleResponse.class);
            validateApiResponse(articleResponse);
            return articleResponse.getArticles().stream().map(this::convertToArticleResponse).toList();
        } catch (HttpClientErrorException ex) {
            handleHttpClientErrorException(ex);
        } catch (JsonProcessingException e) {
            log.error("Error processing JSON response: {}", e.getMessage(), e);
            throw new NewsApiException("JSON-PROCESSING-ERROR", "Failed to process JSON response from NewsAPI",
                    HttpStatus.INTERNAL_SERVER_ERROR);
        } catch (Exception e) {
            log.error("Unknown error occurred: {}", e.getMessage(), e);
            throw new NewsApiException("NEWS-API-UNKNOWN", "Unknown error occurred: " + e.getMessage(),
                    HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return Collections.emptyList();
    }

    private void validateApiResponse(NewsApiArticleResponse response) {
        HttpStatus status = HttpStatus.BAD_GATEWAY;

        if (response == null) {
            throw new NewsApiException("NEWS-API-NO-RESPONSE", "Response was null", status);
        }
        if ("ok".equals(response.getStatus())) {
            return;
        }
        if ("error".equals(response.getStatus())) {
            throw new NewsApiException(response.getCode(), response.getMessage(), status);
        }
        throw new NewsApiException("NEWS-API-UNKNOWN", "Unknown error occurred", status);
    }

    private void handleHttpClientErrorException(HttpClientErrorException ex) {
        log.error("HttpClientErrorException while fetching articles: {}", ex.getMessage(), ex);

        String errorMessage;
        String errorCode;
        HttpStatus status;

        try {
            NewsApiArticleResponse apiErrorResponse = objectMapper.readValue(ex.getResponseBodyAsString(),
                    NewsApiArticleResponse.class);
            errorMessage = apiErrorResponse.getMessage();
            errorCode = apiErrorResponse.getCode();
            status = (HttpStatus) ex.getStatusCode();
        } catch (JsonProcessingException e) {
            errorMessage = "An error occurred while processing your request";
            errorCode = "JSON_PARSE_ERROR";
            status = HttpStatus.UNPROCESSABLE_ENTITY;

            log.error("Failed to parse error response: {}", e.getMessage(), e);
        }

        throw new NewsApiException(errorCode, errorMessage, status);
    }

    private NewsApiSourceResponse getNewsApiSourceResponse(String url) throws JsonProcessingException {
        String response = restTemplate.getForObject(url, String.class);
        return objectMapper.readValue(response, NewsApiSourceResponse.class);
    }

    private ArticleResponse convertToArticleResponse(NewsApiArticle newsApiArticle) {
        return ArticleResponse.builder()
                .author(newsApiArticle.getAuthor())
                .title(newsApiArticle.getTitle())
                .url(newsApiArticle.getUrl())
                .publishedAt(newsApiArticle.getPublishedAt())
                .source(newsApiArticle.getSource() != null ? newsApiArticle.getSource().getName() : null)
                .build();
    }
}
