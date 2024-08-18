package io.shinmen.airnewsaggregator.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class NewsPreferencesNotFoundException extends NewsAggregatorException {
    public NewsPreferencesNotFoundException(String string) {
        super(string);
    }
}
