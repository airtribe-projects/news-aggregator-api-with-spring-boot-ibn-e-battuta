package io.shinmen.airnewsaggregator.exception;

import org.springframework.http.HttpStatus;

import lombok.Getter;

@Getter
public class NewsApiException extends RuntimeException {

    private final String code;
    private final HttpStatus httpStatus;

    public NewsApiException(String code, String message, HttpStatus httpStatus) {
        super(message);
        this.code = code;
        this.httpStatus = httpStatus;
    }
}
