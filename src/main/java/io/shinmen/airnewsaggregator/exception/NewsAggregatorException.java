package io.shinmen.airnewsaggregator.exception;

public class NewsAggregatorException extends RuntimeException {
    public NewsAggregatorException(String message) {
        super(message);
    }

    public NewsAggregatorException(String message, Throwable cause) {
        super(message, cause);
    }
}
