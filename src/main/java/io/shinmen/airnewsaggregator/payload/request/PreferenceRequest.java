package io.shinmen.airnewsaggregator.payload.request;

import java.util.Set;

import io.shinmen.airnewsaggregator.model.enums.Category;
import io.shinmen.airnewsaggregator.model.enums.Country;
import io.shinmen.airnewsaggregator.model.enums.Language;
import io.shinmen.airnewsaggregator.payload.request.validator.annotation.ValidCategories;
import io.shinmen.airnewsaggregator.payload.request.validator.annotation.ValidSources;
import io.shinmen.airnewsaggregator.payload.request.validator.annotation.ValidCountry;
import io.shinmen.airnewsaggregator.payload.request.validator.annotation.ValidLanguage;

import jakarta.validation.constraints.Size;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@RequiredArgsConstructor
public class PreferenceRequest {
    @Size(max = 10, message = "Maximum 10 categories allowed")
    @ValidCategories(message = "Categories must only contain values from: business, entertainment, general, health, science, sports, technology")
    private final Set<Category> categories;

    @ValidSources
    @Size(max = 20, message = "Maximum 20 sources allowed")
    private final Set<String> sources;

    @ValidCountry(message = "ValidCountry must be one of the valid ISO country codes: ae, ar, at, au, be, bg, br, ca, ch, cn, co, cu,"
            + " cz, de, eg, fr, gb, gr, hk, hu, id, ie, il, in, it, jp, kr, lt, lv, ma, mx, my, ng, nl, no,"
            + " nz, ph, pl, pt, ro, rs, ru, sa, se, sg, si, sk, th, tr, tw, ua, us, ve, za or empty for all countries")
    private final Country country;

    @ValidLanguage(message = "ValidLanguage must be one of the following: ar, de, en, es, fr, he, it, nl, no, pt, ru, sv, ud, zh, or empty for all languages")
    private final Language language;
}
