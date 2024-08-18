package io.shinmen.airnewsaggregator.exception;

public class NewsArticleNotFoundException extends NewsAggregatorException {
    public NewsArticleNotFoundException(String message) {
        super(message);
    }
}
