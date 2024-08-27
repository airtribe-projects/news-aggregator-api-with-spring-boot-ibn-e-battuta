package io.shinmen.airnewsaggregator.payload.response;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class UserJwtResponse {
    private String jwt;

    private String username;

    private String email;
}