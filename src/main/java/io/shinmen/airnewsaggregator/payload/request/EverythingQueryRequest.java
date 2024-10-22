package io.shinmen.airnewsaggregator.payload.request;

import io.shinmen.airnewsaggregator.model.enums.Language;
import io.shinmen.airnewsaggregator.payload.request.enums.SortBy;
import io.shinmen.airnewsaggregator.payload.request.validator.annotation.ValidDateRange;
import io.shinmen.airnewsaggregator.payload.request.validator.annotation.ValidDomain;
import io.shinmen.airnewsaggregator.payload.request.validator.annotation.ValidEverythingRequest;
import io.shinmen.airnewsaggregator.payload.request.validator.annotation.ValidLanguage;
import io.shinmen.airnewsaggregator.payload.request.validator.annotation.ValidSortBy;
import io.shinmen.airnewsaggregator.payload.request.validator.annotation.ValidSource;
import io.shinmen.airnewsaggregator.payload.request.validator.annotation.ValidZonedDateTime;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@ValidDateRange
@ValidEverythingRequest
public class EverythingQueryRequest {

    private String query;

    @ValidSource
    private String sources;

    @ValidDomain(message = "A domain must be valid and follow the format like 'example.com, example.org'")
    private String domains;

    @ValidZonedDateTime(message = "Invalid 'from' date format. Use 'yyyy-MM-dd' or 'yyyy-MM-ddTHH:mm:ss'")
    private String from;

    @ValidZonedDateTime(message = "Invalid 'to' date format. Use 'yyyy-MM-dd' or 'yyyy-MM-ddTHH:mm:ss'")
    private String to;

    @ValidLanguage(message = "Language must be one of the following: ar, de, en, es, fr, he, it, nl, no, pt, ru, sv, ud, zh, or empty for all languages")
    private Language language;

    @ValidSortBy(message = "SortBy can only contain values from: relevancy, popularity, publishedAt")
    private SortBy sortBy;

    @Min(value = 1, message = "PageSize must be at least 1")
    @Max(value = 100, message = "PageSize must not exceed 100")
    private int pageSize = 20;

    @Min(value = 1, message = "Page must be at least 1")
    private int page = 1;

    private boolean user = false;
}
