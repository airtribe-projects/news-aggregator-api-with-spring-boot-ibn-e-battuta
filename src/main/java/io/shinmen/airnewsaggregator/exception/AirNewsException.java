package io.shinmen.airnewsaggregator.exception;

public class AirNewsException extends RuntimeException {
    public AirNewsException(final String message) {
        super(message);
    }

    public AirNewsException(final String message, final Throwable cause) {
        super(message, cause);
    }
}
