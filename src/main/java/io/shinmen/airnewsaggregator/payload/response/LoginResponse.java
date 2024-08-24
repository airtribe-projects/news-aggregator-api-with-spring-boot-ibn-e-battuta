package io.shinmen.airnewsaggregator.payload.response;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class LoginResponse {
    private Long id;
    private String username;
    private String email;
    private String token;
    private String refreshToken;
}
