package io.shinmen.airnewsaggregator.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class CacheNotFoundException extends AirNewsException {
    public CacheNotFoundException(String cacheName) {
        super("Cache not found: " + cacheName);
    }
}
