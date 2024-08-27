package io.shinmen.airnewsaggregator.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class VerificationTokenExpiredException extends AirNewsException {
    public VerificationTokenExpiredException(final String token) {
        super("Verification token: " + token + " has expired");
    }
}
