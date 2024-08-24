package io.shinmen.airnewsaggregator.payload.request;

import java.util.Set;

import io.shinmen.airnewsaggregator.model.enums.Category;
import io.shinmen.airnewsaggregator.model.enums.Country;
import io.shinmen.airnewsaggregator.model.enums.Language;
import io.shinmen.airnewsaggregator.payload.request.validator.ValidCategories;
import io.shinmen.airnewsaggregator.payload.request.validator.ValidCountry;
import io.shinmen.airnewsaggregator.payload.request.validator.ValidLanguage;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class NewsPreferenceUpdateRequest {

        @Size(max = 10, message = "Maximum 10 categories allowed")
        @ValidCategories(message = "Categories must only contain values from: business, entertainment, general, health, science, sports, technology")
        private Set<Category> categories;

        @Size(max = 20, message = "Maximum 20 sources allowed")
        private Set<String> sources;

        @ValidCountry(message = "Country must be one of the valid ISO country codes: ae, ar, at, au, be, bg, br, ca, ch, cn, co, cu,"
                        + " cz, de, eg, fr, gb, gr, hk, hu, id, ie, il, in, it, jp, kr, lt, lv, ma, mx, my, ng, nl, no,"
                        + " nz, ph, pl, pt, ro, rs, ru, sa, se, sg, si, sk, th, tr, tw, ua, us, ve, za or empty for all countries")
        private Country country;

        @ValidLanguage(message = "Language must be one of the following: ar, de, en, es, fr, he, it, nl, no, pt, ru, sv, ud, zh, or empty for all languages")
        private Language language;
}
