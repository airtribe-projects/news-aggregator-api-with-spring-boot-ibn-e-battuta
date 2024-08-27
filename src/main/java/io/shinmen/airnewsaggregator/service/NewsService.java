package io.shinmen.airnewsaggregator.service;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
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
import io.shinmen.airnewsaggregator.payload.request.SearchRequest;
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

        public NewsResponse getTopHeadLines(final TopHeadLinesQueryRequest request, final String userName) {

                final User user = request.isUser() ? getUser(userName) : null;
                final Preference preference = user != null ? getPreference(user) : new Preference();
                final String sources = getSources(request.getSources(), preference, request.isUser());
                final Set<String> categories = getCategories(sources, request, preference);
                final String country = getCountry(sources, request, preference);
                final String page = getPageData(request.getPage());
                final String pageSize = getPageData(request.getPageSize());

                final List<ArticleResponse> articleResponses = getArticleResponses(request.getQuery(), country,
                                categories,
                                sources, page, pageSize);

                return NewsResponse.builder()
                                .articles(articleResponses)
                                .total(articleResponses == null ? 0 : articleResponses.size())
                                .build();
        }

        public NewsResponse searchNews(final String keyword, final int page, final int pageSize) {

                SearchRequest request = SearchRequest.builder()
                                .query(keyword)
                                .page(String.valueOf(page))
                                .pageSize(String.valueOf(pageSize))
                                .build();

                final List<ArticleResponse> articleResponses = newsApiService.search(request);

                return NewsResponse.builder()
                                .articles(articleResponses)
                                .total(articleResponses == null ? 0 : articleResponses.size())
                                .build();
        }

        public NewsResponse getEverything(final EverythingQueryRequest request, final String userName)
                        throws JsonProcessingException {

                final User user = request.isUser() ? getUser(userName) : null;
                final Preference preference = user != null ? getPreference(user) : new Preference();

                final String sources = getSources(request.getSources(), preference, request.isUser());
                final String page = getPageData(request.getPage());
                final String pageSize = getPageData(request.getPageSize());
                final String domains = request.getDomains();
                final String from = request.getFrom();
                final String to = request.getTo();
                final String language = getLanguage(Optional.ofNullable(request.getLanguage()), preference,
                                request.isUser());
                final String sortBy = Optional.ofNullable(request.getSortBy()).map(Enum::toString).orElse(null);

                final RequestParams params = RequestParams.builder()
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

                final List<ArticleResponse> articleResponses = getArticleResponses(params);

                return NewsResponse.builder()
                                .articles(articleResponses)
                                .total(articleResponses == null ? 0 : articleResponses.size())
                                .build();
        }

        private List<ArticleResponse> getArticleResponses(final RequestParams params) {

                final EverythingSearchRequest request = EverythingSearchRequest.builder()
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

        private List<ArticleResponse> getArticleResponses(final String query, final String country,
                        final Set<String> categories,
                        final String sources, final String page, final String pageSize) {

                return categories.stream()
                                .map(category -> createTopHeadLinesSearchRequest(query, country, category, sources,
                                                page, pageSize))
                                .flatMap(request -> newsApiService.getTopHeadlines(request).stream())
                                .toList();
        }

        private TopHeadLinesSearchRequest createTopHeadLinesSearchRequest(final String query, final String country,
                        final String category,
                        final String sources, final String page, final String pageSize) {

                return TopHeadLinesSearchRequest.builder()
                                .query(query)
                                .country(country)
                                .category(category)
                                .sources(sources)
                                .page(page)
                                .pageSize(pageSize)
                                .build();
        }

        private Set<String> getCategories(final String sources, final TopHeadLinesQueryRequest request,
                        final Preference preference) {

                final Set<String> categories = new HashSet<>();

                if (sources == null) {
                        categories.addAll(getCategories(request.getCategory(), preference, request.isUser()));

                        return categories;
                }

                categories.add(null);

                return categories;
        }

        private String getCountry(final String sources, final TopHeadLinesQueryRequest request,
                        final Preference preference) {

                if (sources == null) {
                        return getValidCountry(Optional.ofNullable(request.getCountry()), preference, request.isUser());
                }

                return null;
        }

        private String getSources(final String sources, final Preference preference, final boolean user) {

                if (sources != null) {
                        final String userSources = user && preference != null && preference.getSources() != null
                                        ? String.join(",", preference.getSources()
                                                        .stream()
                                                        .map(Source::getId)
                                                        .toList())
                                        : null;

                        return Stream.of(userSources, sources)
                                        .filter(StringUtils::hasText)
                                        .flatMap(src -> Arrays.stream(src.split(",")))
                                        .collect(Collectors.toSet())
                                        .stream()
                                        .collect(Collectors.joining(","));
                }

                return null;
        }

        private Set<String> getCategories(final Category category, final Preference preference, final boolean user) {

                Set<String> categories = new HashSet<>();

                if (category != null) {
                        categories.add(category.toValue());
                }

                if (user && preference != null && preference.getCategories() != null) {
                        categories.addAll(preference.getCategories()
                                        .stream()
                                        .map(Category::toValue)
                                        .toList());
                }

                return categories;
        }

        private String getValidCountry(final Optional<Country> country, final Preference preference,
                        final boolean isUser) {

                return country.map(Country::toValue)
                                .orElseGet(() -> isUser && preference != null && preference.getCountry() != null
                                                ? preference.getCountry().toValue()
                                                : null);
        }

        private String getLanguage(final Optional<Language> language, final Preference preference, final boolean user) {

                return language.map(Language::toValue)
                                .orElseGet(() -> user && preference != null && preference.getLanguage() != null
                                                ? preference.getLanguage().toValue()
                                                : null);
        }

        private String getPageData(final int pageData) {
                return String.valueOf(pageData);
        }

        private Preference getPreference(final User user) {
                return preferenceRepository.findByUser(user)
                                .orElse(new Preference());
        }

        private User getUser(final String userName) {
                return userRepository.findByUsername(userName)
                                .orElseThrow(() -> new UserNotFoundException(userName));
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
