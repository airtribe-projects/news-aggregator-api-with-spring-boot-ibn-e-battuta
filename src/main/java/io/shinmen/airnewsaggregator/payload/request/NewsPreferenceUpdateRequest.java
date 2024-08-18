package io.shinmen.airnewsaggregator.payload.request;

import java.util.Set;

import io.shinmen.airnewsaggregator.model.enums.Category;
import io.shinmen.airnewsaggregator.model.enums.Country;
import io.shinmen.airnewsaggregator.model.enums.Language;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class NewsPreferenceUpdateRequest {


    @Size(max = 10, message = "Maximum 10 categories allowed")
    private Set<Category> categories;

    @Size(max = 20, message = "Maximum 20 sources allowed")
    private Set<String> sources;

    @Pattern(regexp = "^[a-z]{2}$", message = "Country must be exactly 2 uppercase letters")
    private String country;

    @Pattern(regexp = "^[a-z]{2}$", message = "Language must be exactly 2 uppercase letters")
    private String language;
}
