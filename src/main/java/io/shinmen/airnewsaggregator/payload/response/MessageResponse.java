package io.shinmen.airnewsaggregator.payload.response;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class MessageResponse {
    private String message;
}
