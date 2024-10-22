package io.shinmen.airnewsaggregator.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.UNAUTHORIZED)
public class RefreshTokenExpiredException extends AirNewsException {
    public RefreshTokenExpiredException(final String token) {
        super("Verification token: " + token + " has expired");
    }
}
