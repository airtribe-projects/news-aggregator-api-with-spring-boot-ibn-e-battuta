package io.shinmen.airnewsaggregator.service;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.shinmen.airnewsaggregator.exception.UserNotFoundException;
import io.shinmen.airnewsaggregator.model.NewsPreference;
import io.shinmen.airnewsaggregator.model.User;
import io.shinmen.airnewsaggregator.model.enums.Category;
import io.shinmen.airnewsaggregator.model.enums.Country;
import io.shinmen.airnewsaggregator.payload.request.EverythingQueryRequest;
import io.shinmen.airnewsaggregator.payload.request.EverythingSearchRequest;
import io.shinmen.airnewsaggregator.payload.request.TopHeadLinesQueryRequest;
import io.shinmen.airnewsaggregator.payload.request.TopHeadLinesSearchRequest;
import io.shinmen.airnewsaggregator.payload.response.NewsArticlesResponse;
import io.shinmen.airnewsaggregator.payload.response.NewsResponse;
import io.shinmen.airnewsaggregator.payload.response.newsapi.NewsApiArticleResponse;
import io.shinmen.airnewsaggregator.payload.response.newsapi.NewsApiResponse;
import io.shinmen.airnewsaggregator.repository.NewsPreferenceRepository;
import io.shinmen.airnewsaggregator.repository.UserRepository;
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

    private final NewsPreferenceRepository newsPreferenceRepository;
    private final UserRepository userRepository;
    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;
    private final ModelMapper modelMapper;

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
    public NewsResponse getTopHeadlines(TopHeadLinesSearchRequest request)
            throws JsonProcessingException {

        String topHeadlinesUrl = buildTopHeadlinesUrl(request.getQuery(),
                request.getCountry(),
                request.getCategory(), request.getSources(),
                request.getPage(), request.getPageSize(),
                request.isUseSources());

        log.info("Fetch top-headlines for the URL: {}", topHeadlinesUrl);

        return new NewsResponse(getNewsResponses(topHeadlinesUrl));
    }

    @Cacheable(value = "search", key = "#query + '-' + #page + '-' + #pageSize")
    public NewsResponse search(String query, int page, int pageSize)
            throws JsonProcessingException {

        String url = buildUrlForSearch(query, page, pageSize);
        return new NewsResponse(getNewsResponses(url));
    }

    @Cacheable(value = "everything", key = "#request.toCacheKey()")
    public NewsResponse getEverything(EverythingSearchRequest request)
            throws JsonProcessingException {

        String everythingUrl = buildEverythingUrl(request.getQuery(), request.getSources(),
                request.getDomains(), request.getFrom(), request.getTo(), request.getLanguage(),
                request.getSortBy(), request.getPageSize(), request.getPage());

        return new NewsResponse(getNewsResponses(everythingUrl));
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
        NewsArticlesResponse newsArticlesResponse = modelMapper.map(newsApiArticleResponse,
                NewsArticlesResponse.class);

        if (newsApiArticleResponse.getSource() != null) {
            newsArticlesResponse.setSource(newsApiArticleResponse.getSource().getName());
        }

        return newsArticlesResponse;
    }

    private List<NewsArticlesResponse> getNewsResponses(String url) throws JsonProcessingException {
        String newsApiResponseString = restTemplate.getForObject(url, String.class);
        NewsApiResponse newsApiResponse = objectMapper.readValue(newsApiResponseString, NewsApiResponse.class);
        return newsApiResponse.getArticles().stream().map(this::convertToNewsResponse).toList();
    }

    public TopHeadLinesSearchRequest getTopHeadLinesRequest(TopHeadLinesQueryRequest topHeadlinesQueryRequest,
            String userName) {

        User user = userRepository.findByUsername(userName)
                .orElseThrow(() -> new UserNotFoundException("User with username: " + userName + " not found"));

        NewsPreference userNewsPreference = newsPreferenceRepository.findByUser(user).orElse(new NewsPreference());

        String query = topHeadlinesQueryRequest.getQuery() != null
                ? topHeadlinesQueryRequest.getQuery().replace(" ", "-")
                : "";

        String country;
        if (topHeadlinesQueryRequest.getCountry() != null) {
            country = topHeadlinesQueryRequest.getCountry().toValue();
        } else {
            country = Country.US.toValue();
        }

        String category;
        if (topHeadlinesQueryRequest.getCategory() != null) {
            category = topHeadlinesQueryRequest.getCategory().toValue();
        } else if (query.isEmpty()) {
            category = "";
        } else {
            List<Category> userCategories = new ArrayList<>(userNewsPreference.getCategories());
            category = !userCategories.isEmpty()
                    ? userCategories.get(ThreadLocalRandom.current().nextInt(userCategories.size()))
                            .toValue()
                    : "";
        }

        String userSources = userNewsPreference.getSources() != null
                ? String.join(",", userNewsPreference.getSources())
                : "";

        String headlineSources = topHeadlinesQueryRequest.getSources() != null
                ? topHeadlinesQueryRequest.getSources()
                : "";

        String sources = Stream.of(userSources, headlineSources)
                .filter(s -> !s.isEmpty())
                .collect(Collectors.joining(","));

        String page = String.valueOf(topHeadlinesQueryRequest.getPage());
        String pageSize = String.valueOf(topHeadlinesQueryRequest.getPageSize());

        Boolean useSources = topHeadlinesQueryRequest.getUseSources();

        return TopHeadLinesSearchRequest.builder()
                .query(query)
                .country(country)
                .category(category)
                .sources(sources)
                .page(page)
                .pageSize(pageSize)
                .useSources(useSources)
                .build();
    }

    public EverythingSearchRequest getEverythingRequest(EverythingQueryRequest everythingQueryRequest,
            String userName) {

        User user = userRepository.findByUsername(userName)
                .orElseThrow(() -> new UserNotFoundException("User with username: " + userName + " not found"));

        NewsPreference userNewsPreference = newsPreferenceRepository.findByUser(user).orElse(new NewsPreference());

        String query = everythingQueryRequest.getQuery() != null
                ? everythingQueryRequest.getQuery().replace(" ", "-")
                : "";

        String userSources = userNewsPreference.getSources() != null
                ? String.join(",", userNewsPreference.getSources())
                : "";

        String headlineSources = everythingQueryRequest.getSources() != null
                ? everythingQueryRequest.getSources()
                : "";

        String sources = Stream.of(userSources, headlineSources)
                .filter(s -> !s.isEmpty())
                .collect(Collectors.joining(","));

        String page = String.valueOf(everythingQueryRequest.getPage());
        String pageSize = String.valueOf(everythingQueryRequest.getPageSize());

        String domains = everythingQueryRequest.getDomains();

        String from = everythingQueryRequest.getFrom() != null ? everythingQueryRequest.getFrom().toString()
                : "";
        String to = everythingQueryRequest.getTo() != null ? everythingQueryRequest.getTo().toString() : "";

        String language = everythingQueryRequest.getLanguage() != null
                ? everythingQueryRequest.getLanguage().toValue()
                : "";

        String sortBy = everythingQueryRequest.getSortBy();

        return EverythingSearchRequest.builder()
                .query(query)
                .domains(domains)
                .sources(sources)
                .language(language)
                .sortBy(sortBy)
                .page(page)
                .pageSize(pageSize)
                .from(from)
                .to(to)
                .build();
    }

    @CacheEvict(value = { "topHeadlines", "search", "everything" }, allEntries = true)
    public void refreshCache() {
        log.info("Refreshing news cache");
    }
}
