package io.shinmen.airnewsaggregator.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.shinmen.airnewsaggregator.payload.request.EverythingSearchRequest;
import io.shinmen.airnewsaggregator.payload.request.TopHeadLinesSearchRequest;
import io.shinmen.airnewsaggregator.payload.response.NewsArticlesResponse;
import io.shinmen.airnewsaggregator.payload.response.NewsArticlesResponse.NewsArticlesResponseBuilder;
import io.shinmen.airnewsaggregator.payload.response.newsapi.NewsApiArticleResponse;
import io.shinmen.airnewsaggregator.payload.response.newsapi.NewsApiResponse;
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

    private static final String QUERY = "q";
    private static final String SOURCES = "sources";
    private static final String COUNTRY = "country";
    private static final String LANGUAGE = "language";
    private static final String DOMAINS = "domains";
    private static final String FROM = "from";
    private static final String TO = "to";
    private static final String CATEGORY = "category";
    private static final String PAGE = "page";
    private static final String PAGE_SIZE = "pageSize";
    private static final String SORT_BY = "sortBy";
    private static final String TOP_HEADLINES = "top-headlines";
    private static final String EVERYTHING = "everything";
    private static final String API_KEY = "apiKey";

    @Cacheable(value = "topHeadlines", key = "#request.toCacheKey()")
    public List<NewsArticlesResponse> getTopHeadlines(TopHeadLinesSearchRequest request)
            throws JsonProcessingException {

        log.info("Top headlines cache key: {}", request.toCacheKey());

        String topHeadlinesUrl = buildTopHeadlinesUrl(request.getQuery(),
                request.getCountry(),
                request.getCategory(), request.getSources(),
                request.getPage(), request.getPageSize(),
                request.isUseSources());

        log.info("Fetch top-headlines for the URL: {}", topHeadlinesUrl);

        return getNewsResponses(topHeadlinesUrl);
    }

    @Cacheable(value = "search", key = "#query + '-' + #page + '-' + #pageSize")
    public List<NewsArticlesResponse> search(String query, int page, int pageSize)
            throws JsonProcessingException {

        log.info("Search news cache key: {}", query + "-" + page + "-" + pageSize);

        String searchUrl = buildUrlForSearch(query, page, pageSize);

        log.info("Fetch news for the URL: {}", searchUrl);

        return getNewsResponses(searchUrl);
    }

    @Cacheable(value = "everything", key = "#request.toCacheKey()")
    public List<NewsArticlesResponse> getEverything(EverythingSearchRequest request)
            throws JsonProcessingException {

        log.info("Everything news cache key: {}", request.toCacheKey());

        String everythingUrl = buildEverythingUrl(request.getQuery(), request.getSources(),
                request.getDomains(), request.getFrom(), request.getTo(), request.getLanguage(),
                request.getSortBy(), request.getPageSize(), request.getPage());

        log.info("Everything news for the URL: {}", everythingUrl);

        return getNewsResponses(everythingUrl);
    }

    private String buildTopHeadlinesUrl(String query, String country, String category, String sources, String page,
            String pageSize, boolean useSources) {
        if (useSources) {
            return UriComponentsBuilder.fromHttpUrl(newsApiUrl + "/" + TOP_HEADLINES)
                    .queryParam(QUERY, query)
                    .queryParam(SOURCES, sources)
                    .queryParam(PAGE, page)
                    .queryParam(PAGE_SIZE, pageSize)
                    .queryParam(API_KEY, apiKey)
                    .toUriString();
        }

        return UriComponentsBuilder.fromHttpUrl(newsApiUrl + "/" + TOP_HEADLINES)
                .queryParam(QUERY, query)
                .queryParam(COUNTRY, country)
                .queryParam(CATEGORY, category)
                .queryParam(PAGE, page)
                .queryParam(PAGE_SIZE, pageSize)
                .queryParam(API_KEY, apiKey)
                .toUriString();
    }

    private String buildUrlForSearch(String query, int page, int pageSize) {
        return UriComponentsBuilder.fromHttpUrl(newsApiUrl + "/" + EVERYTHING)
                .queryParam(QUERY, query)
                .queryParam(PAGE, page)
                .queryParam(PAGE_SIZE, pageSize)
                .queryParam(API_KEY, apiKey)
                .toUriString();
    }

    private String buildEverythingUrl(String query, String sources, String domains, String from, String to,
            String language, String sortBy, String pageSize, String page) {
        return UriComponentsBuilder.fromHttpUrl(newsApiUrl + "/" + EVERYTHING)
                .queryParam(QUERY, query)
                .queryParam(SOURCES, sources)
                .queryParam(DOMAINS, domains)
                .queryParam(FROM, from)
                .queryParam(TO, to)
                .queryParam(LANGUAGE, language)
                .queryParam(SORT_BY, sortBy)
                .queryParam(PAGE, page)
                .queryParam(PAGE_SIZE, pageSize)
                .queryParam(API_KEY, apiKey)
                .toUriString();
    }

    private NewsArticlesResponse convertToNewsResponse(NewsApiArticleResponse newsApiArticleResponse) {
        NewsArticlesResponseBuilder newsArticlesResponseBuilder = NewsArticlesResponse.builder()
                .author(newsApiArticleResponse.getAuthor())
                .title(newsApiArticleResponse.getTitle())
                .url(newsApiArticleResponse.getUrl())
                .publishedAt(newsApiArticleResponse.getPublishedAt());

        if (newsApiArticleResponse.getSource() != null) {
            newsArticlesResponseBuilder.source(newsApiArticleResponse.getSource().getName());
        }

        return newsArticlesResponseBuilder.build();
    }

    private List<NewsArticlesResponse> getNewsResponses(String url) throws JsonProcessingException {
        String newsApiResponseString = restTemplate.getForObject(url, String.class);
        NewsApiResponse newsApiResponse = objectMapper.readValue(newsApiResponseString, NewsApiResponse.class);
        return newsApiResponse.getArticles().stream().map(this::convertToNewsResponse).toList();
    }
}
