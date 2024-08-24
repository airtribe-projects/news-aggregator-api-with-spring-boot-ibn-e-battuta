package io.shinmen.airnewsaggregator.exception;

public class NewsArticleNotFoundException extends AirNewsException {
    public NewsArticleNotFoundException(String message) {
        super(message);
    }
}
