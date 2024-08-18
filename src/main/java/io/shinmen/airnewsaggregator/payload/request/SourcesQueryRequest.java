package io.shinmen.airnewsaggregator.payload.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

public class SourcesQueryRequest {
    @NotNull(message = "Category cannot be null")
    @Pattern(
            regexp = "^(business|entertainment|general|health|science|sports|technology)$",
            message = "Category must be one of the following: business, entertainment, general, health, science, sports, technology, or empty for all categories"
    )
    private String category;

    @NotNull(message = "Language cannot be null")
    @Pattern(
            regexp = "^(ar|de|en|es|fr|he|it|nl|no|pt|ru|sv|ud|zh)$",
            message = "Language must be one of the following: ar, de, en, es, fr, he, it, nl, no, pt, ru, sv, ud, zh, or empty for all languages"
    )
    private String language;

    @NotNull(message = "Country cannot be null")
    @Pattern(
            regexp = "^(ae|ar|at|au|be|bg|br|ca|ch|cn|co|cu|cz|de|eg|fr|gb|gr|hk|hu|id|ie|il|in|it|jp|kr|lt|lv|ma|mx|my|ng|nl|no|nz|ph|pl|pt|ro|rs|ru|sa|se|sg|si|sk|th|tr|tw|ua|us|ve|za)$",
            message = "Country must be one of the valid ISO country codes or empty for all countries"
    )
    private String country;
}
