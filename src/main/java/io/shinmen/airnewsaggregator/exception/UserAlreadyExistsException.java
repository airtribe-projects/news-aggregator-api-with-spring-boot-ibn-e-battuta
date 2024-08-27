package io.shinmen.airnewsaggregator.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.CONFLICT)
public class UserAlreadyExistsException extends AirNewsException {
    public UserAlreadyExistsException(final String user) {
        super("User: " + user + " already exists");
    }
}
