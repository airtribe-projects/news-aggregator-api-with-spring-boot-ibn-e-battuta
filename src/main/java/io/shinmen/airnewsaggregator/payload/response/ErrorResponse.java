package io.shinmen.airnewsaggregator.payload.response;

import java.time.ZonedDateTime;
import java.util.List;

import org.springframework.http.HttpStatus;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ErrorResponse {
    private HttpStatus status;
    private String message;
    private ZonedDateTime timestamp;
    private List<String> details;
}
