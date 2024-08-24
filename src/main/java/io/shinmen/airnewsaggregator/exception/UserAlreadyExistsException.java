package io.shinmen.airnewsaggregator.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class UserAlreadyExistsException extends AirNewsException {
    public UserAlreadyExistsException(String message) {
        super(message);
    }
}
