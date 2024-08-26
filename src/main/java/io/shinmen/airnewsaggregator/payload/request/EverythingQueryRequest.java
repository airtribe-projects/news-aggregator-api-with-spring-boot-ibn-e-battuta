package io.shinmen.airnewsaggregator.payload.request;

import io.shinmen.airnewsaggregator.model.enums.Language;
import io.shinmen.airnewsaggregator.payload.request.enums.SortBy;
import io.shinmen.airnewsaggregator.payload.request.validator.annotation.DateRange;
import io.shinmen.airnewsaggregator.payload.request.validator.annotation.Domain;
import io.shinmen.airnewsaggregator.payload.request.validator.annotation.Source;
import io.shinmen.airnewsaggregator.payload.request.validator.annotation.ValidLanguage;
import io.shinmen.airnewsaggregator.payload.request.validator.annotation.ValidSortBy;
import io.shinmen.airnewsaggregator.payload.request.validator.annotation.ValidZonedDateTime;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@DateRange
public class EverythingQueryRequest {

    @NotBlank(message = "Query cannot be empty")
    private String query;

    @Source
    private String sources;

    @Domain(message = "A domain must be valid and follow the format like 'example.com, example.org'")
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
    private Integer pageSize = 20;

    @Min(value = 1, message = "Page must be at least 1")
    private Integer page = 1;

    private boolean user = false;
}
