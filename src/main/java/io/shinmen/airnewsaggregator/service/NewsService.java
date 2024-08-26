package io.shinmen.airnewsaggregator.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.fasterxml.jackson.core.JsonProcessingException;

import io.shinmen.airnewsaggregator.exception.UserNotFoundException;
import io.shinmen.airnewsaggregator.model.Preference;
import io.shinmen.airnewsaggregator.model.Source;
import io.shinmen.airnewsaggregator.model.User;
import io.shinmen.airnewsaggregator.model.enums.Category;
import io.shinmen.airnewsaggregator.model.enums.Country;
import io.shinmen.airnewsaggregator.model.enums.Language;
import io.shinmen.airnewsaggregator.payload.request.EverythingQueryRequest;
import io.shinmen.airnewsaggregator.payload.request.EverythingSearchRequest;
import io.shinmen.airnewsaggregator.payload.request.TopHeadLinesQueryRequest;
import io.shinmen.airnewsaggregator.payload.request.TopHeadLinesSearchRequest;
import io.shinmen.airnewsaggregator.payload.response.ArticleResponse;
import io.shinmen.airnewsaggregator.payload.response.NewsResponse;
import io.shinmen.airnewsaggregator.repository.PreferenceRepository;
import io.shinmen.airnewsaggregator.repository.UserRepository;

import lombok.Builder;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class NewsService {

    private final NewsApiService newsApiService;
    private final UserRepository userRepository;
    private final PreferenceRepository preferenceRepository;

    public NewsResponse getTopHeadLines(TopHeadLinesQueryRequest request, String userName) {
        User user = request.isUser() ? getUser(userName) : null;
        Preference preference = user != null ? getPreference(user) : new Preference();

        String sources = getSources(request.getSources(), preference, request.isUser());
        List<String> categories = getCategories(sources, request, preference);
        String country = getCountry(sources, request, preference);

        String page = getPageData(request.getPage());
        String pageSize = getPageData(request.getPageSize());

        List<ArticleResponse> articleResponses = getArticleResponses(request.getQuery(), country, categories, sources,
                page, pageSize);

        return NewsResponse.builder()
                .articles(articleResponses)
                .total(articleResponses == null ? 0 : articleResponses.size())
                .build();
    }

    public NewsResponse searchNews(String keyword, int page, int pageSize) {
        List<ArticleResponse> articleResponses = newsApiService.search(keyword, page, pageSize);
        return NewsResponse.builder()
                .articles(articleResponses)
                .total(articleResponses == null ? 0 : articleResponses.size())
                .build();
    }

    public NewsResponse getEverything(EverythingQueryRequest request, String userName) throws JsonProcessingException {
        User user = request.isUser() ? getUser(userName) : null;
        Preference preference = user != null ? getPreference(user) : new Preference();

        String sources = getSources(request.getSources(), preference, request.isUser());
        String page = getPageData(request.getPage());
        String pageSize = getPageData(request.getPageSize());
        String domains = request.getDomains();
        String from = request.getFrom();
        String to = request.getTo();
        String language = getLanguage(Optional.ofNullable(request.getLanguage()), preference, request.isUser());
        String sortBy = Optional.ofNullable(request.getSortBy()).map(Enum::toString).orElse(null);

        RequestParams params = RequestParams.builder()
                .query(request.getQuery())
                .sources(sources)
                .domains(domains)
                .language(language)
                .sortBy(sortBy)
                .from(from)
                .to(to)
                .page(page)
                .pageSize(pageSize)
                .build();

        List<ArticleResponse> articleResponses = getArticleResponses(params);

        return NewsResponse.builder()
                .articles(articleResponses)
                .total(articleResponses == null ? 0 : articleResponses.size())
                .build();
    }

    private List<ArticleResponse> getArticleResponses(RequestParams params) {
        EverythingSearchRequest request = EverythingSearchRequest.builder()
                .query(params.query())
                .domains(params.domains())
                .sources(params.sources())
                .language(params.language())
                .sortBy(params.sortBy())
                .page(params.page())
                .pageSize(params.pageSize())
                .from(params.from())
                .to(params.to())
                .build();

        return newsApiService.getEverything(request);
    }

    private List<ArticleResponse> getArticleResponses(String query, String country, List<String> categories,
            String sources, String page, String pageSize) {
        return categories.stream()
                .map(category -> createTopHeadLinesSearchRequest(query, country, category, sources, page, pageSize))
                .flatMap(request -> newsApiService.getTopHeadlines(request).stream())
                .toList();
    }

    private TopHeadLinesSearchRequest createTopHeadLinesSearchRequest(String query, String country, String category,
            String sources, String page, String pageSize) {
        return TopHeadLinesSearchRequest.builder()
                .query(query)
                .country(country)
                .category(category)
                .sources(sources)
                .page(page)
                .pageSize(pageSize)
                .build();
    }

    private List<String> getCategories(String sources, TopHeadLinesQueryRequest request, Preference preference) {
        List<String> categories = new ArrayList<>();
        if (sources == null) {
            categories.addAll(getCategories(request.getCategory(), preference, request.isUser()));
            return categories;
        }

        categories.add(null);
        return categories;
    }

    private String getCountry(String sources, TopHeadLinesQueryRequest request, Preference preference) {
        if (sources == null) {
            return getValidCountry(Optional.ofNullable(request.getCountry()), preference, request.isUser());
        }
        return null;
    }

    private String getSources(String sources, Preference preference, boolean user) {
        String userSources = user && preference != null && preference.getSources() != null
                ? String.join(",", preference.getSources().stream().map(Source::getId).toList())
                : null;

        return Stream.of(userSources, sources)
                .filter(StringUtils::hasText)
                .collect(Collectors.joining(","));
    }

    private List<String> getCategories(Category category, Preference preference, boolean user) {
        List<String> categories = new ArrayList<>();

        if (category != null) {
            categories.add(category.toValue());
        }

        if (user && preference != null && preference.getCategories() != null) {
            categories.addAll(preference.getCategories().stream().map(Category::toValue).toList());
        }

        return categories;
    }

    private String getValidCountry(Optional<Country> country, Preference preference, boolean isUser) {
        return country.map(Country::toValue)
                .orElseGet(() -> isUser && preference != null && preference.getCountry() != null
                        ? preference.getCountry().toValue()
                        : null);
    }

    private String getLanguage(Optional<Language> language, Preference preference, boolean user) {
        return language.map(Language::toValue)
                .orElseGet(() -> user && preference != null && preference.getLanguage() != null
                        ? preference.getLanguage().toValue()
                        : null);
    }

    private String getPageData(Integer pageSize) {
        return pageSize != null ? pageSize.toString() : "1";
    }

    private Preference getPreference(User user) {
        return preferenceRepository.findByUser(user).orElse(new Preference());
    }

    private User getUser(String userName) {
        return userRepository.findByUsername(userName)
                .orElseThrow(() -> new UserNotFoundException("User with username: " + userName + " not found"));
    }

    @Builder
    private record RequestParams(
            String query,
            String sources,
            String page,
            String pageSize,
            String domains,
            String from,
            String to,
            String language,
            String sortBy) {
    }
}
