package io.shinmen.airnewsaggregator.service;

import static io.shinmen.airnewsaggregator.utility.Constants.CACHE_EVERYTHING;
import static io.shinmen.airnewsaggregator.utility.Constants.CACHE_SEARCH;
import static io.shinmen.airnewsaggregator.utility.Constants.CACHE_TOP_HEADLINES;
import static io.shinmen.airnewsaggregator.utility.Constants.NEWS_API_EVERYTHING;
import static io.shinmen.airnewsaggregator.utility.Constants.NEWS_API_EXCEPTION_CODE_JSON_ERROR;
import static io.shinmen.airnewsaggregator.utility.Constants.NEWS_API_EXCEPTION_CODE_NO_RESPONSE_ERROR;
import static io.shinmen.airnewsaggregator.utility.Constants.NEWS_API_EXCEPTION_CODE_UNKNOWN_ERROR;
import static io.shinmen.airnewsaggregator.utility.Constants.NEWS_API_QUERY_API_KEY;
import static io.shinmen.airnewsaggregator.utility.Constants.NEWS_API_QUERY_CATEGORY;
import static io.shinmen.airnewsaggregator.utility.Constants.NEWS_API_QUERY_COUNTRY;
import static io.shinmen.airnewsaggregator.utility.Constants.NEWS_API_QUERY_DOMAINS;
import static io.shinmen.airnewsaggregator.utility.Constants.NEWS_API_QUERY_FROM;
import static io.shinmen.airnewsaggregator.utility.Constants.NEWS_API_QUERY_KEYWORD;
import static io.shinmen.airnewsaggregator.utility.Constants.NEWS_API_QUERY_LANGUAGE;
import static io.shinmen.airnewsaggregator.utility.Constants.NEWS_API_QUERY_PAGE;
import static io.shinmen.airnewsaggregator.utility.Constants.NEWS_API_QUERY_PAGE_SIZE;
import static io.shinmen.airnewsaggregator.utility.Constants.NEWS_API_QUERY_SORT_BY;
import static io.shinmen.airnewsaggregator.utility.Constants.NEWS_API_QUERY_SOURCES;
import static io.shinmen.airnewsaggregator.utility.Constants.NEWS_API_QUERY_TO;
import static io.shinmen.airnewsaggregator.utility.Constants.NEWS_API_SOURCES;
import static io.shinmen.airnewsaggregator.utility.Constants.NEWS_API_STATUS_ERROR;
import static io.shinmen.airnewsaggregator.utility.Constants.NEWS_API_STATUS_OK;
import static io.shinmen.airnewsaggregator.utility.Constants.NEWS_API_TOP_HEADLINES;
import static io.shinmen.airnewsaggregator.utility.Messages.NEWS_API_EXCEPTION_MESSAGE_JSON_ERROR;
import static io.shinmen.airnewsaggregator.utility.Messages.NEWS_API_EXCEPTION_MESSAGE_NO_RESPONSE_ERROR;
import static io.shinmen.airnewsaggregator.utility.Messages.NEWS_API_EXCEPTION_MESSAGE_UNKNOWN_ERROR;

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
import io.shinmen.airnewsaggregator.payload.request.SearchRequest;
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

    @Cacheable(value = CACHE_TOP_HEADLINES, key = "#request.toCacheKey()")
    public List<ArticleResponse> getTopHeadlines(final TopHeadLinesSearchRequest request) {
        final String topHeadlinesUrl = buildUrl(NEWS_API_TOP_HEADLINES,
                NEWS_API_QUERY_KEYWORD, request.getQuery(),
                NEWS_API_QUERY_COUNTRY, request.getCountry(),
                NEWS_API_QUERY_CATEGORY, request.getCategory(),
                NEWS_API_QUERY_SOURCES, request.getSources(),
                NEWS_API_QUERY_PAGE, request.getPage(),
                NEWS_API_QUERY_PAGE_SIZE, request.getPageSize());

        log.info("Headlines url {}", topHeadlinesUrl);
        return getArticleResponses(topHeadlinesUrl);
    }

    @Cacheable(value = CACHE_SEARCH, key = "#request.toCacheKey()")
    public List<ArticleResponse> search(final SearchRequest request) {
        final String searchUrl = buildUrl(NEWS_API_EVERYTHING,
                NEWS_API_QUERY_KEYWORD, request.getQuery(),
                NEWS_API_QUERY_PAGE, request.getPage(),
                NEWS_API_QUERY_PAGE_SIZE, request.getPageSize());

        log.info("Search url: {}", searchUrl);
        return getArticleResponses(searchUrl);
    }

    @Cacheable(value = CACHE_EVERYTHING, key = "#request.toCacheKey()")
    public List<ArticleResponse> getEverything(final EverythingSearchRequest request) {

        final String everythingUrl = buildUrl(NEWS_API_EVERYTHING,
                NEWS_API_QUERY_KEYWORD, request.getQuery(),
                NEWS_API_QUERY_SOURCES, request.getSources(),
                NEWS_API_QUERY_DOMAINS, request.getDomains(),
                NEWS_API_QUERY_FROM, request.getFrom(),
                NEWS_API_QUERY_TO, request.getTo(),
                NEWS_API_QUERY_LANGUAGE, request.getLanguage(),
                NEWS_API_QUERY_SORT_BY, request.getSortBy(),
                NEWS_API_QUERY_PAGE, request.getPage(),
                NEWS_API_QUERY_PAGE_SIZE, request.getPageSize());

        log.info("Everything url {}", everythingUrl);

        return getArticleResponses(everythingUrl);
    }

    @Transactional
    @Scheduled(fixedRate = 24 * 60 * 60 * 1000)
    public void fetchAndUpdateNewsSources() throws JsonProcessingException {
        final String sourcesUrl = buildUrl(NEWS_API_SOURCES);

        log.info("Sources url {}", sourcesUrl);

        final NewsApiSourceResponse newsApiSourceResponse = getNewsApiSourceResponse(sourcesUrl);

        saveSources(newsApiSourceResponse);
    }

    private void saveSources(NewsApiSourceResponse newsApiSourceResponse) {
        try {
            if (newsApiSourceResponse != null && "ok".equals(newsApiSourceResponse.getStatus())) {
                final List<NewsApiSource> newsApiSources = newsApiSourceResponse.getSources();
                for (final NewsApiSource newsApiSource : newsApiSources) {
                    final Optional<Source> source = sourceRepository.findById(newsApiSource.getId());
                    if (source.isPresent()) {
                        final Source existingSource = getSource(newsApiSource, source.get());
                        sourceRepository.save(existingSource);
                    } else {
                        final Source newSource = getNewSource(newsApiSource);
                        sourceRepository.save(newSource);
                    }
                }
            }
        } catch (Exception e) {
            log.error("Error fetching and updating news sources: {}", e.getMessage(), e);
        }
    }

    private Source getNewSource(final NewsApiSource newsApiSource) {

        return Source.builder()
                .id(newsApiSource.getId())
                .name(newsApiSource.getName())
                .description(newsApiSource.getDescription())
                .url(newsApiSource.getUrl())
                .category(newsApiSource.getCategory())
                .language(newsApiSource.getLanguage())
                .country(newsApiSource.getCountry())
                .build();
    }

    private Source getSource(final NewsApiSource newsApiSource, final Source source) {
        source.setName(newsApiSource.getName());
        source.setDescription(newsApiSource.getDescription());
        source.setUrl(newsApiSource.getUrl());
        source.setCategory(newsApiSource.getCategory());
        source.setLanguage(newsApiSource.getLanguage());
        source.setCountry(newsApiSource.getCountry());
        return source;
    }

    private String buildUrl(final String endpoint, final String... queryParams) {
        final UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(newsApiUrl + "/" + endpoint);

        for (int i = 0; i < queryParams.length; i += 2) {
            addQueryParamIfPresent(builder, queryParams[i], queryParams[i + 1]);
        }

        builder.queryParam(NEWS_API_QUERY_API_KEY, apiKey);

        return builder.toUriString();
    }

    private void addQueryParamIfPresent(final UriComponentsBuilder builder, final String paramName,
            final String paramValue) {
        Optional.ofNullable(paramValue).ifPresent(value -> builder.queryParam(paramName, value));
    }

    private List<ArticleResponse> getArticleResponses(final String url) {
        try {
            final String response = restTemplate.getForObject(url, String.class);

            final NewsApiArticleResponse articleResponse = objectMapper.readValue(response,
                    NewsApiArticleResponse.class);

            validateApiResponse(articleResponse);

            return articleResponse.getArticles().stream().map(this::convertToArticleResponse).toList();
        } catch (HttpClientErrorException ex) {
            handleHttpClientErrorException(ex);
        } catch (JsonProcessingException e) {
            log.error("Error processing JSON response: {}", e.getMessage(), e);

            throw new NewsApiException(NEWS_API_EXCEPTION_CODE_JSON_ERROR,
                    NEWS_API_EXCEPTION_MESSAGE_JSON_ERROR,
                    HttpStatus.INTERNAL_SERVER_ERROR);
        } catch (Exception e) {
            log.error("Unknown error occurred: {}", e.getMessage(), e);

            throw new NewsApiException(NEWS_API_EXCEPTION_CODE_UNKNOWN_ERROR,
                    NEWS_API_EXCEPTION_MESSAGE_UNKNOWN_ERROR + ": " + e.getMessage(),
                    HttpStatus.INTERNAL_SERVER_ERROR);
        }

        return Collections.emptyList();
    }

    private void validateApiResponse(final NewsApiArticleResponse response) {
        final HttpStatus status = HttpStatus.BAD_GATEWAY;

        if (response == null) {
            throw new NewsApiException(NEWS_API_EXCEPTION_CODE_NO_RESPONSE_ERROR,
                    NEWS_API_EXCEPTION_MESSAGE_NO_RESPONSE_ERROR, status);
        }
        if (NEWS_API_STATUS_OK.equals(response.getStatus())) {
            return;
        }
        if (NEWS_API_STATUS_ERROR.equals(response.getStatus())) {
            throw new NewsApiException(response.getCode(), response.getMessage(), status);
        }
        throw new NewsApiException(NEWS_API_EXCEPTION_CODE_UNKNOWN_ERROR, NEWS_API_EXCEPTION_MESSAGE_UNKNOWN_ERROR,
                status);
    }

    private void handleHttpClientErrorException(final HttpClientErrorException ex) {
        log.error("HttpClientErrorException while fetching articles: {}", ex.getMessage(), ex);

        String errorMessage;
        String errorCode;
        HttpStatus status;

        try {
            final NewsApiArticleResponse apiErrorResponse = objectMapper.readValue(ex.getResponseBodyAsString(),
                    NewsApiArticleResponse.class);

            errorMessage = apiErrorResponse.getMessage();
            errorCode = apiErrorResponse.getCode();
            status = (HttpStatus) ex.getStatusCode();
        } catch (JsonProcessingException e) {
            errorMessage = NEWS_API_EXCEPTION_MESSAGE_JSON_ERROR;
            errorCode = NEWS_API_EXCEPTION_CODE_JSON_ERROR;
            status = HttpStatus.UNPROCESSABLE_ENTITY;

            log.error("Failed to parse error response: {}", e.getMessage(), e);
        }

        throw new NewsApiException(errorCode, errorMessage, status);
    }

    private NewsApiSourceResponse getNewsApiSourceResponse(String url) throws JsonProcessingException {
        final String response = restTemplate.getForObject(url, String.class);
        return objectMapper.readValue(response, NewsApiSourceResponse.class);
    }

    private ArticleResponse convertToArticleResponse(final NewsApiArticle newsApiArticle) {
        return ArticleResponse.builder()
                .author(newsApiArticle.getAuthor())
                .title(newsApiArticle.getTitle())
                .url(newsApiArticle.getUrl())
                .publishedAt(newsApiArticle.getPublishedAt())
                .source(newsApiArticle.getSource() != null ? newsApiArticle.getSource().getName() : null)
                .build();
    }
}
