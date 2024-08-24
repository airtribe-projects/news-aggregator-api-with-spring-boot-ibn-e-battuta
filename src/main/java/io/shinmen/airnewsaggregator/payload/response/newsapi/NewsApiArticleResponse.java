package io.shinmen.airnewsaggregator.payload.response.newsapi;

import java.time.ZonedDateTime;

import com.fasterxml.jackson.annotation.JsonInclude;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class NewsApiArticleResponse {

    @NotBlank(message = "Title is mandatory")
    @Size(max = 512, message = "Title cannot exceed 255 characters")
    private String title;

    @NotBlank(message = "Description is mandatory")
    @Size(max = 1024, message = "Description cannot exceed 500 characters")
    private String description;

    @NotBlank(message = "URL is mandatory")
    @Pattern(regexp = "^(https?|ftp)://[^\\s/$.?#].[^\\s]*$", message = "Invalid URL format")
    private String url;

    @Pattern(regexp = "^(https?|ftp)://[^\\s/$.?#].[^\\s]*$", message = "Invalid URL format for the image")
    private String urlToImage;

    private String author;

    @NotNull(message = "Published date is mandatory")
    @PastOrPresent(message = "Published date cannot be in the future")
    private ZonedDateTime publishedAt;

    private String content;

    @NotNull(message = "Source is mandatory")
    private NewsApiSourceResponse source;

    public void setSource(NewsApiSourceResponse source) {
        this.source = (source != null) ? source : new NewsApiSourceResponse("unknown", "Unknown Source");
    }
}
