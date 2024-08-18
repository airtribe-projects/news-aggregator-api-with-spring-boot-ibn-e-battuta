package io.shinmen.airnewsaggregator.payload.response;

import io.shinmen.airnewsaggregator.model.enums.Category;
import lombok.Getter;
import lombok.Setter;

import java.util.Set;

@Getter
@Setter
public class NewsPreferenceResponse {
    private String username;
    private String email;
    private Set<Category> categories;
    private Set<String> sources;
    private String country;
    private String language;
}
