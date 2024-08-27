package io.shinmen.airnewsaggregator.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class RefreshTokenNotFoundException extends AirNewsException {
    public RefreshTokenNotFoundException(final String token) {
        super("Refresh token: " + token + " was not found");
    }
}
