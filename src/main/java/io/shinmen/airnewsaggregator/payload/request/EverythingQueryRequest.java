package io.shinmen.airnewsaggregator.payload.request;

import java.time.ZonedDateTime;

import io.shinmen.airnewsaggregator.model.enums.Language;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class EverythingQueryRequest {
    @NotBlank(message = "Query cannot be empty")
    private String query;
    private String sources;
    private String domains;

    private ZonedDateTime from;
    private ZonedDateTime to;

    private Language language;
    private String sortBy;

    @Min(1)
    @Max(100)
    private Integer pageSize;

    @Min(1)
    private Integer page;

    public EverythingQueryRequest() {
        page = 1;
        pageSize = 20;
    }
}
