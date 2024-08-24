package io.shinmen.airnewsaggregator.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
public class InvalidCacheTypeException extends AirNewsException {
    public InvalidCacheTypeException(String cacheName) {
        super("Invalid cache type: " + cacheName);
    }
}
