package io.shinmen.airnewsaggregator.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class PreferenceNotFoundException extends AirNewsException {
    public PreferenceNotFoundException(final String user) {
        super("Preference for user: " + user + " was not found");
    }
}
