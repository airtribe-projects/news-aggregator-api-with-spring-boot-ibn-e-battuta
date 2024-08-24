package io.shinmen.airnewsaggregator.service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.fasterxml.jackson.core.JsonProcessingException;

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
import io.shinmen.airnewsaggregator.repository.NewsPreferenceRepository;
import io.shinmen.airnewsaggregator.repository.UserRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class NewsService {

    private final NewsApiService newsApiService;
    private final UserRepository userRepository;
    private final NewsPreferenceRepository newsPreferenceRepository;

    public NewsResponse getTopHeadLines(TopHeadLinesQueryRequest topHeadlinesQueryRequest,
            String userName) throws JsonProcessingException {

        User user = getUser(userName);
        NewsPreference newsPreference = getNewsPreference(user);

        String query = getQuery(topHeadlinesQueryRequest.getQuery());
        String country = getCountry(topHeadlinesQueryRequest, newsPreference);
        List<String> categories = getCategories(topHeadlinesQueryRequest, newsPreference, query);
        String sources = getSources(topHeadlinesQueryRequest.getSources(), newsPreference);
        String page = getPageData(topHeadlinesQueryRequest.getPage());
        String pageSize = getPageData(topHeadlinesQueryRequest.getPageSize());
        boolean useSources = getUseSources(topHeadlinesQueryRequest, sources);

        List<NewsArticlesResponse> newsArticlesResponses = getNewsArticlesResponses(query, country, categories, sources,
                page, pageSize, useSources);

        return NewsResponse.builder()
                .articles(newsArticlesResponses)
                .total(newsArticlesResponses.size())
                .build();
    }

    public NewsResponse searchNews(String keyword, int page, int pageSize) throws JsonProcessingException {
        List<NewsArticlesResponse> newsArticlesResponses = newsApiService.search(keyword, page, pageSize);
        return NewsResponse.builder()
                .articles(newsArticlesResponses)
                .total(newsArticlesResponses == null ? 0 : newsArticlesResponses.size())
                .build();
    }

    public NewsResponse getEverything(EverythingQueryRequest everythingQueryRequest, String userName) throws JsonProcessingException {

        User user = getUser(userName);
        NewsPreference newsPreference = getNewsPreference(user);

        String query = getQuery(everythingQueryRequest.getQuery());
        String sources = getSources(everythingQueryRequest.getSources(), newsPreference);

        String page = getPageData(everythingQueryRequest.getPage());
        String pageSize = getPageData(everythingQueryRequest.getPageSize());

        String domains = everythingQueryRequest.getDomains();

        String from = everythingQueryRequest.getFrom();
        String to = everythingQueryRequest.getTo();

        String language = getLanguage(everythingQueryRequest);

        String sortBy = everythingQueryRequest.getSortBy() == null ? null : everythingQueryRequest.getSortBy().toValue();

        List<NewsArticlesResponse> newsArticlesResponses = getNewsArticlesResponses(query, sources, page, pageSize, domains, from, to, language, sortBy);

        return NewsResponse.builder()
                .articles(newsArticlesResponses)
                .total(newsArticlesResponses == null ? 0 : newsArticlesResponses.size())
                .build();
    }

    private List<NewsArticlesResponse> getNewsArticlesResponses(String query, String sources, String page, String pageSize,
            String domains, String from, String to, String language, String sortBy)
            throws JsonProcessingException {

        EverythingSearchRequest everythingSearchRequest = EverythingSearchRequest.builder()
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

        return newsApiService.getEverything(everythingSearchRequest);
    }

    private String getLanguage(EverythingQueryRequest everythingQueryRequest) {
        return everythingQueryRequest.getLanguage() != null
                ? everythingQueryRequest.getLanguage().toValue()
                : null;
    }

    private String getPageData(Integer pageSize) {
        return String.valueOf(pageSize);
    }

    private List<NewsArticlesResponse> getNewsArticlesResponses(String query, String country, List<String> categories,
            String sources, String page, String pageSize, boolean useSources) throws JsonProcessingException {
        List<NewsArticlesResponse> newsArticlesResponses = new ArrayList<>();

        for (String category : categories) {
            TopHeadLinesSearchRequest topHeadLinesSearchRequest = TopHeadLinesSearchRequest.builder()
                    .query(query)
                    .country(country)
                    .category(category)
                    .sources(sources)
                    .page(page)
                    .pageSize(pageSize)
                    .useSources(useSources)
                    .build();

            newsArticlesResponses.addAll(newsApiService.getTopHeadlines(topHeadLinesSearchRequest));
        }
        return newsArticlesResponses;
    }

    private boolean getUseSources(TopHeadLinesQueryRequest topHeadlinesQueryRequest, String sources) {
        return sources != null && topHeadlinesQueryRequest.isUseSources();
    }

    private String getSources(String querySources, NewsPreference newsPreference) {
        String userSources = newsPreference.getSources() != null && !newsPreference.getSources().isEmpty()
                ? String.join(",", newsPreference.getSources())
                : null;

        String sources = querySources != null
                ? querySources
                : null;

        return (StringUtils.hasText(userSources) || StringUtils.hasText(sources))
                ? Stream.of(userSources, sources)
                        .filter(StringUtils::hasText)
                        .collect(Collectors.joining(","))
                : null;
    }

    private List<String> getCategories(TopHeadLinesQueryRequest topHeadlinesQueryRequest,
            NewsPreference userNewsPreference, String query) {
        List<String> categories = new ArrayList<>();
        if (topHeadlinesQueryRequest.getCategory() != null) {
            categories.add(topHeadlinesQueryRequest.getCategory().toValue());
        } else if (StringUtils.hasText(query)) {
            categories.add(null);
        } else {
            List<Category> userCategories = new ArrayList<>(userNewsPreference.getCategories());
            categories.addAll(userCategories.stream().map(Category::toValue).toList());
        }
        return categories;
    }

    private String getCountry(TopHeadLinesQueryRequest topHeadlinesQueryRequest, NewsPreference newsPreference) {
        String country;
        if (topHeadlinesQueryRequest.getCountry() != null) {
            country = topHeadlinesQueryRequest.getCountry().toValue();
        } else if (newsPreference.getCountry() != null) {
            country = newsPreference.getCountry().toValue();
        } else {
            country = Country.US.toValue();
        }
        return country;
    }

    private String getQuery(String query) {
        return query != null
                ? query.replace(" ", "-")
                : null;
    }

    private NewsPreference getNewsPreference(User user) {
        return newsPreferenceRepository.findByUser(user)
                .orElse(new NewsPreference());
    }

    private User getUser(String userName) {
        return userRepository.findByUsername(userName)
                .orElseThrow(() -> new UserNotFoundException(
                        "User with username: " + userName + " not found"));
    }
}
