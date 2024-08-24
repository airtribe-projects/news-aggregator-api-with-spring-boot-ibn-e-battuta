package io.shinmen.airnewsaggregator.payload.response;

import io.shinmen.airnewsaggregator.model.enums.Category;
import lombok.Getter;
import lombok.Setter;

import java.util.Set;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class NewsPreferenceResponse {
    private String username;
    private String email;

    @JsonProperty
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private Set<Category> categories;

    @JsonProperty
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private Set<String> sources;
    private String country;
    private String language;
}
