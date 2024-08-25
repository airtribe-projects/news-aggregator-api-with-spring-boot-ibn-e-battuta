package io.shinmen.airnewsaggregator.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.UNPROCESSABLE_ENTITY)
public class UserAlreadyVerifiedException extends AirNewsException {
    public UserAlreadyVerifiedException(String message) {
        super(message);
    }
}
