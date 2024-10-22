package io.shinmen.airnewsaggregator.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class ArticleNotFoundException extends AirNewsException {
    public ArticleNotFoundException(final String article) {
        super("Article: " + article + " was not found");
    }
}
