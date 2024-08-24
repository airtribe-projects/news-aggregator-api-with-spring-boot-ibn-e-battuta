package io.shinmen.airnewsaggregator.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class NewsArticleNotFoundException extends AirNewsException {
    public NewsArticleNotFoundException(String message) {
        super(message);
    }
}
