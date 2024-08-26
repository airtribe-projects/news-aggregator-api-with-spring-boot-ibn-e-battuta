package io.shinmen.airnewsaggregator.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class PreferencesNotFoundException extends AirNewsException {
    public PreferencesNotFoundException(String string) {
        super(string);
    }
}
