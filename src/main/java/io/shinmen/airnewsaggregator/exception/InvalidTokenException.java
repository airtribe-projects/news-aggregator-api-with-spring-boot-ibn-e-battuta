package io.shinmen.airnewsaggregator.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class InvalidTokenException extends AirNewsException {
    public InvalidTokenException(final String token) {
        super("Token: " + token + " is invalid");
    }
}
