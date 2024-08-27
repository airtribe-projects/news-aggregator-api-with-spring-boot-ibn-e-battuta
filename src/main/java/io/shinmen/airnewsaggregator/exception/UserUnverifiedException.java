package io.shinmen.airnewsaggregator.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.FORBIDDEN)
public class UserUnverifiedException extends AirNewsException {
    public UserUnverifiedException(final String user) {
        super("User" + user + " is not verified");
    }
}
