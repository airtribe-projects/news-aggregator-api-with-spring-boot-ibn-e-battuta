package io.shinmen.airnewsaggregator.payload.response;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class VerificationTokenResponse {
    private String token;
}