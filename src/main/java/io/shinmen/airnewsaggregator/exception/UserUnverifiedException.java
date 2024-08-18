package io.shinmen.airnewsaggregator.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class UserUnverifiedException extends NewsAggregatorException {
    public UserUnverifiedException(String message) {
        super(message);
    }
}
