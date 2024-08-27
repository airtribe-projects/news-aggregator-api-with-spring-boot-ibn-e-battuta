package io.shinmen.airnewsaggregator.payload.request;

import jakarta.validation.constraints.NotBlank;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@RequiredArgsConstructor
public class TokenRefreshRequest {
    @NotBlank
    private final String token;
}
