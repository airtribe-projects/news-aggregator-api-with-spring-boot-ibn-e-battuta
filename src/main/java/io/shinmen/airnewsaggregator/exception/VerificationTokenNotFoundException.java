package io.shinmen.airnewsaggregator.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class VerificationTokenNotFoundException extends AirNewsException {
    public VerificationTokenNotFoundException(final String token) {
        super("Verification token: " + token + " was not found");
    }
}
