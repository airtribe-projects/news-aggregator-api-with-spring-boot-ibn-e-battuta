package io.shinmen.airnewsaggregator.payload.request;

import io.shinmen.airnewsaggregator.model.enums.Category;
import io.shinmen.airnewsaggregator.model.enums.Country;
import io.shinmen.airnewsaggregator.payload.request.validator.annotation.ValidSource;
import io.shinmen.airnewsaggregator.payload.request.validator.annotation.ValidTopHeadlinesRequest;
import io.shinmen.airnewsaggregator.payload.request.validator.annotation.ValidCategory;
import io.shinmen.airnewsaggregator.payload.request.validator.annotation.ValidCountry;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@ValidTopHeadlinesRequest
public class TopHeadLinesQueryRequest {

    private String query;

    @ValidCategory(message = "Category must only contain values from: business, entertainment, general, health, science, sports, technology")
    private Category category;

    @ValidCountry(message = "Country must be one of the valid ISO country codes: ae, ar, at, au, be, bg, br, ca, ch, cn, co, cu,"
            + " cz, de, eg, fr, gb, gr, hk, hu, id, ie, il, in, it, jp, kr, lt, lv, ma, mx, my, ng, nl, no,"
            + " nz, ph, pl, pt, ro, rs, ru, sa, se, sg, si, sk, th, tr, tw, ua, us, ve, za or empty for all countries")
    private Country country;

    @ValidSource
    private String sources;

    @Min(value = 1, message = "Page must be at least 1")
    private int page = 1;

    @Min(value = 1, message = "PageSize must be at least 1")
    @Max(value = 100, message = "PageSize must not exceed 100")
    private int pageSize = 20;

    private boolean user = false;
}
