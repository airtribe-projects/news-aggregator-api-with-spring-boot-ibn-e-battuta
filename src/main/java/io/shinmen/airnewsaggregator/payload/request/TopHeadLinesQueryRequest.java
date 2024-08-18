package io.shinmen.airnewsaggregator.payload.request;

import io.shinmen.airnewsaggregator.model.enums.Category;
import io.shinmen.airnewsaggregator.model.enums.Country;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TopHeadLinesQueryRequest {

    private String query;

    private Category category;

    private Country country;

    private String sources;

    @Min(value = 1, message = "Page must be at least 1")
    private Integer page;

    @Min(value = 1, message = "PageSize must be at least 1")
    @Max(value = 100, message = "PageSize must not exceed 100")
    private Integer pageSize;

    private Boolean useSources;

    public TopHeadLinesQueryRequest() {
        this.page = 1;
        this.pageSize = 20;
        this.useSources = false;
    }
}
