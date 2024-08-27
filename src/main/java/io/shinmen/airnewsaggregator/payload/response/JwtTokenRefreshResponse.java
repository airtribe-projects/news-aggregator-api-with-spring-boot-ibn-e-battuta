package io.shinmen.airnewsaggregator.payload.response;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class JwtTokenRefreshResponse {
    private String accessToken;

    private String refreshToken;

    @Builder.Default
    private String tokenType = "Bearer";
}
