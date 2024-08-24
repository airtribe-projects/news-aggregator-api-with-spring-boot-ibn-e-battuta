package io.shinmen.airnewsaggregator.payload.response.newsapi;

import com.fasterxml.jackson.annotation.JsonInclude;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class NewsApiSourceResponse {

    @NotBlank(message = "Source ID cannot be blank")
    private String id;

    @NotBlank(message = "Source name is mandatory")
    private String name;
}
