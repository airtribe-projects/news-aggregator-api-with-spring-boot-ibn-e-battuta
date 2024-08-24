package io.shinmen.airnewsaggregator.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class UserAlreadyVerifiedException extends AirNewsException {
    public UserAlreadyVerifiedException(String message) {
        super(message);
    }
}
