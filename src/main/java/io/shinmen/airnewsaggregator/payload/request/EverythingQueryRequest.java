package io.shinmen.airnewsaggregator.payload.request;

import io.shinmen.airnewsaggregator.model.enums.Language;
import io.shinmen.airnewsaggregator.payload.request.enums.SortBy;
import io.shinmen.airnewsaggregator.payload.request.validator.ValidDateRange;
import io.shinmen.airnewsaggregator.payload.request.validator.ValidLanguage;
import io.shinmen.airnewsaggregator.payload.request.validator.ValidSortBy;
import io.shinmen.airnewsaggregator.payload.request.validator.ValidZonedDateTimeFormat;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@ValidDateRange(message = "The 'from' date must be earlier than or equal to the 'to' date")
public class EverythingQueryRequest {

    @NotBlank(message = "Query cannot be empty")
    private String query;

    private String sources;

    private String domains;

    @ValidZonedDateTimeFormat(message = "Invalid 'from' date format. Use 'yyyy-MM-dd' or 'yyyy-MM-ddTHH:mm:ss'")
    private String from;

    @ValidZonedDateTimeFormat(message = "Invalid 'to' date format. Use 'yyyy-MM-dd' or 'yyyy-MM-ddTHH:mm:ss'")
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
}
