package io.shinmen.airnewsaggregator.exception;

public class AirNewsException extends RuntimeException {
    public AirNewsException(String message) {
        super(message);
    }

    public AirNewsException(String message, Throwable cause) {
        super(message, cause);
    }
}
